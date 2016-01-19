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
 * (I'll only warn you once)
 ***/


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class EcoboxServer extends Listener {

	Server server;

	ArrayList<RPiClient> RPiClients = new ArrayList<RPiClient>();
	ArrayList<User> userClients = new ArrayList<User>();
	int guestCount = 0;
	
	public EcoboxServer() throws Exception {
		Out.info("Starting Server Application");
		
		//Setup Database
		
		Out.info("Database started");
		
		//Load RPi-Metadata
		RPiClient c1 = new RPiClient("test-test", 1, "DummyClient1", new ArrayList<Integer>(), new ArrayList<Integer>(), true);
		RPiClient c2 = new RPiClient("justforthelolz", 2, "IDontExistLol", new ArrayList<Integer>(), new ArrayList<Integer>(), true);
		RPiClient c3 = new RPiClient("ahahah", 3, "PrivateRPi", new ArrayList<Integer>(), new ArrayList<Integer>(), false);
		RPiClients.add(c1);
		RPiClients.add(c2);
		RPiClients.add(c3);
		
		Out.info("RaspberryPi Metadata loaded");
		
		//Load User-Metadata
		
		Out.info("User Metadata loaded");
		
		//Setup & Start server
		server = new Server() {};
		Network.registerPackets(server.getKryo());
		server.addListener(this);
		server.bind(Network.tcpPort, Network.udpPort);
		server.start();
		
		Out.info("Server configured and started. Listening...");
	}
	
	public void connected(Connection c){
		Out.info("New connection (" + c.getID() + ") from " + c.getRemoteAddressTCP().getAddress().getHostAddress());
	}
	
	public void disconnected(Connection c){
		
		User u = User.findByConnection(userClients, c.getID());
		RPiClient RPi = RPiClient.findByConnection(RPiClients, c.getID());
		if(u != null){
			u.con = null;
			
			//Unregister from streams.
			for(RPiClient rpi : RPiClients) for(List<Integer> l : rpi.dataListeners.values()) if(l.contains((Integer)u.id)) l.remove((Integer)u.id);
			
			Out.info("User " + u.name + " (" + u.id + ") disconnected.");
		}else if(RPi != null){
			RPi.con = null;
			
			Out.info("RPi " + RPi.NAME + " (" + RPi.ID + ") disconnected.");
		}else{
			Out.info("Disconnection");
		}
	}
	
	public void received(Connection c, Object o){
		
		try{
			
		
		//RPi requests authentification
		if(o instanceof Packet01RPiAuthentication){
			Out.info("Packet", "Received 01RPiAuthentication");
			
			Packet01RPiAuthentication p = (Packet01RPiAuthentication) o;
			
			boolean success = true;
			
			//Check if the authentification is valid.
			RPiClient RPi = RPiClient.findByKey(RPiClients, p.authKey);
			if(p.type != (PacketSrc.fromRPi | PacketSrc.toServer) || RPi == null || RPi.con != null){
				success = false;
				Out.info("Packet", "Authentification unsuccessful.");
			}else{
				//Recognize Connection as RPi
				RPi.con = c;
				
				Out.info("Packet", "Authentification successful.");
			}
			
			//Send back confirming packet
			server.sendToTCP(c.getID(), PacketFactory.new03AuthConfirmation(PacketSrc.fromServer, PacketSrc.toRPi, success));
			Out.info("Packet", "Sent 03AuthConfirmation to RPi"+c.getID());
		}
		
		//User-Client requests authentification
		else if(o instanceof Packet02UserAuthentication){
			Out.info("Packet", "Received 02UserAuthentication");
			
			Packet02UserAuthentication p = (Packet02UserAuthentication) o;
			
			boolean success = true;
			
			if(p.type != (PacketSrc.fromClient | PacketSrc.toServer) || (User.findByConnection(userClients, c.getID()) != null && User.findByConnection(userClients, c.getID()).con != null)){
				
				System.out.println("FALSERINO");
				success = false;
			}
			//todo: add additional checks, exceptions, etc
			else if(p.guest){
				//Recognize Connection as User
				if(false){
					success = false;
				}else{
					User u = User.createGuest(p.username, guestCount);
					userClients.add(u);
					u.con = c;
					guestCount++;
					Out.info("User", "Guest \"" + u.name + "\" connected");
				}
			}else{
				//check user with database...
				
				//if failed:
				success = false;
			}
			//Send back confirming packet
			server.sendToTCP(c.getID(), PacketFactory.new03AuthConfirmation(PacketSrc.fromServer, PacketSrc.toClient, success));
			Out.info("Packet", "Sent 03AuthConfirmation");
			return;
		}
		
		//User-Client requests RPi Metadata.//TODO more security. user needs to be allowed to access that RPi etc
		else if(o instanceof Packet04RequestRPiMetadata && User.findByConnection(userClients, c.getID()) != null){
			Packet04RequestRPiMetadata p = (Packet04RequestRPiMetadata) o;
			
			if(p.type != (PacketSrc.fromClient | PacketSrc.toServer)){
				server.sendToTCP(c.getID(), PacketFactory.new403Forbidden(PacketSrc.fromServer, PacketSrc.toClient));
				return;
			}
			
			//put results in packet and send
			int numResults = 0;
			ArrayList<Integer> IDs = new ArrayList<Integer>();
			ArrayList<String> Names = new ArrayList<String>();
			ArrayList<Boolean> Statuses = new ArrayList<Boolean>();
			ArrayList<String> IPs = new ArrayList<String>();
			ArrayList<String[]> Owners = new ArrayList<String[]>();
			User u = User.findByConnection(userClients, c.getID());
			for(RPiClient RPi : RPiClients){
				if(RPi.publicData || u.userGroup == 2 || RPi.owners.contains(u.id) || RPi.members.contains(u.id)){
					numResults++;
					if((p.requestFlags & 1) == 1) IDs.add(RPi.ID);
					if((p.requestFlags & 2) == 2) Names.add(RPi.NAME);
					if((p.requestFlags & 4) == 4) Statuses.add(RPi.con==null?false:true);
					if((p.requestFlags & 8) == 8) IPs.add(RPi.con.getRemoteAddressTCP().getAddress().getHostAddress());
					if((p.requestFlags & 16) == 16){
						ArrayList<String> subOwners = new ArrayList<String>();
						for(Integer i : RPi.owners) subOwners.add(User.findByID(userClients, i).name);
						if(RPi.owners.size() == 0) subOwners.add("(No owners)");
						Owners.add(subOwners.toArray(new String[subOwners.size()]));
					}
				}
			}
			
			server.sendToTCP(c.getID(), PacketFactory.new05RPiMetadata(PacketSrc.fromServer, PacketSrc.toClient, numResults, IDs.toArray(new Integer[IDs.size()]), Statuses.toArray(new Boolean[Statuses.size()]), Names.toArray(new String[Names.size()]), IPs.toArray(new String[IPs.size()]), Owners.toArray(new String[Owners.size()][])));
		}
		
		else if(o instanceof Packet06OpenRPiStreamRequest){
			Packet06OpenRPiStreamRequest p = (Packet06OpenRPiStreamRequest) o;
			
			User u = User.findByConnection(userClients, c.getID());
			RPiClient RPi = RPiClient.findByID(RPiClients, p.RPiID);
			if(p.type != (PacketSrc.fromClient | PacketSrc.toServer) || RPi == null || u == null || u.con == null || (!RPi.publicData && u.userGroup != 2 && !RPi.owners.contains(u.id) && !RPi.members.contains(u.id))){
				server.sendToTCP(c.getID(), PacketFactory.new403Forbidden(PacketSrc.fromServer, PacketSrc.toClient));
				return;
			}
			
			for(String dt : p.requestedData){
				if(RPi.dataListeners.containsKey(dt)) RPi.dataListeners.get(dt).add(u.id);
				else RPi.dataListeners.put(dt, Arrays.asList(u.id));
			}
			
			p.type = (byte) (PacketSrc.fromServer | PacketSrc.toRPi);
			p.requestedData = RPi.dataListeners.keySet().toArray(new String[RPi.dataListeners.keySet().size()]);
			server.sendToTCP(RPi.con.getID(), p);
		}
		
		else if(o instanceof Packet07CloseRPiStreamRequest){
			Packet07CloseRPiStreamRequest p = (Packet07CloseRPiStreamRequest) o;
			
			User u = User.findByConnection(userClients, c.getID());
			RPiClient RPi = RPiClient.findByID(RPiClients, p.RPiID);
			if(p.type != (PacketSrc.fromClient | PacketSrc.toServer) || RPi == null || u == null || u.con == null || (!RPi.publicData && u.userGroup != 2 && !RPi.owners.contains(u.id) && !RPi.members.contains(u.id))){
				server.sendToTCP(c.getID(), PacketFactory.new403Forbidden(PacketSrc.fromServer, PacketSrc.toClient));
				return;
			}
			
			ArrayList<String> streams2Close = new ArrayList<String>();
			
			for(String dt : p.requestedData){
				if(RPi.dataListeners.containsKey(dt)){
					ArrayList<Integer> usrs = new ArrayList<Integer>(RPi.dataListeners.get(dt));
					usrs.remove((Integer) u.id);
					RPi.dataListeners.put(dt, usrs);
					if(RPi.dataListeners.get(dt).size() == 0) streams2Close.add(dt);
				}
			}
			
			p.type = (byte) (PacketSrc.fromServer | PacketSrc.toRPi);
			p.requestedData = streams2Close.toArray(new String[streams2Close.size()]);
			server.sendToTCP(RPi.con.getID(), p);
		}
		
		else if(o instanceof Packet08RPiDataRequest && User.findByConnection(userClients, c.getID()) != null){
			Packet08RPiDataRequest p = (Packet08RPiDataRequest) o;
			
			User u = User.findByConnection(userClients, c.getID());
			RPiClient RPi = RPiClient.findByID(RPiClients, p.RPiID);
			if(p.type != (PacketSrc.fromClient | PacketSrc.toServer) || RPi == null || (!RPi.publicData && u.userGroup != 2 && !RPi.owners.contains(u.id) && !RPi.members.contains(u.id))){
				server.sendToTCP(c.getID(), PacketFactory.new403Forbidden(PacketSrc.fromServer, PacketSrc.toClient));
				return;
			}
			
			ArrayList<String> datatypes = new ArrayList<String>();
			ArrayList<Object> data = new ArrayList<Object>();
			for(String datatype : p.requestedData){
				if(RPi.data.containsKey(datatype) && RPi.data.get(datatype) != null){
					data.add(RPi.data.get(datatype));
					datatypes.add(datatype);
				}else{ //TODO optional...
					data.add("E404");
					datatypes.add(datatype);
				}
			}
			server.sendToTCP(c.getID(), PacketFactory.new09RPiData(PacketSrc.fromServer, PacketSrc.toClient, datatypes.toArray(new String[datatypes.size()]), data.toArray(new Object[data.size()]), p.RPiID));
		}
		
		else if(o instanceof Packet09RPiData){
			Out.info("Packet", "Received 09RPiData");
			
			Packet09RPiData p = (Packet09RPiData) o;
			
			if(p.type != (PacketSrc.fromRPi | PacketSrc.toServer)){
				server.sendToTCP(c.getID(), PacketFactory.new403Forbidden(PacketSrc.fromServer, PacketSrc.toRPi));
				return;
			}
			
			//TODO: Save data if not streaming. send further if streaming. also add listener list
			RPiClient rpi = RPiClient.findByConnection(RPiClients, c.getID());
			for(int i = 0; i < p.dataTypes.length; i++){
				if(rpi.dataListeners.containsKey(p.dataTypes[i]) && rpi.dataListeners.get(p.dataTypes[i]).size() > 0){
					for (int userID : rpi.dataListeners.get(p.dataTypes[i])){
						User u = User.findByID(userClients, userID);
						if(u != null && u.con != null) server.sendToUDP(u.con.getID(), p.data[i]);
					}
				}else{
					//TODO: save
					for(int ii = 0; ii < p.dataTypes.length; ii ++){
						//System.out.println(p.dataTypes[ii] + ": " + p.data[ii]);
					}
				}
				rpi.data.put(p.dataTypes[i], p.data[i]);
			}
			
		}
		
		
		
		else {
			//TODO: Log unknown packet
		}
		
		}catch(PacketViolation e){
			
		}
	}
	
	
	public static void main(String [] args) throws Exception{
		EcoboxServer server = new EcoboxServer();
		server.toString();
	}
}
