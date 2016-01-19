/***
 * 
 * 
 * 
 * 
 * 
 *******************************************************************************************************************************************
 *                                                                                                                                         *
 *     /\    DISCLAIMER     UGLY, UN-OPTIMIZED, "ALPHA-PROTOTYPING" CODE                                                                   *
 *    /  \   DISCLAIMER     DO NOT READ FURTHER UNTIL YOU HAVE FOUND A CURE FOR EYE CANCER                                                 *
 *   / !! \  DISCLAIMER     #KAPPA                                                                                                         *
 *  /______\ DISCLAIMER     Seriously though. Don't judge, this was written in a rush and will be improved, revised, and refactored soon.  *
 *                                                                                                                                         *
 *******************************************************************************************************************************************
 *
 *
 *
 *
 * (I'll only warn you once)(Srsly this is one of the ugliest parts)
 ***/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class EcoboxRaspberryPiClient extends Listener {

	//Networking Client
	Client client;
	
	//RPi-unique Key
	String authenticationKey;
	
	//Connection to server as RPi successfully established?
	boolean connectionReady = false;
	
	//List of scripts
	ArrayList<DataScript> scripts = new ArrayList<DataScript>();
	
	
	//TODO start args
	public EcoboxRaspberryPiClient(String ip) throws Exception {
		
		//Setup & Start RPiClient, connect to server
		client = new Client();
		Network.registerPackets(client.getKryo());
	    client.start();
	    client.connect(5000, ip, Network.tcpPort, Network.udpPort);
	    client.addListener(this);
	    
	    
	    //Load RPi Key used to authenticate
	    //TODO load from file
	    authenticationKey = "test-test";
	    
	    //Try to authenticate as RPi.
	    tryAuth();
	    
	    //Setup scripts (Load etc, sort by priority) //TODO scripts can add their own classes to Network
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(new File("Scripts/LoadOrder.txt")));
	    	String line = "";
	    	ArrayList<String> toLoad = new ArrayList<String>();
	    	while((line = br.readLine()) != null){
	    		if(new File("Scripts/"+line+".properties").exists())
	    			toLoad.add(line);
	    		else
	    			Out.warn("[Scripts]", "Property file for script \""+line+"\" not found.");
	    	}
	    	br.close();
	    	Properties props = new Properties();
	    	for(int i = 0; i < toLoad.size(); i++){
	    		props.load(new FileInputStream(new File("Scripts/" + toLoad.get(i) + ".properties")));
		    	if(! Boolean.valueOf(props.getProperty("Enabled"))) continue;
		    	switch(((String)props.get("ScriptFile")).split("\\.")[1]){
		    	case "py":
		    		scripts.add(new PyDataScript(props.getProperty("Name"), Long.valueOf(props.getProperty("NormalInterval")), PROTOCOL.valueOf(props.getProperty("NormalProtocol")), PROTOCOL.valueOf(props.getProperty("StreamProtocol")), i, props.getProperty("ScriptFile").split("\\.")[0]));
		    		break;
		    		
		    	default:
		    		break;
		    	}
		    	Out.info("[Scripts]", props.getProperty("Name") + " loaded.");
	    	}
	    } catch(Exception e){e.printStackTrace();}
	    
	    /*Properties props = new Properties();
	    for(File f : new File("Scripts/").listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".properties"));
			}
		})){
	    	props.load(new FileInputStream(f));
	    	if(! Boolean.valueOf(props.getProperty("Enabled"))) continue;
	    	switch(((String)props.get("ScriptFile")).split("\\.")[1]){
	    	case "py":
	    		scripts.add(new PyDataScript(props.getProperty("Name"), Long.valueOf(props.getProperty("NormalInterval")), PROTOCOL.valueOf(props.getProperty("NormalProtocol")), PROTOCOL.valueOf(props.getProperty("StreamProtocol")), Integer.valueOf(props.getProperty("Priority")), props.getProperty("ScriptFile").split("\\.")[0]));
	    		break;
	    		
	    	default:
	    		break;
	    	}
	    }
	    */
	    
	    Collections.sort(scripts);
	    
	    //Start running data-retrieving scripts: One thread for UDP-Packets (Streaming), one for TCP-Packets (Normal data retrieving)
	    Runnable udpScriptLoop = new Runnable() {
			public void run() {
				while(true){
			    	//If the connection isn't ready yet, abort
			    	if(!connectionReady) continue;
			    	
			    	//Send UDP Packets ( = Streaming)
			    	for(DataScript script : scripts){
			    		//For ever streaming script...
			    		if(script.currentlyStreaming)
							try {
								client.sendUDP(PacketFactory.new09RPiData(PacketSrc.fromRPi, PacketSrc.toServer, new String[]{script.NAME}, new Object[]{script.retrieveData()}, 0));
							} catch (PacketViolation e) {
								e.printStackTrace();
							}
			    	}
			    }
			}
		};
		Runnable tcpScriptLoop = new Runnable() {
			public void run() {
				long old = System.nanoTime()/1000000;
				while(true){
			    	//If the connection isn't ready yet, abort
			    	if(!connectionReady) continue;
			    	
			    	//Send TCP Packets
			    	for(DataScript script : scripts){
			    		//Send data over TCP.
			    		if(!script.currentlyStreaming && script.update((System.nanoTime()/1000000) - old))
							try {
								client.sendTCP(PacketFactory.new09RPiData(PacketSrc.fromRPi, PacketSrc.toServer, new String[]{script.NAME}, new Object[]{script.retrieveData()}, 0));
							} catch (PacketViolation e) {
								e.printStackTrace();
							}
			    	}
			    }
			}
		};
		Thread tcpScriptThread = new Thread(tcpScriptLoop);
		Thread udpScriptThread = new Thread(udpScriptLoop);
		tcpScriptThread.setPriority(Thread.NORM_PRIORITY);
		udpScriptThread.setPriority(Thread.MAX_PRIORITY);
		tcpScriptThread.start();
		udpScriptThread.start();
	    
	}
	
	public void tryAuth() throws PacketViolation {
		Packet01RPiAuthentication p = PacketFactory.new01RPiAuthentication(PacketSrc.fromRPi, PacketSrc.toServer, authenticationKey);
	    client.sendTCP(p);
	    Out.info("Packet", "Sent 01RPiAuthentication to Server");
	}
	
	public void received(Connection c, Object o){
		try{
			if(o instanceof Packet){
				if(((Packet)o).type != (PacketSrc.fromServer | PacketSrc.toRPi)){
					Out.info("Packet", "Received & Abort: Wrong sender/receiver.");
					return;
				}
			}
			if(o instanceof Packet03AuthConfirmation){
				Out.info("Packet", "Received 03RAuthConfirmation");
				
				Packet03AuthConfirmation p = (Packet03AuthConfirmation) o;
				
				connectionReady = p.success;
				
				if(!connectionReady){
					Out.info("Packet", "Authentication failed. Retry...");
					tryAuth();
					return;
				}
				Out.info("Packet", "Authentication successful. Sending data now allowed.");
			}
			if(o instanceof Packet06OpenRPiStreamRequest){
				Out.info("Packet", "Received 06OpenRPiStreamRequest");
				
				Packet06OpenRPiStreamRequest p = (Packet06OpenRPiStreamRequest) o;
				
				List<String> reqData = Arrays.asList(p.requestedData);
				for(DataScript script : scripts)
					if(reqData.contains(script.NAME)) script.currentlyStreaming = true;
			}
			if(o instanceof Packet07CloseRPiStreamRequest){
				Out.info("Packet", "Received 07CloseRPiStreamRequest");
				
				Packet07CloseRPiStreamRequest p = (Packet07CloseRPiStreamRequest) o;
				
				List<String> reqData = Arrays.asList(p.requestedData);
				for(DataScript script : scripts)
					if(reqData.contains(script.NAME)) script.currentlyStreaming = false;
			}
		}catch(PacketViolation e){
			
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		new EcoboxRaspberryPiClient(args[0]);
	}
	
}
