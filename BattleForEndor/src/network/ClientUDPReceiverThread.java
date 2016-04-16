package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientUDPReceiverThread extends Thread{
		
	
	public ClientUDPReceiverThread() throws IOException{
		System.out.println("Udp thread created at client.");
	}
	

	@Override
	public void run(){
		System.out.println("Run method running.");
		super.run();
		while(true){
			System.out.println("While loop is running.");
			try{
				System.out.println("Trying to listen.");
				DatagramSocket clientUDPSocket = new DatagramSocket(9876);
				//InetAddress IPAddress = InetAddress.getByName("localhost");
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				System.out.println("Next: Receive!");
				clientUDPSocket.receive(receivePacket);//Stalls here. Never receives anything.
				System.out.println("Packet received!");
				String received = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
				System.out.println("String made.");
				clientUDPSocket.close();
				System.out.println("Socket closed.");
				System.out.println("Client received UDP packet from server: " + received);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
		}
	}
	
	
}
