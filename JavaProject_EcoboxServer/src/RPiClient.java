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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;




public class RPiClient {

	public Connection con = null;
	public final String authKey;
	public final int ID;
	public final String NAME;
	public ArrayList<Integer> owners;
	public ArrayList<Integer> members;
	public boolean publicData = false;
	public HashMap<String, Object> data = new HashMap<String, Object>();
	public HashMap<String, List<Integer>> dataListeners = new HashMap<String, List<Integer>>();
	
	public RPiClient(String authKey, int ID, String Name, ArrayList<Integer> o, ArrayList<Integer> m, boolean isDataPublic){
		this.authKey = authKey;
		this.ID = ID;
		this.publicData = isDataPublic;
		this.NAME = Name;
		owners = o;
		members = m;
	}
	
	public static RPiClient findByKey(Collection<RPiClient> clients, String authKey){
		for(RPiClient c : clients){
			if(c.authKey.equals(authKey)) return c;
		}
		return null;
	}
	
	public static RPiClient findByConnection(Collection<RPiClient> clients, int con){
		for(RPiClient c: clients){
			if(c.con != null && c.con.getID() == con) return c;
		}
		return null;
	}
	
	public static RPiClient findByID(Collection<RPiClient> clients, int ID){
		for(RPiClient c : clients){
			if(c.ID == ID) return c;
		}
		return null;
	}
	
}
