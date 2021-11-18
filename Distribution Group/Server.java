import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import javax.swing.*;
/**
 * The class Server represents a server that distributes updates to clients in a distribution group.
 * Question 2, maman 16.
 * 
 * @author (Inbal Sapir)
 * @version (January 30, 2021)
 */
public class Server extends JPanel
{
	// variables
	private ArrayList <RegisteredClient> group; // distribution group
	private AListener actionListener = new AListener(); // listener for action events
	private WAdapter windowListener = new WAdapter(); // listener for window events
	private JTextArea updatesArea; // updates area
	private JScrollPane updatesScroll; // updates scroll pane
	private JButton sendButton; // send updates button
	private JFrame frame; // the frame of the panel
	private int port= 7777; // the port this server listens on
	private DatagramSocket socket;
	private DatagramPacket receivePacket; // a packet to receive from client
	private DatagramPacket sendPacket; // a packet to send to client
	private String join= "join"; // the message a client sends to server when user wants to join distribution group
	private String leave= "leave"; // the message a client sends to server when user wants to leave distribution group
	private boolean flag; // true when server receives joining and leaving messages from clients
	// constructor
	/**
	 * An empty constructor. Constructs a new server that distributes news updates.
	 * The server's panel includes a text area to write updates, and a send button.
	 */
	public Server ()
	{
		// creating the panel
		updatesArea= new JTextArea ();
		updatesArea.setLineWrap(true);
		updatesArea.setWrapStyleWord(true);
		updatesScroll= new JScrollPane (updatesArea); 
		sendButton= new JButton ("Send");
		sendButton.addActionListener(actionListener);
		setLayout (new BorderLayout());
		add (updatesScroll, BorderLayout.CENTER);
		add (sendButton, BorderLayout.SOUTH);
		// creating the frame
		frame= new JFrame ("Distributing News");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(500,500);
		frame.add(this);
		frame.setVisible(true);
		frame.addWindowListener(windowListener);
		// creating a socket and distribution group 
		try 
		{
			socket= new DatagramSocket (port);
			System.out.println ("server's ready");
		}
		catch (SocketException e1)
		{
			e1.printStackTrace();
		}
		group= new ArrayList <RegisteredClient> ();
		flag= true;
	}
	// methods
	/**
	 * The main method of the program.
	 * Creates a new server and a panel to send updates. 
	 * Receives joining distribution group messages or leaving distribution group messages from clients.
	 */
	public static void main(String[] args) 
	{
		Server server= new Server ();
		server.start();
	}
	/**
	 * Receives joining distribution group messages or leaving distribution group messages from clients.
	 * Acts by the message received from client- Adds or removes client from distribution group.
	 */
	public void start ()
	{
		while (flag) 
		{
			try 
			{
				byte[] buf= new byte [256];
				receivePacket= new DatagramPacket (buf, buf.length);
				socket.receive(receivePacket); // receive message from a client
				buf= receivePacket.getData();
				String received= new String (buf, 0, receivePacket.getLength());
				int receivePort= receivePacket.getPort();
				InetAddress receiveAddress= receivePacket.getAddress();
				if (received.equals(join)) // if a joining message was received from client
					group.add(new RegisteredClient(receiveAddress, receivePort));
				if (received.equals(leave)) // if a leaving message was received from client
					group.remove(new RegisteredClient(receiveAddress, receivePort)); 
			} 
			catch (IOException e) 
			{
				System.out.println ("server disconnected");
			}
		}
	}
	/**
	 * The class AListener handles relevant action events.
	 */
	private class AListener implements ActionListener
	{
		/**
		 * Handles the event which was invoked by clicking on the send button.
		 * Distributes update to all clients in the distribution group.
		 * @override actionPerformed in interface ActionListener
		 * @param e the event
		 */
		public void actionPerformed (ActionEvent e)
		{
			String update= updatesArea.getText();
			byte[] message= update.getBytes();
			Iterator <RegisteredClient> iterator= group.iterator();
			while (iterator.hasNext()) // distribute update to all clients in the distribution group
			{
				try 
				{
					RegisteredClient user= iterator.next();
					InetAddress userAddress= user.getAddress();
					int userPort= user.getPort();
					sendPacket= new DatagramPacket (message, message.length, userAddress, userPort);
					socket.send(sendPacket); // send update to client
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			updatesArea.setText("");
		}
	}
	/**
	 * The class WAdapter handles relevant window events.
	 */
	private class WAdapter extends WindowAdapter
	{
		/**
		 * Handles the event which was invoked by closing the frame window.
		 * Closes open sockets, disposes frame and ends program. 
		 * @override windowClosing in class WindowAdapter
		 * @param e the event
		 */
		public void windowClosing(WindowEvent e)
		{
			flag= false;
			socket.close();
			frame.dispose();
			System.exit(1);
		}
	}
}