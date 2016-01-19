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
 * PacketViolation.class, shared between all Ecobox Projects
 * 
 * Purpose: Throw ALL the exceptions! (If you don't get it, stop right here and go educate yourself on memes)
 * 
 * 
 ***/

public class PacketViolation extends Exception {

	private static final long serialVersionUID = -3807621260284217242L;

	public PacketViolation(String msg){
		super("[Packet]" + msg);
	}
}
