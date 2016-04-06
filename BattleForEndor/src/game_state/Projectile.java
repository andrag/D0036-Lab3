package game_state;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.ImageIcon;

import org.w3c.dom.css.Rect;

public class Projectile implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private int x, y, speed, width, height, id;
	private String team;
	private boolean exists;
	public static int projectileID = 0;
	private Rectangle boundingBox;
	
	
	
	public Projectile(int id, int x, int y, String team){
		
		this.id = id;
		this.x = x;
		this.y = y;
		this.team = team;
		if(team.equals("Alliance")){
			
			speed = 2;
		}
		else {
			
			speed = -2;
		}
		width = 25;
		height = 15;
		boundingBox = new Rectangle(x, y, width, height);
		exists = true;
	}
	
	public void move(){
		x += speed;
		if(x+width<0 || x > 800){
			exists = false;
		}
		boundingBox.setBounds(x, y, width, height);
	}
	
	public boolean exists(){
		return exists;
	}
	
	public void setExist(boolean exist){
		this.exists = exist;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getTeam(){
		return team;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Rectangle getBounds(){
		return boundingBox;
	}
	
	public synchronized void updatePos(int x, int y){
		this.x = x;
		this.y = y;
	}
}
