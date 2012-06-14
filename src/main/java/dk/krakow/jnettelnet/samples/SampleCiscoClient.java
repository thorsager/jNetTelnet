package dk.krakow.jnettelnet.samples;

import dk.krakow.jnettelnet.CiscoSession;

/**
 * <p>
 * Sample usage of the <code>dk.krakow.net.telnet.CiscoSession</code> class
 * </p>
 * <pre>
 * 	// Create a session
 *	CiscoSession cs = new CiscoSession("cisco.host.com",23);
 * 
 *	// Connect the session to the host
 *	cs.connect();
 *
 *	// Login using username and password, if we are not asked
 *	// for the user name, it will not be used.
 *	cs.login("myusername", "Secret");
 *		
 *	// Enable on the router using password
 *	cs.enable("evenMoreSecret");
 *	
 *	// Setup the terminal (to avoid the paging stuff)
 *	cs.cmd("terminal length 0"); // ignoring output..
 *
 *	// Print the ios versino information
 *	for (String line: cs.cmd("show version") ) { System.out.println("got: "+line); }
 *
 *	// Print the running configuration of the host
 *	for (String line: cs.cmd("show running") ) { System.out.println("got: "+line); }
 *
 *	// Disconnect from remote host
 *	cs.close();
 * </pre>
 *
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt; 
 */
public class SampleCiscoClient {

	public static void main(String[] args) throws Exception {

		// Create a session
		CiscoSession cs = new CiscoSession("cisco.host.com",23);

		// Connect the session to the host
		cs.connect();

		// Login using username and password, if we are not asked
		// for the user name, it will not be used.
		cs.login("myusername", "Secret");
		
		// Enable on the router using password
		cs.enable("evenMoreSecret");
	
		// Setup the terminal (to avoid the paging stuff)
		cs.cmd("terminal length 0"); // ignoring output..

		// Print the ios versino information
		for (String line: cs.cmd("show version") ) { System.out.println("got: "+line); }

		// Print the running configuration of the host
		for (String line: cs.cmd("show running") ) { System.out.println("got: "+line); }

		// Disconnect from remote host
		cs.close();
	}
	
}
