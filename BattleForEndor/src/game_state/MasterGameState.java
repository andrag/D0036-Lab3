package game_state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.Timer;

import network.Server;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

public class MasterGameState implements ActionListener{

	public static int idAssigned = 0;
	public static int noEmpireUnits;
	public static int noAllianceUnits;
	public static int projectileID = 0;
	

	public static ConcurrentSkipListMap<Integer, Entity> entities = new ConcurrentSkipListMap<Integer, Entity>();
	public static ConcurrentSkipListMap<Integer, Projectile> projectiles = new ConcurrentSkipListMap<Integer, Projectile>();
	
	private Timer timer;
	private final int DELAY = 10; 

	public MasterGameState(){
		timer = new Timer(DELAY, this);
		timer.start();


	}

	/**SYNKA ADDEN PÅ DENNA NÄR DET BLIR MULTITHREAD
	 * 
	 * This method creates a new entity representing the newly joined player at the server game state.
	 * @param player
	 * @return a String containing an assigned id and position for the newly joined player.
	 */
	public static String addNewPlayer(String team, String userName){
		//Calculate id and position for the new player
		idAssigned++;
		int x = 0, y = 0;
		if(team.equals("Empire")){
			noEmpireUnits++;
			x = 600;
			y = 100 * noEmpireUnits;
		}
		else if(team.equals("Alliance")){
			noAllianceUnits++;
			x = 50;
			y = 100 * noAllianceUnits;
		}

		//Add the new player to the game state
		synchronized (entities) {
			Entity e = new Entity(idAssigned, x, y, team, userName, 3);
			entities.put(idAssigned, e);
		}

		//Data to return to the newly joined player
		String data = idAssigned+","+x+","+y;
		return data;

	}

	/**
	 * Updates the speed of an entity as a response to user key input.
	 * Every entitys move method is then using the speed to update position with regular intervals.
	 * @param id
	 * @param key
	 */
	public static synchronized void updateEntitySpeed(int id, int key, int type){
		synchronized(entities){
			Entity e = entities.get(id);

			if(type == KeyEvent.KEY_PRESSED){
				if(key == KeyEvent.VK_LEFT){
					System.out.println("Client pressed left key.");
					e.setDx(-1);
				}

				if(key == KeyEvent.VK_RIGHT){
					System.out.println("Client pressed right key.");
					e.setDx(1);
				}

				if(key == KeyEvent.VK_UP){
					System.out.println("Client pressed up key.");
					e.setDy(-1);
				}

				if(key == KeyEvent.VK_DOWN){
					System.out.println("Client pressed down key.");
					e.setDy(1);
				}
			}
			if(type == KeyEvent.KEY_RELEASED){
				if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT){
					e.setDx(0);
				}
				if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN){
					e.setDy(0);
				}
			}
		}
	}


	/**
	 * Updates the positions of every entity every 10 ms.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(!entities.isEmpty()){
			Entity e;
			synchronized (entities) {
				Iterator<Entity> iterator = entities.values().iterator();
				while(iterator.hasNext()){
					e = iterator.next();
					e.move();
				}
			}
		}
	}




	//Metod för att lösa heartbeatproblem genom att kopiera listan istället för att skicka själva listan. Kanske inte behövs
	//efter synkning.
	public static ConcurrentSkipListMap<Integer, Entity> copyList(){
		ConcurrentSkipListMap<Integer, Entity> tempMap = new ConcurrentSkipListMap<Integer, Entity>();
		synchronized (entities) {

			//Pröva att bta ut mot en enkel copy-metod
			Iterator<Entity> iterator = entities.values().iterator();
			while(iterator.hasNext()){
				Entity fromEnt = iterator.next();
				Entity toEnt = new Entity(fromEnt.getId(), fromEnt.getX(), fromEnt.getY(), fromEnt.getTeam(), fromEnt.getUserName(), fromEnt.getLives());
				tempMap.put(toEnt.getId(), toEnt);
			}
		}
		return tempMap;
	}

	public static ConcurrentSkipListMap<Integer, Projectile> copyProjectiles(){
		ConcurrentSkipListMap<Integer, Projectile> copy = new ConcurrentSkipListMap<Integer, Projectile>();
		synchronized (projectiles) {
			Iterator<Projectile> iterator = projectiles.values().iterator();
			while(iterator.hasNext()){
				Projectile fromProj = iterator.next();
				Projectile toProj = new Projectile(fromProj.getId(), fromProj.getX(), fromProj.getY(), fromProj.getTeam());
				copy.put(toProj.getId(), toProj);
			}
		}
		return copy;
	}
	
	public static synchronized void incrementProjectileID(){
		projectileID++;
	}

	
	public static void fire(String team, int x, int y) throws IOException {
		incrementProjectileID();
		System.out.println("Inne i fire()");
		synchronized (projectiles) {
			Server.SendFire(team, x, y, projectileID);
		}

	}
	
	/**
	 * Process a hit message from a client. Decrease one life. 
	 * Notify all users of hit and possible kill.
	 * Destroys the psojectile involved.
	 *  
	 * @param playerID
	 * @param projectileID2
	 * @throws IOException
	 */
	public static void ProcessHit(int playerID, int projectileID2) throws IOException {
		//Decrease one life from the entity and notify all users of this
		synchronized (entities) {
			Entity player = entities.get(playerID);
			player.decreaseLives();
			Server.sendHit(playerID, projectileID2);
			if(!(player.getLives()>0)){
				Server.sendKill(playerID);
				entities.remove(playerID);
			}
		}
	}
}
