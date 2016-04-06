package game_state;

import game_gui.Board;
import game_gui.GameClientGUI;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.w3c.dom.css.Rect;

public class Player implements Serializable{
	/**
	 * 
	 */
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
	private Rectangle bounds;
	public boolean alive = true;

	public Player(int x, int y, String team, int id, Board board, String userName){//, Board board){
		this.x = x;
		this.y = y;
		this.team = team;
		this.ID = id;
		this.board = board;
		this.userName = userName;
		lives = 3;
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
		bounds = new Rectangle(x, y, width, height);
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

	public Image getImage(){
		return image;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public synchronized void updatePos(int x, int y){
		this.x = x;
		this.y = y;
		bounds.setBounds(x, y, width, height);
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
	
	public Rectangle getBounds(){
		return bounds;
	}

	
	public void keyPressed(KeyEvent event) throws IOException{
		//Se till att endast skicka om det är relevanta tangenter
		int key = event.getKeyCode();
		int type = KeyEvent.KEY_PRESSED;
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN){
			board.game.client.sendMove(ID, key, type);
			
			if(key == KeyEvent.VK_LEFT){
				setDx(-1);
			}

			if(key == KeyEvent.VK_RIGHT){
				setDx(1);
			}

			if(key == KeyEvent.VK_UP){
				setDy(-1);
			}

			if(key == KeyEvent.VK_DOWN){
				setDy(1);
			}
		}	
		
		if(key == KeyEvent.VK_SPACE){
			System.out.println("Space är tryckt!");
			if(team.equals("Alliance")){
				board.game.client.sendProjectile(team, x+width, y+(height/2));
			}
			else board.game.client.sendProjectile(team, x, y+(height/2));
		}
	}

	public void keyReleased(KeyEvent event) throws IOException{

		int key = event.getKeyCode();
		int type = KeyEvent.KEY_RELEASED;
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN){
			System.out.println("Key released! Call requestMove!");
			board.game.client.sendMove(ID, key, type);
			
			if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT){
				setDx(0);
			}
			if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN){
				setDy(0);
			}
		}	
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
		if(y > 0 && y + height < board.getHeight()){
			y += dy;
		}
		else {
			if(y == 0)y++;
			else if(y + height == board.getHeight())y--;
			dy = 0;
		}
	}
	
	
	public synchronized void decreaseLives(){
		lives--;
		System.out.println("The player "+userName+" was hit. Lives left: "+lives);
	}
	
	public int getLives(){
		return lives;
	}	
}
