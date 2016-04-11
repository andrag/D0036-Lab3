package game_gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

//import javafx.scene.control.ScrollBar;

import javax.imageio.ImageIO;
import javax.swing.*;

import network.Client;
import network.Server;
import network.Message;


public class GameClientGUI extends JFrame{
	//Globals
	public static Client client;
	public static Board board;
	public static String userName = "Anonymous";//Detta borde vara dynamiskt.
	public static String team = "No team";
	private static int PORT = 4444;
	private static String HOST = "Datamaskinen";
	
	private int width = 1000;
	private int height = 600;
	
	
	
	//___________GUI FIELDS_______________________________________
	
	//Chat
	public static JTextField message = new JTextField();
	
	private static JLabel chatLabel = new JLabel();
	public static JTextArea chat = new JTextArea();
	private static JScrollPane chatPane = new JScrollPane();
	
	private static JLabel gamersOnlineLabel = new JLabel();
	public static JList gamersOnlineList = new JList<>();
	private static JScrollPane gamersOnlinePane = new JScrollPane();
	
	//Network controls
	public static JButton connect;
	private static String connectString = "Connect to server";
	private static String disconnectString = "Disconnect from server";
	private static String userString = "User name: ";
	public static JButton disconnect;
	public static JButton send;
	
	private static JLabel loggedInAs = new JLabel();
	private static JLabel loggedInAsBox = new JLabel();
	
	//Components for login window
	public static JFrame logInFrame = new JFrame();
	public static JTextField userNameTextField = new JTextField(15);
	public static JButton connectButton = new JButton("Connect");
	public static JButton imperialButton = new JButton("Empire");
	public static JButton allianceButton = new JButton("Alliance");
	public static JLabel enterNameLabel = new JLabel("Enter username: ");
	public static JLabel chooseTeamLabel = new JLabel("Choose team: ");
	public static JPanel logInPanel = new JPanel();
	public static JLabel teamLabel = new JLabel("Choose team: ");
	
	public static JTextField ip_field = new JTextField("localhost", 15);
	public static JLabel enterIP = new JLabel("Server IP: ");
	public static JTextField port_field = new JTextField("4444", 15);
	public static JLabel enterPort = new JLabel("Server port: ");
	
