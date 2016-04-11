package network;
import game_state.Entity;
import game_state.MasterGameState;
import game_state.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;


public class Server {

	
	private final static int PORT = 4444;

	public static MasterGameState GameState = new MasterGameState();

	public static List<Socket> Sockets = Collections.synchronizedList(new ArrayList<Socket>());
	public static List<ObjectOutputStream> OutStreams = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
	public static List<Socket> Connections = Collections.synchronizedList(new ArrayList<Socket>());
	private static List<ServingThread> ClientThreads = Collections.synchronizedList(new ArrayList<ServingThread>());


	public static void main(String[] args) {

		ServerSocket server;

		
		try {
			server = new ServerSocket(PORT);
			System.out.println("Server is running.");

			while(true){
				Socket pipe = server.accept();
				System.out.println("Add connection to new client.");


				ObjectOutputStream oos = new ObjectOutputStream(pipe.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(pipe.getInputStream());


				Sockets.add(pipe);
				OutStreams.add(oos);

				ServingThread s = new ServingThread(pipe, oos, ois);
				Thread t = new Thread(s);
				t.start();
				ClientThreads.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//___________________________METHODS FOR SENDING TO ALL CLIENTS THROUGH ALL UPDATE THREADS_____________________________
	
	/**
	 * Handles the first join message from a client. The client is assigned an id and a position
	 * and is added to the servers master game state.
	 * @param message
	 * @return
	 * @throws IOException
	 */
	

	public static void sendHit(int id, int projectileID) throws IOException{
		synchronized (ClientThreads) {
			System.out.println("Skicka sendHit till alla. Det blir s�h�r m�nga: ");
			int count = 1;
			for(ServingThread s : ClientThreads){
				System.out.println(count);
				count++;
				s.sendHit(id, projectileID);
			}	
		}
	}

	public static synchronized void sendKill(int id) throws IOException{
		synchronized (ClientThreads) {
			for(ServingThread s : ClientThreads){
				s.sendKill(id);
			}
		}
	}
	public static void SendFire(String team, int x, int y, int id) throws IOException {
		
		synchronized (ClientThreads) {
			for(ServingThread s : ClientThreads){
				s.sendFire(team, x, y, id);
			}
		}
		
	}
}


