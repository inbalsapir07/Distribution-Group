import java.net.InetAddress;
/**
 * The class RegisteredClient represents a client that joined a distribution group.
 * 
 * @author (Inbal Sapir)
 * @version (January 30, 2021)
 */
public class RegisteredClient 
{
	// variables
	private InetAddress address; // client's address
	private int port; // client's port
	// constructor
	/**
	 * Constructs a new registered client using the client's address and the client's port.
	 * @param address the client's address
	 * @param port the client's port
	 */
	public RegisteredClient (InetAddress address, int port)
	{
		this.address= address;
		this.port= port;
	}
	// methods
	/**
	 * Gets the client's address
	 * @return the client's address
	 */
	public InetAddress getAddress ()
	{
		return address;
	}
	/**
	 * Gets the client's port
	 * @return the client's port
	 */
	public int getPort ()
	{
		return port;
	}
	/**
	 * Indicates whether some other object is equal to this one.
	 * @override equals in class Object
	 * @param o the reference Object
	 * @return true if this RegisteredClient is the same as reference RegisteredClient; false otherwise
	 */
	public boolean equals (Object o)
	{
		if (o instanceof RegisteredClient && port==((RegisteredClient)o).getPort() &&
				address.equals(((RegisteredClient)o).getAddress()))
			return true;
		else
			return false;
	}
}