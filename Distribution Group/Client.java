import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
/**
 * The class Client represents a client that receives news from server.
 * Question 2, maman 16.
 * 
 * @author (Inbal Sapir)
 * @version (January 30, 2021)
 */
public class Client extends JPanel
{
	//variables
	private AListener actionListener = new AListener(); // listener for action events
	private WAdapter windowAdapter = new WAdapter(); // listener for window events
	private JTextArea newsArea; // news area
	private JScrollPane newsScroll; // news scroll pane
	private JPanel center; // the center panel, to receive news
	private JButton clearButton; // clear button
	private JButton joinButton; // join distribution group button
	private JButton leaveButton; // leave distribution group button
	private Box box; // box for south panel
	private JPanel south; // the south panel
	private JFrame frame; // the frame of the panel
	private int port; // server's port
	private static String host; // host name
	private InetAddress address; // server's address
	private DatagramSocket socket;
	private final int DELAY= 2000; // the number of milliseconds to wait for receiving a packet
	private DatagramPacket receivePacket; // a packet to receive from server
	private DatagramPacket sendPacket; // a packet to send to server
	private byte join[]= "join".getBytes(); // message to send to server when user wants to join distribution group
	private byte leave[]= "leave".getBytes(); // message to send to server when user wants to leave distribution group
	private boolean flag; // true when client joins distribution group
	private Receive receive; // a thread that handles receiving news
	// constructor
	/**
	 * An empty constructor. Constructs a new client who can receive news updates.
	 * The client's panel includes text area for receiving news,
	 * a clear button to clear the text area,
	 * a join distribution group button to join distribution group,
	 * and a leave distribution group button to leave distribution group.
	 */
	public Client ()
	{
		// center panel
		newsArea= new JTextArea ();
		newsArea.setLineWrap(true);
		newsArea.setWrapStyleWord(true);
		newsArea.setEnabled(false);
		newsScroll= new JScrollPane (newsArea); 
		center= new JPanel();
		center.setLayout(new BorderLayout());
		center.add(newsScroll, BorderLayout.CENTER);
		// south panel
		clearButton= new JButton ("Clear");
		clearButton.addActionListener(actionListener); 
		clearButton.setEnabled(false);
		joinButton= new JButton ("Join Distribution Group");
		joinButton.addActionListener(actionListener); 
		joinButton.setEnabled(true);
		leaveButton= new JButton ("Leave Distribution Group");
		leaveButton.addActionListener(actionListener); 
		leaveButton.setEnabled(false);
		box = Box.createHorizontalBox();
		box.add(clearButton);
		box.add(Box.createHorizontalGlue());
		box.add(joinButton);
		box.add(leaveButton);
		south= new JPanel ();
		south.setLayout(new BoxLayout (south, BoxLayout.X_AXIS));
		south.add(box);
		// organizing panels in the news panel
		setLayout (new BorderLayout());
		add (center, BorderLayout.CENTER);
		add (south, BorderLayout.SOUTH);
		// creating the frame
		frame= new JFrame ("Breaking News");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(500,500);
		frame.add(this);
		frame.setVisible(true);
		frame.addWindowListener(windowAdapter);
		// destination server
		try 
		{
			port= 7777;
			socket= new DatagramSocket ();
			socket.setSoTimeout(DELAY);
			host=JOptionPane.showInputDialog(frame, "Please enter host name:");
			if (host==null || host.equals(""))
				host= "localhost";
			address= InetAddress.getByName(host);
		} 
		catch (SocketException e1) 
		{
			e1.printStackTrace();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
	}
	// methods
	/**
	 * The main method of the program.
	 * Creates a new client and a panel to receive news. 
	 */
	public static void main(String[] args) 
	{
		Client client= new Client ();
	}
	/**
	 * The class AListener handles relevant action events.
	 */
	private class AListener implements ActionListener
	{
		/**
		 * Handles the event which was invoked by user clicking on a button.
		 * If user clicked on clear button, clears text area from news updates.
	     * If user joined distribution group, joins distribution group.
	     * If user left distribution group, leaves distribution group.
		 * @override actionPerformed in interface ActionListener
		 * @param e the event
		 */
		public void actionPerformed (ActionEvent e)
		{
			if (((JButton)e.getSource()).getText().charAt(0)=='C') // if user wants to clear screen
				newsArea.setText("");
			if (((JButton)e.getSource()).getText().charAt(0)=='J') // if user wants to join distribution group
				join ();
			if (((JButton)e.getSource()).getText().charAt(0)=='L') // if user wants to leave distribution group
				leave ();
		}
	}
	/**
	 * Joins distribution group.
	 * Enables and disables relevant components,
	 * sends joining message to server,
	 * Creates a new thread that handles receiving news.
	 */
	public void join ()
	{
		try 
		{
			newsArea.setEnabled(true);
			clearButton.setEnabled(true);
			joinButton.setEnabled(false);
			leaveButton.setEnabled(true);
			flag= true;
			sendPacket= new DatagramPacket(join, join.length, address, port);
			socket.send(sendPacket); // sending joining message to server
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		receive= new Receive (flag, receivePacket, socket, newsArea);
		receive.start(); // starting a new thread that handles receiving news
	}
	/**
	 * Leaves distribution group.
	 * Sends leaving message to server,
	 * enables and disables relevant components.
	 */
	public void leave ()
	{
		try 
		{
			sendPacket= new DatagramPacket(leave, leave.length, address, port);
			socket.send(sendPacket); // sending leaving message to server
			receive.setFlag(false);
			newsArea.setEnabled(false);
			clearButton.setEnabled(false);
			joinButton.setEnabled(true);
			leaveButton.setEnabled(false);
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		catch (NullPointerException e2)
		{
			e2.printStackTrace();
		}
	}
	/**
	 * The class WAdapter handles relevant window events.
	 */
	private class WAdapter extends WindowAdapter
	{
		/**
		 * Handles the event which was invoked by closing the frame window.
		 * Leaves distribution group, closes open sockets, disposes frame and ends program. 
		 * @override windowClosing in class WindowAdapter
		 * @param e the event
		 */
		public void windowClosing(WindowEvent e)
		{
			leave();
			socket.close();
			frame.dispose();
			System.exit(1);
		}
	}
}