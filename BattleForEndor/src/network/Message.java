package network;
import game_state.Entity;
import game_state.Player;
import game_state.Projectile;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListMap;


public class Message implements Serializable{
	
	private String type;
	private Object object;
	private String string;
	private Player player;
	private int id;
	private ConcurrentSkipListMap<Integer, Entity> list;
	private ConcurrentSkipListMap<Integer, Projectile> projectiles;
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Message(String type, Object object){
		this.type = type;
		if(type.equals("chatmessage") && object instanceof String){
			string = (String) object;
		}
		
		if(type.equals("joinrequest")){
			string = (String) object;
		}
		
		if(type.equals("joinreply")){
			string = (String) object;
		}
		
		if(type.equals("list")){
			list = (ConcurrentSkipListMap<Integer, Entity>) object;
		}
		
		if(type.equals("moverequest")){
			string = (String) object;
		}
		
		if(type.equals("fire")){
			string = (String) object;
		}
		
		if(type.equals("projectile")){
			string = (String) object;
		}
		
		if(type.equals("playerhit")){
			string = (String) object;
		}
		
		if(type.equals("hit")){
			id = (int) object;
		}

		if(type.equals("kill")){
			id = (int) object;
		}
	}
	
	
	public String getString(){
		return string;
	}
	
	/*public void setString(String newMessage){
		string = newMessage;
	}*/

	public ConcurrentSkipListMap<Integer, Entity> getList(){
		return list;
	}
	
	/*public ConcurrentSkipListMap<Integer, Projectile> getProjectiles(){
		return projectiles;
	}*/

	/*public Player getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}*/
	
	public String getType(){
		return type;
	}
	
	public int getId(){
		return id;
	}
}
