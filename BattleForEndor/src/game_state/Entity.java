package game_state;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class Entity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id, x, y, width, height, lives;
	private int dx = 0;
	private int dy = 0;
	private String team;
	private String userName;
	public boolean destroyed = false;
	public boolean hit = false;

	public Entity(int id, int x, int y, String team, String userName, int lives){
		this.id = id;
		this.x = x;
		this.y = y;
		this.team = team;
		this.userName = userName;
		this.lives = lives;
		initEntity();
	}
	
	/**
	 * Get the width and height of the Entity. Kan skickas med från början istället.
	 */
	private void initEntity(){
		ImageIcon ii = new ImageIcon();
		if(team.equals("Empire")){
			ii = new ImageIcon("res/deathstar.png");
		}
		if(team.equals("Alliance")){
			ii = new ImageIcon("res/ackbar.png");
		}
		width = ii.getIconWidth();
		height = ii.getIconHeight();
	}
	
	

	public int getId(){
		return id;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public String getTeam(){
		return team;
	}
	
	public String getUserName(){
		return userName;
	}

	public synchronized void setDx(int i) {
		dx = i;
	}
	
	public synchronized void setDy(int i){
		dy = i;
	}
	
	public synchronized void updatePos(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	
	//Game width är hårdkodat nu.
	public synchronized void move(){
		
			if(x > 0 && x + width < 800){
				x += dx;
			}
			else {
				if(x == 0)x++;
				else if(x + width == 800)x--;
				dx = 0;
			}
			if(y > 0 && y + height < 600){
				y += dy;
			}
			else {
				if(y == 0)y++;
				else if(y + height == 600)y--;
				dy = 0;
			}
		}

	public synchronized void decreaseLives() {
		lives--;
	}
	

	public int getLives(){
		return lives;
	}
	
	
}
