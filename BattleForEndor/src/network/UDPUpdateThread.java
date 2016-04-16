package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class UDPUpdateThread extends Thread{

	private final int FPS = 40;
	private long startTime, sleepTime;
	private long ticksPS = 1000/FPS;
	private boolean running = false;
	private DatagramSocket serverSocket;
	private DatagramPacket sendPacket;
	private byte[] sendData;
	private InetAddress IPAddress;
	private int port;
	private Server server;
	
	

	public UDPUpdateThread()throws Exception{
		serverSocket = new DatagramSocket(9875);
		sendData = new byte[1024];
	}
	
	public void setRunning(boolean run){
		running = run;
	}
	
	
	@Override
	public void run() {
		super.run();
		
		
		while(running){
			startTime = System.currentTimeMillis();
			
			if(!Server.Sockets.isEmpty()){
				//Send datagrams with entity list to all sockets, do it synchronized.
				for(Socket s : Server.Sockets){
					String testString = "Test message from server.";
					sendData = testString.getBytes();
					sendPacket = new DatagramPacket(sendData, sendData.length, s.getInetAddress(), 9876);
					try {
						serverSocket.send(sendPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			sleepTime = ticksPS - (System.currentTimeMillis()-startTime);
			try{
				if(sleepTime > 0){
					sleep(sleepTime);
				} 
				else sleep(10);
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		}
	}
