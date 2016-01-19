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
 * PacketSrc.class, shared between all Ecobox Projects
 * 
 * Purpose: Pretty useless (Quick'n'dirty 5minute attempt at establishing a lil' more security against hacks)
 * 
 * 
 ***/

public class PacketSrc {

	/**
	 * Bitmap specifying packet sender and receiver types.
	 * 00 00 00 01 = From Server. 
	 * 00 00 01 00 = From RPi
	 * 00 01 00 00 = From Client
	 * 00 00 00 10 = To Server
	 * 00 00 10 00 = To RPi
	 * 00 10 00 00 = To Client
	 * Combined examples:
	 * 00 10 00 01 = To Client From Server
	 * 00 00 10 01 = To RPi From Server
	 * 00 00 01 10 = From RPi to Server
	 */
	
	public static final byte
	fromServer = 1,
	toServer = 2,
	fromRPi = 4,
	toRPi = 8,
	fromClient = 16,
	toClient = 32;
}
