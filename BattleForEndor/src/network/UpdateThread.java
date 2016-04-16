package network;

import game_state.Entity;
import game_state.MasterGameState;
import game_state.Projectile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.Timer;

public class UpdateThread implements ActionListener{
	
	private Timer timer;
	private int interval = 40;
	private ObjectOutputStream oos;
	private Socket sock;

	
	
	public UpdateThread(ObjectOutputStream oos, Socket pipe){
		timer = new Timer(interval, this);
		sock = pipe;
		this.oos = oos;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		try {
			
			if(!MasterGameState.entities.isEmpty()){
				ConcurrentSkipListMap<Integer, Entity> entities = MasterGameState.copyList();
				synchronized (oos) {
					Message message = new Message("list", entities);
					oos.writeObject(message);
					oos.flush();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	public void startTimer(){
		timer.start();
	}

}
