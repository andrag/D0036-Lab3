package game_gui;

import game_state.Entity;
import game_state.LocalEntity;
import game_state.Player;
import game_state.Projectile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import sun.java2d.loops.DrawLine;




public class Board extends JPanel implements ActionListener{

	private Timer timer;
	private Player player;
	private ConcurrentSkipListMap<Integer, LocalEntity> opponents = new ConcurrentSkipListMap<Integer, LocalEntity>();
	private ConcurrentSkipListMap<Integer, Projectile> projectiles = new ConcurrentSkipListMap<Integer, Projectile>();

	private final int DELAY = 10;
	public GameClientGUI game;
	public boolean isConnected = false;
	public int playerID;
	public String playerTeam;

	private ImageIcon ii;
	private Image lifeImage;
	private Image background;


	public Board(GameClientGUI game){
		this.game = game;
		initBoard();
	}


	private void initBoard(){

		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);

		ii = new ImageIcon("res/one_life_small.png");
		lifeImage = ii.getImage();
		ii = new ImageIcon("res/endor2.jpg");
		background = ii.getImage();

		timer = new Timer(DELAY, this);
		timer.start(); 
	}


	//_____PAINTING METHODS___________________________________________________________________________________________

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();
	}

	
	private void doDrawing(Graphics g){
		if(isConnected){
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(background, 0, 0, this);

			drawPlayer(g2d);
			drawOpponents(g2d);
			drawProjectiles(g2d);

			
		}
	}

	private void drawPlayer(Graphics2D g2d){
		if(!player.alive){
			g2d.setColor(Color.RED);
			g2d.drawString("You dead!", getWidth()/2, getHeight()/2);
		}
		else{
			g2d.setColor(Color.WHITE);
			g2d.drawImage(player.getImage(), player.getX(), player.getY(), this);
			g2d.drawString(player.getUserName(), player.getX(), player.getY()+player.getHeight());
			drawLifeBar(player.getLives(), player.getX(), player.getY(), g2d);
		}
	}

	private void drawOpponents(Graphics2D g2d){
		if(!opponents.isEmpty()){
			synchronized (opponents) {
				Iterator<LocalEntity> iterator = opponents.values().iterator();
				while(iterator.hasNext()){
					LocalEntity tempEnt = iterator.next();
					g2d.drawImage(tempEnt.getImage(), tempEnt.getX(), tempEnt.getY(), this);
					g2d.drawString(tempEnt.getUserName(), tempEnt.getX(), tempEnt.getY()+tempEnt.getHeight());
					drawLifeBar(tempEnt.getLives(), tempEnt.getX(), tempEnt.getY(), g2d);
				}
			}}
	}

	private void drawProjectiles(Graphics2D g2d){
		if(!projectiles.isEmpty()){
			synchronized (projectiles) {
				Iterator<Projectile> it = projectiles.values().iterator();
				while(it.hasNext()){
					Projectile proj = it.next();
					if(proj.getTeam().equals("Empire")){
						g2d.setColor(Color.GREEN);
					}
					else g2d.setColor(Color.RED);
					g2d.fillRect(proj.getX(), proj.getY(), proj.getWidth(), proj.getHeight());
				}
			}
		}
	}


	private void drawLifeBar(int lives, int x, int y, Graphics2D g2d) {
		for(int i = 0; i<lives;i++){
			g2d.drawImage(lifeImage, x + i*20, y, this);
		}
	}


	//_______________ACTION METHODS___________________________________________________________________________________


	
	public void actionPerformed(ActionEvent arg0) {
		if(isConnected){
			if(player.alive){
				player.move();
			}
			moveProjectiles();
			repaint();
		}
	}
	
	private void moveProjectiles(){
		synchronized (projectiles) {
			Iterator<Projectile> it = projectiles.values().iterator();
			while(it.hasNext()){
				Projectile temp = it.next();
				temp.move();
				checkForCollission(temp);
				if(temp.getX() > getWidth() || temp.getX() < 0){
					it.remove();
				}
			}
		}
	}
	
	private void checkForCollission(Projectile temp){
		if(player.alive){
			if(temp.getBounds().intersects(player.getBounds()) && !temp.getTeam().equals(player.team)){
				try {
					game.client.sendHit(playerID, temp.getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private class TAdapter extends KeyAdapter{

		public void keyPressed(KeyEvent arg0) {
			if(isConnected && player.alive){
				try {
					player.keyPressed(arg0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void keyReleased(KeyEvent arg0) {
			if(isConnected && player.alive){
				try {
					player.keyReleased(arg0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	//______________RESPONSES TO MESSAGES FROM SERVER___________________________________________________________________

	/**
	 * Create our player using id and position from server
	 * @param data
	 */
	public void createPlayer(String[] data){
		//Array contains: id, x, y
		System.out.println("Now creating the player. Print its data:");
		for(String s : data){
			System.out.println(s);
		}
		playerID = Integer.parseInt(data[0]);
		player = new Player(Integer.parseInt(data[1]), Integer.parseInt(data[2]), playerTeam, Integer.parseInt(data[0]), this, GameClientGUI.userName);
		isConnected = true;
		repaint();
	}



	/**
	 * Receive a list with positions from the server every 40 ms and update the positions of
	 * all local entities.
	 * @param list
	 */
	public synchronized void updateAllPositions(ConcurrentSkipListMap<Integer, Entity> list) {
		if(player.alive && list.containsKey(playerID)){
			Entity me = list.get(playerID);
			player.updatePos(me.getX(), me.getY());
			list.remove(playerID);//
		}

		synchronized (opponents) {
			Iterator<Entity> it = list.values().iterator();
			while(it.hasNext()){
				Entity temp = it.next();
				if(opponents.containsKey(temp.getId())){
					LocalEntity le = opponents.get(temp.getId());
					le.updatePos(temp.getX(), temp.getY());
					//le.updateLives(temp.getLives());
				}
				else{

					LocalEntity newJoin = new LocalEntity(temp.getX(), temp.getY(), temp.getTeam(), temp.getId(), this, temp.getUserName(), temp.getLives());
					opponents.put(newJoin.ID, newJoin);
				}
			}
		}
	}


	public synchronized void killEntity(int id) {
		if(player.ID == id){
			player.alive=false;
		}
		else{
			synchronized (opponents) {
				if(opponents.containsKey(id)){
					opponents.remove(id);
				}
			}
		}
	}


	public synchronized void hitEntity(int id, int projectileID) {
		if(playerID == id){
			player.decreaseLives();
		}
		else{
			synchronized (opponents) {
				if(opponents.containsKey(id)){
					opponents.get(id).decreaseLives();
				}
			}
		}
		if(projectiles.containsKey(projectileID)){
			synchronized (projectiles) {
				projectiles.remove(projectileID);
			}
		}
	}


	public synchronized void fireProjectile(String team, int x, int y, int projID) {
		Projectile proj = new Projectile(projID, x, y, team);
		projectiles.put(projID, proj);
	}
}
