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


import java.util.Collection;

import com.esotericsoftware.kryonet.Connection;


public class User {

	public static User createGuest(String name, int id){
		User u = new User();
		u.id = -1 * id;
		u.userGroup = 0;
		u.name = name;
		return u;
	}
	
	public int id;
	public String name;
	public byte userGroup; //0: Guest, 1: Member, 2: Admin
	public Connection con = null;
	
	public static User findByConnection(Collection<User> clients, int con){
		for(User c: clients){
			if(c.con != null && c.con.getID() == con) return c;
		}
		return null;
	}
	
	public static User findByID(Collection<User> clients, int id){
		for(User c : clients){
			if(c.id == id) return c;
		}
		return null;
	}
}
