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
 * (Don't even try to look at this)
 ***/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class EcoboxUserClient extends Listener implements ViewDataLayer {

	Client client;
	UserClientView view;
	Set<RPi> RPis = new HashSet<RPi>();
	
	//Step 0: Wait for user input
	//Step 1: Send Auth Packet
	//Step 2: Wait for Response
	//Step 3: Goto (1) if failed, goto (4) if success
	//Step 4: Send RPiMetadataRequest Packet
	//Step 5: Show results to user, wait for click
	//Step 6: Request RPi Info
	//Step 7: Update every second or so
	
	public boolean authenticated = false;
	
	public String[] requests;
	
	public EcoboxUserClient() throws Exception {
		client = new Client();
		Network.registerPackets(client.getKryo());
	    client.start();
	    client.connect(5000, Network.ServerIP, Network.tcpPort, Network.udpPort);
	    client.addListener(this);
	 
	    //Setup requests... TODO only beta of course
	    BufferedReader br = new BufferedReader(new FileReader(new File("UCR.txt")));
	    requests = br.readLine().split(",");
	    br.close();
	    
	    view = new UserClientView(this);
	    view.setListener(this);
	   
	}
	
	public void received(Connection c, Object o){
		
		try{
			if(o instanceof Packet){
				if(((Packet)o).type != (PacketSrc.fromServer | PacketSrc.toClient)){
					Out.info("Packet", "Received & Abort: Wrong sender/receiver.");
					return;
				}
			}
			
			if(o instanceof Packet03AuthConfirmation){
				Out.info("Packet", "03AuthConfirmation received.");
				
				Packet03AuthConfirmation p = (Packet03AuthConfirmation) o;
				System.out.println(p.success);
				if(p.success){
					authenticated = true;
					view.view(Views.LOADING);
					client.sendTCP(PacketFactory.new04RequestRPiMetadata(PacketSrc.fromClient, PacketSrc.toServer, 1|2|4|16));
				}else{
					authenticated = false;
					//view.popup("Authentication error, try again");
				}
			}
			
			if(o instanceof Packet05RPiMetadata){
				Out.info("Packet", "05RPiMetadata received.");
				
				Packet05RPiMetadata p = (Packet05RPiMetadata) o;
				
				for(int i = 0; i < p.numResults; i++){
					RPis.add(new RPi(p.IDs[i], p.Names[i], p.Statuses[i], p.Owners[i]));
				}
				view.view(Views.LIST);
			}
			
			if(o instanceof Packet09RPiData){
				Packet09RPiData p = (Packet09RPiData) o;
				
				//for(int i = 0; i < p.dataTypes.length; i++) System.out.println(p.dataTypes[i] + ": " + p.data[i]);
				RPi rpi = RPi.findByID(RPis, p.RPiID);
				for(int i = 0; i < p.dataTypes.length; i++) rpi.data.put(p.dataTypes[i], p.data[i]);
				
				view.refresh(Views.DETAILED);
			}
			
		}catch(PacketViolation e){
			
		}
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		new EcoboxUserClient();
	}

	@Override
	public boolean submitLogin(String name, String password, boolean guest) { //TODO checks
		try {
			client.sendTCP(PacketFactory.new02UserAuthentication(PacketSrc.fromClient, PacketSrc.toServer, guest, name, password));
		} catch (PacketViolation e) {
			e.printStackTrace();
		} //TODO Md5
		return true;
	}

	@Override
	public void openRPi(int id) {
		try {
			client.sendTCP(PacketFactory.new08RPiDataRequest(PacketSrc.fromClient, PacketSrc.toServer, id, requests));
			crpi = id;
			view.view(Views.DETAILED);
			Thread t = new Thread(new Runnable(){
				public void run(){
					while(true){
						try {
							client.sendTCP(PacketFactory.new08RPiDataRequest(PacketSrc.fromClient, PacketSrc.toServer, id, requests));
							Thread.sleep(1000);
						} catch (PacketViolation | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			t.start();
		} catch (PacketViolation e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] beta_reqs(){
		return requests;
	}
	@Override
	public Set<RPi> getData() {
		return RPis;
	}
	int crpi = -1;
	@Override
	public int currentRPi(){
		return crpi;
	}
	
	@Override
	public void stream(String s, boolean b){
		if(b)
			try {
				client.sendTCP(PacketFactory.new06OpenRPiStreamRequest(PacketSrc.fromClient, PacketSrc.toServer, crpi, new String[]{s}));
			} catch (PacketViolation e) {
				e.printStackTrace();
			}
		else
			try {
				client.sendTCP(PacketFactory.new07CloseRPiStreamRequest(PacketSrc.fromClient, PacketSrc.toServer, crpi, new String[]{s}));
			} catch (PacketViolation e) {
				e.printStackTrace();
			}
	}
	
}
