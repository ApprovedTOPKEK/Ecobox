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


import java.util.Collection;
import java.util.HashMap;


public class RPi {

	public final int ID;
	public final String NAME;
	public String[] owners;
	public boolean status;
	public HashMap<String, Object> data = new HashMap<String, Object>();
	
	public RPi(int ID, String Name, boolean status, String[] o){
		this.ID = ID;
		this.NAME = Name;
		this.status = status;
		owners = o;
	}
	
	public static RPi findByID(Collection<RPi> clients, int ID){
		for(RPi c : clients){
			if(c.ID == ID) return c;
		}
		return null;
	}
	
}