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
 *
 ***/


/***
 *
 *
 * PacketFactory.class, shared between all Ecobox Projects
 * 
 * Purpose: Pretty useless
 * 
 * 
 ***/

public class PacketFactory {
	
	public static Packet01RPiAuthentication new01RPiAuthentication(byte from, byte to, String key) throws PacketViolation {
		if(from != PacketSrc.fromRPi || to != PacketSrc.toServer)
			throw new PacketViolation("01RPiAuthentification: Unauthorized sender ("+from+") or receiver ("+to+").");
		
		Packet01RPiAuthentication RPiAuthPacket = new Packet01RPiAuthentication();
		
		RPiAuthPacket.authKey = key;
		RPiAuthPacket.type = (byte) (from | to);
		return RPiAuthPacket;
	}
	
	public static Packet02UserAuthentication new02UserAuthentication(byte from, byte to, boolean guest, String name, String pw) throws PacketViolation{

		if(from != PacketSrc.fromClient || to != PacketSrc.toServer)
			throw new PacketViolation("02UserAuthentication: Unauthorized sender or receiver");
		
		Packet02UserAuthentication p = new Packet02UserAuthentication();
		p.type = (byte) (from | to);
		p.guest = guest;
		p.username = name;
		p.password = pw;
		return p; //TODO additional checks etc
	}
	
	public static Packet03AuthConfirmation new03AuthConfirmation(byte from, byte to, boolean success){
		Packet03AuthConfirmation p = new Packet03AuthConfirmation();
		p.type = (byte) (from | to);
		p.success = success;
		return p;
	}
	
	public static Packet04RequestRPiMetadata new04RequestRPiMetadata(byte from, byte to, int flags) throws PacketViolation {
		
		if(from != PacketSrc.fromClient || to != PacketSrc.toServer)
			throw new PacketViolation("04RequestRPiMetadata: Unauthorized sender ("+from+") or receiver ("+to+").");
		
		Packet04RequestRPiMetadata p = new Packet04RequestRPiMetadata();
		
		p.type = (byte) (from | to);
		p.requestFlags = flags;
		return p;
	}
	
	public static Packet05RPiMetadata new05RPiMetadata(byte from, byte to, int nr, Integer[] IDs, Boolean[] Statuses, String[] Names, String[] IPs, String[][] Owners) throws PacketViolation{
		
		if(from != PacketSrc.fromServer || to != PacketSrc.toClient)
			throw new PacketViolation("05RPiMetadata: Unauthorized sender ("+from+") or receiver ("+to+").");
		
		Packet05RPiMetadata p = new Packet05RPiMetadata();
		
		p.type = (byte) (from | to);
		p.numResults = nr;
		p.IDs = IDs;
		p.Statuses = Statuses;
		p.Names = Names;
		p.IPs = IPs;
		p.Owners = Owners;
		return p;
	}
	
	public static Packet06OpenRPiStreamRequest new06OpenRPiStreamRequest(byte from, byte to, int id, String[] reqStreams) throws PacketViolation{
		if((from == PacketSrc.fromRPi) || (to == PacketSrc.toClient))
			throw new PacketViolation("o6OpenRPiStreamRequest: Unauthorized sender or receiver");
		
		Packet06OpenRPiStreamRequest p = new Packet06OpenRPiStreamRequest();
		
		p.type = (byte) (from | to);
		p.RPiID = id;
		p.requestedData = reqStreams;
		return p;
	}
	
	public static Packet07CloseRPiStreamRequest new07CloseRPiStreamRequest(byte from, byte to, int id, String[] reqStreams) throws PacketViolation{
		if((from == PacketSrc.fromRPi) || (to == PacketSrc.toClient))
			throw new PacketViolation("o7CloseRPiStreamRequest: Unauthorized sender or receiver");
		
		Packet07CloseRPiStreamRequest p = new Packet07CloseRPiStreamRequest();
		
		p.type = (byte) (from | to);
		p.RPiID = id;
		p.requestedData = reqStreams;
		return p;
	}

	public static Packet08RPiDataRequest new08RPiDataRequest(byte from, byte to, int RPiID, String[] rD) throws PacketViolation{
		
		if(from != PacketSrc.fromClient || to != PacketSrc.toServer)
			throw new PacketViolation("08RPiDataRequest: Unauthorized sender ("+from+") or receiver ("+to+").");
		
		Packet08RPiDataRequest p = new Packet08RPiDataRequest();
		
		p.type = (byte) (from | to);
		p.RPiID = RPiID;
		p.requestedData = rD;
		return p;
	}
	
	public static Packet09RPiData new09RPiData(byte from, byte to, String[] types, Object[] data, int id) throws PacketViolation{
		if((from != PacketSrc.fromRPi && from != PacketSrc.fromServer) || (to != PacketSrc.toServer && to != PacketSrc.toClient))
			throw new PacketViolation("09RPiData: Unauthorized sender ("+from+") or receiver ("+to+").");
		
		Packet09RPiData p = new Packet09RPiData();
		
		p.type = (byte) (from | to);
		p.dataTypes = types;
		p.data = data;
		p.RPiID = id;
		return p;
	}
	
	public static Packet403Forbidden new403Forbidden(byte from, byte to){
		Packet403Forbidden p = new Packet403Forbidden();
		p.type = (byte) (from | to);
		return p;
	}
}