	//Team pictures
	public static BufferedImage imperial;
	public static BufferedImage alliance;
	public static JLabel picLabel = new JLabel(new ImageIcon("res/no_team_picture.png"));
	
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				GameClientGUI game = new GameClientGUI();
				game.setVisible(true);
			}
		});
	}
	

	public GameClientGUI(){
		initGUI();
	}
	
	
	
	//_________INITIALIZE THE USER INTERFACE___________________________

	private void initGUI(){

		//Main window
		setLayout(null);
		setSize(width, height);
		setResizable(false);

		setTitle("The Battle of Endor!");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//The game area
		board = new Board(this);
		getContentPane().add(board);
		board.setBounds(0, 0, 800, height);
		board.setFocusable(true);
		
		
		//Information and control area
		connect = new JButton(connectString);
		add(connect);
		Dimension size = connect.getPreferredSize();
		connect.setBounds(810, 10, size.width, size.height);
		
		gamersOnlineLabel.setText("Jedis online:");
		gamersOnlineLabel.setToolTipText("");//No hovering over this, you hear?
		add(gamersOnlineLabel);
		size = gamersOnlineLabel.getPreferredSize();
		gamersOnlineLabel.setBounds(810, 50, size.width, size.height);
		
		gamersOnlineList.setForeground(new java.awt.Color(0,0,255));//Blue
		gamersOnlinePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		gamersOnlinePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		gamersOnlinePane.setViewportView(gamersOnlineList);
		add(gamersOnlinePane);
		size = gamersOnlinePane.getPreferredSize();
		gamersOnlinePane.setBounds(810, 70, 180, size.height);
		
		chatLabel.setText("Communications link:");;
		add(chatLabel);
		chatLabel.setBounds(810, 250, 150, 20);
		
		chat.setColumns(20);
		chat.setFont(new java.awt.Font("Tahoma", 0, 12));
		chat.setForeground(new java.awt.Color(0,0,255));
		chat.setLineWrap(true);
		chat.setRows(5);
		chat.setEditable(false);
		
		chatPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatPane.setViewportView(chat);
		add(chatPane);
		size = chatPane.getPreferredSize();
		chatPane.setBounds(810, 270, 180, 150);
		
		message.setForeground(new java.awt.Color(0,0,255));
		add(message);
		message.setBounds(810, 425, 180, 20);
		
		send = new JButton("Send");
		send.setEnabled(false);
		add(send);
		size = send.getPreferredSize();
		send.setBounds(810, 450, size.width, size.height);
		
		size = picLabel.getPreferredSize();
		add(picLabel);
		picLabel.setBounds(895, 465, (int)(0.3*size.width), (int)(0.3*size.height));
		
		loggedInAs.setText(userString);
		size = loggedInAs.getPreferredSize();
		add(loggedInAs);
		loggedInAs.setBounds(810, 550, 80, size.height);
		
		
		
		//_____________ACTION LISTENERS FOR BUTTONS IN THE MAIN GUI_________________________________________
		
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					actionSend();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(connect.getText().equals(connectString)){
					buildLogInFrame();
				}
				else if(connect.getText().equals(disconnectString)){
					actionDisconnect();
				}
			}
		});	
	}
	
	public static void actionSend() throws IOException{
		if(!message.getText().equals("")){
			client.send(message.getText());
			//message.requestFocus();
			board.requestFocus();	
		}
	}
	
	public static void actionDisconnect(){
		try{
			connect.setText(connectString);
			client.disconnect();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	

	
	
	//__________________METHODS FOR THE LOG IN WINDOW______________________________________
	
	/**
	 * Builds a frame with controls for logging in to the server.
	 */
	public static void buildLogInFrame(){
		Dimension size;
		
		logInFrame.setTitle("Enter gamer name and choose team!");
		logInFrame.setSize(300, 200);
		logInFrame.setLocation(250, 200);
		logInFrame.setResizable(false);
		logInFrame.setLayout(null);
		
		logInFrame.add(enterNameLabel);
		size = enterNameLabel.getPreferredSize();
		enterNameLabel.setBounds(10, 10, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(userNameTextField);
		size = userNameTextField.getPreferredSize();
		userNameTextField.setBounds(110, 10, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(chooseTeamLabel);
		size = chooseTeamLabel.getPreferredSize();
		chooseTeamLabel.setBounds(10, 40, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(allianceButton);
		size = allianceButton.getPreferredSize();
		allianceButton.setBounds(110, 35, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(imperialButton);
		size = imperialButton.getPreferredSize();
		imperialButton.setBounds(204, 35, (int)size.getWidth(), (int)size.getHeight());
			
		logInFrame.add(enterIP);
		size = enterIP.getPreferredSize();
		enterIP.setBounds(10, 70, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(ip_field);
		size = ip_field.getPreferredSize();
		ip_field.setBounds(110, 70, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(enterPort);
		size = enterPort.getPreferredSize();
		enterPort.setBounds(10, 105, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(port_field);
		size = port_field.getPreferredSize();
		port_field.setBounds(110, 105, (int)size.getWidth(), (int)size.getHeight());
		
		logInFrame.add(connectButton);
		size = connectButton.getPreferredSize();
		connectButton.setBounds(197, 130, (int)size.getWidth(), (int)size.getHeight());
		connectButton.setEnabled(false);
		
		
	
		
		
		actionLogIn();
		logInFrame.setVisible(true);
		}
	
	//Handles all button clicks in the log in window
	public static void actionLogIn(){
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String error = "";
				if(!ip_field.getText().equals("")){
					HOST = ip_field.getText();//Does not handle faulty syntax
				}
				
				if(!port_field.getText().equals("")){
					PORT = Integer.parseInt(port_field.getText());//No error handling
				}
				
				if(!userNameTextField.getText().equals("")){
					
					userName = userNameTextField.getText().trim();//Trimma bort inledande och avslutande whitespace
					loggedInAs.setText("User name: "+userName);//Threadshit
					
					//Notify users chat about the new user
					//Server.Users.add(userName);
					
					logInFrame.setVisible(false);
					send.setEnabled(true);
					connect.setText(disconnectString);
					connect();
				}
				else{
					JOptionPane.showMessageDialog(null, "Please enter a name!");
					allianceButton.setEnabled(true);
					imperialButton.setEnabled(true);
				}
				board.requestFocus();
				
			}
		});
		
		imperialButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				imperialButton.setEnabled(false);
				allianceButton.setEnabled(false);
				connectButton.setEnabled(true);
				team = "Empire";
				board.playerTeam = team;
			}
		});
		
		allianceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				allianceButton.setEnabled(false);
				imperialButton.setEnabled(false);
				connectButton.setEnabled(true);
				team = "Alliance";
				board.playerTeam = team;
				
			}
		});
	}
	
	
	
	//________________________CONNECTING TO SERVER METHOD_________________________________________
	
	
	public static void connect(){
		
		try{
			//final int PORT = 444;
			//final String HOST = "Datamaskinen";
			Socket sock = new Socket(HOST, PORT);
			System.out.println("You connected to " + HOST);
			
			
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());//Ska denna skickas med tråden?
			out.flush();
			String joinStr = userName+","+team;
			Message joinMsg = new Message("joinrequest", joinStr);
			out.writeObject(joinMsg);
			out.flush();
			client = new Client(sock, board, out);
			
			Thread t = new Thread(client);
			t.start();
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	

	//__________________RETURN METHODS____________________________________

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}
}
