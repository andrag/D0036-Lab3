package network;

import game_gui.Board;
import game_state.Entity;
import game_state.Player;
import game_state.Projectile;

import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;


public class Client implements Runnable{

	private Socket socket;
	private static Board board;
	private Socket socketConnection = null;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public Client(Socket socket, Board board, ObjectOutputStream oos){
		this.socket = socket;
		this.board = board;
		this.oos = oos;
	}

	@Override
	public void run() {
		try {

			ois = new ObjectInputStream(socket.getInputStream());

			System.out.println("Receive object from server.");
			Message reply = (Message) ois.readObject();
			handleInput(reply);

			while(true){
					receive();
			}
		
			//Some cleanup
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally{
			if(oos!=null){
				try {
					oos.close();
					System.out.println("The objectoutputstream is closed at the client.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ois != null){
				try{
					ois.close();
					System.out.println("The objectinputstream is closed at the client.");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	//______________________________RECEIVING METHODS______________________________________________________________________

	
	private void receive() throws ClassNotFoundException, IOException {
		try{
			synchronized (ois) {
				Message input = (Message) ois.readObject();
				handleInput(input);
			}
		}catch(EOFException e){
			System.out.println("EOFException at client. Oooops.");
		}

	}

	
	private synchronized void handleInput(Message message) throws IOException{

		
		if(message.getType().equals("chatmessage")){
			//Chatmeddelande
		}
		
		else if(message.getType().equals("joinreply")){
			String msg = message.getString();
			String[] playerData = msg.split(",");
			board.createPlayer(playerData);
			
		}

		else if(message.getType().equals("list")){
			ConcurrentSkipListMap<Integer, Entity> list = message.getList();
			Iterator<Entity> iterator = list.values().iterator();
			board.updateAllPositions(list);
		}
		
		else if(message.getType().equals("kill")){
			int id = message.getId();
			board.killEntity(id);
		}
		else if(message.getType().equals("playerhit")){
			String str = message.getString();
			String[] data = str.split(",");
			int playerID = Integer.parseInt(data[0]);
			int projectileID = Integer.parseInt(data[1]);
			System.out.println("Received info from server that player with id: "+playerID+" was hit.");
			board.hitEntity(playerID, projectileID);
		}
		
		else if(message.getType().equals("fire")){
			//Get board to make the right entity fire the right team color laser
			String str = message.getString();
			String[] data = str.split(",");
			String team = data[0];
			int x = Integer.parseInt(data[1]);
			int y = Integer.parseInt(data[2]);
			int projID = Integer.parseInt(data[3]);
			board.fireProjectile(team, x, y, projID);
		}
	}
	
	
	
	//______________________________SEDNING METHODS________________________________________________________________________

	

	public void send(String input){

	}
	
	public synchronized void sendMove(int id, int direction, int type) throws IOException{
		synchronized (oos) {
			String moveString = id+","+direction+","+type;
			Message moveMessage = new Message("moverequest", moveString);
			oos.writeObject(moveMessage);
			oos.flush();
		}
	}

	public void disconnect() {
		
	}

	public synchronized void sendProjectile(String team, int x, int y) throws IOException {
		synchronized (oos) {
			String str = team+","+x+","+y;
			Message msg = new Message("projectile", str);
			oos.writeObject(msg);
			oos.flush();
		}
	}

	public synchronized void sendHit(int playerID, int projectileID) throws IOException {
		synchronized (oos) {
			String str = playerID+","+projectileID;//Onödigt att blanda message type och string headers. Kör med enums sen.
			Message msg = new Message("playerhit", str);
			oos.writeObject(msg);
			oos.flush();
		}	
	}
}
