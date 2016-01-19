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
 * Packet.class, shared between all Ecobox Projects
 * 
 * Purpose: Those things are the stuff sent over network. Whatever
 * 
 * 
 ***/

public abstract class Packet {

	/**
	 * See PacketSrc
	 */
	public byte type;
	
}

/**
 * Packet sent from RPi to Server on startup. Also sent back to RPi from Server to confirm authentication.
 */
class Packet01RPiAuthentication extends Packet {
	/**
	 * Authentication Key unique to each RPi. Server should check if the Key is valid, and if yes, put this specific connection into the RPi List.
	 */
	public String authKey;
}

class Packet02UserAuthentication extends Packet {
	/**
	 * Authentication data
	 */
	public String username, password;
	public boolean guest = false;
}

class Packet03AuthConfirmation extends Packet {
	public boolean success;
}

class Packet04RequestRPiMetadata extends Packet {
	/**
	 * Data-types requested. Bitmask used for faster networking.
	 * RequestIDs = 1 (ID used to fetch RPi data)
	 * RequestNames = 2 (Given raspberry pi name)
	 * RequestStatus = 4 (Online/Offline)
	 * RequestIP = 8
	 * RequestOwners = 16
	 */
	public int requestFlags = 0;
}

class Packet05RPiMetadata extends Packet {
	//RPi Metadata returned. One per RPi
	public int numResults;
	
	public Integer[] IDs;
	public Boolean[] Statuses;
	public String[] Names;
	public String[] IPs;
	public String[][] Owners;
}

class Packet06OpenRPiStreamRequest extends Packet {
	public int RPiID;
	public String[] requestedData;
}

class Packet07CloseRPiStreamRequest extends Packet {
	public int RPiID;
	public String[] requestedData;
}

class Packet08RPiDataRequest extends Packet {
	public int RPiID;
	public String[] requestedData;
}

class Packet09RPiData extends Packet {
	public int RPiID;
	public String[] dataTypes;
	public Object[] data;
}

class Packet403Forbidden extends Packet {}

class Packet10VideoPartStart {
	
}
