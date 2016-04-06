package game_state;


import game_gui.Board;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class LocalEntity{
	
	private static final long serialVersionUID = 1L;
	private int dx;
	private int dy;
	private int x;
	private int y;
	private int lives;
	public final int ID;
	public String team;
	private String userName;
	private Image image;
	private int width;
	private int height;
	private Board board;
	

	public LocalEntity(int x, int y, String team, int id, Board board, String userName, int lives){//, Board board){
		this.x = x;
		this.y = y;
		this.team = team;
		this.ID = id;
		this.board = board;
		this.userName = userName;
		this.lives = lives;
		initPlayer();
	}
	
	private void initPlayer(){
		ImageIcon ii = new ImageIcon();
		if(team.equals("Empire")){
			ii = new ImageIcon("res/deathstar.png");
		}
		if(team.equals("Alliance")){
			ii = new ImageIcon("res/ackbar.png");
		}
		width = ii.getIconWidth();
		height = ii.getIconHeight();
		image = ii.getImage();
		
	}


	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}
	
	public String getTeam(){
		return team;
	}
	
	public String getUserName(){
		return userName;
	}

	public Image getImage(){
		return image;
	}
	
	public synchronized void updatePos(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void setX(int newX){
		x = newX;
	}
	public void setY(int newY){
		y = newY;
	}
	
	public synchronized void setDx(int i){
		dx = i;
	}
	
	public synchronized void setDy(int i){
		dy = i;
	}

	
	public synchronized void move(){
		if(x > 0 && x + width < 800){
			x += dx;
		}
		else {
			if(x == 0)x++;
			else if(x + width == 800)x--;
			dx = 0;
		}
		if(y > 0 && y + height < 800){
			y += dy;
		}
		else {
			if(y == 0)y++;
			else if(y + height == 600)y--;
			dy = 0;
		}
	}

	public synchronized void updateLives(int lives) {
		this.lives = lives; 
	}
	
	public synchronized void decreaseLives(){
		lives--;
	}
	
	public int getLives(){
		return lives;
	}

	
	
}
