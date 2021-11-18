import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
/**
 * The class Receive represents a thread that handles receiving news.
 * 
 * @author (Inbal Sapir)
 * @version (January 30, 2021)
 */
public class Receive extends Thread
{
	// variables
	private boolean flag; // true if client joined distribution group; false if client left distribution group
	private DatagramPacket receivePacket; // a packet to receive from server
	private DatagramSocket socket;
	private JTextArea newsArea; // news area
	// constructor
	/**
	 * Constructs a new thread that handles receiving news,
	 * using a boolean variable, the client's DatagramSocket, DatagramPacket and news Area.
	 * @param flag boolean variable, true if client joined distribution group; false if client left distribution group
	 * @param receivePacket the client's DatagramPacket
	 * @param socket the client's DatagramSocket
	 * @param newsArea the client's news Area
	 */
	public Receive (boolean flag, DatagramPacket receivePacket, DatagramSocket socket, JTextArea newsArea)
	{
		this.flag= flag;
		this.receivePacket= receivePacket;
		this.socket= socket;
		this.newsArea= newsArea;
	}
	// methods
	/**
	 * Receives news from server and displays the time and the news to user.
	 * @override run in class Thread
	 */
	public void run ()
	{
		while (flag) // while client is in distribution group
		{
			try 
			{
				byte[] buf= new byte [256];
				receivePacket= new DatagramPacket (buf, buf.length);
				socket.receive(receivePacket); // receive new from server
				buf= receivePacket.getData();
				String received= new String (buf, 0, receivePacket.getLength());
				Date date = new Date(); 
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				newsArea.append(formatter.format(date)+"\n"+received+"\n"); // display the time and the news
			}
			catch (IOException e2) {}
		}
	}
	/**
	 * Sets boolean variable- true if client joined distribution group; false if client left distribution group
	 * @param flag the boolean variable
	 */
	public void setFlag (boolean flag)
	{
		this.flag= flag;
	}
}