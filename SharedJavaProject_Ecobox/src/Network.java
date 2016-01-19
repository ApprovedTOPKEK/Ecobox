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
 * Network.class, shared between all Ecobox Projects
 * 
 * Purpose: Contains Server Information + Registers classes to be sent over network for serialization
 * 
 * 
 ***/

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;


public class Network {
	
	public static final String ServerIP = "localhost";
	public static final int tcpPort = 54555, udpPort = 54777;
	
	public static void registerPackets(Kryo k){
		k.register(String.class);
		k.register(Integer.class);
		k.register(Byte.class);
		k.register(String[].class);
		k.register(String[][].class);//TODO: etc...
		k.register(Boolean.class);
		k.register(ArrayList.class);
		k.register(Object.class);
		k.register(Object[].class);
		k.register(Integer[].class);
		k.register(Boolean[].class);
		k.register(int[][].class);
		k.register(int[].class);
		
		k.register(Packet.class);
		k.register(Packet01RPiAuthentication.class);
		k.register(Packet02UserAuthentication.class);
		k.register(Packet03AuthConfirmation.class);
		k.register(Packet04RequestRPiMetadata.class);
		k.register(Packet05RPiMetadata.class);
		k.register(Packet06OpenRPiStreamRequest.class);
		k.register(Packet07CloseRPiStreamRequest.class);
		k.register(Packet08RPiDataRequest.class);
		k.register(Packet09RPiData.class);
	}

}
