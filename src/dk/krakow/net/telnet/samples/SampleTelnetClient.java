/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.krakow.net.telnet.samples;

import dk.krakow.net.telnet.Session;
import dk.krakow.net.telnet.SessionException;

/**
 * <p>
 * Sample usage of the <code>dk.krakow.net.telnet.Session</code> class
 * </p>
 * <pre>
 *	// create Session object
 *	Session ts = new Session("cisco.host.com");
 *
 *	// Connect to remote host
 *	ts.connect();
 *
 *	// Read to some prompt and print 
 *	for ( String s: ts.read() ) { System.out.println(s); }
 * 
 *	// transmit my username
 *	ts.write("myUsername");
 *	for ( String s: ts.read() ) { System.out.println(s); }
 *
 *	// transmit my password
 *	ts.write("myPassword");
 *	for ( String s: ts.read() ) { System.out.println(s); }
 *
 *	// execute command on remote host
 *	ts.write("ls -al");
 *	for ( String s: ts.read() ) { System.out.println(s); }
 *
 *	// Close session to remote host
 *	ts.close(); 
 * </pre> 
 * 
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt; 
 */
public class SampleTelnetClient {

	public static void main(String[] args) throws SessionException {

		// create Session object
		Session ts = new Session("cisco.host.com");

		// Connect to remote host
		ts.connect();

		// Read to some prompt and print 
		for ( String s: ts.read() ) { System.out.println(s); }

		// transmit my username
		ts.write("myUsername");
		for ( String s: ts.read() ) { System.out.println(s); }

		// transmit my password
		ts.write("myPassword");
		for ( String s: ts.read() ) { System.out.println(s); }

		// execute command on remote host
		ts.write("ls -al");
		for ( String s: ts.read() ) { System.out.println(s); }

		// Close session to remote host
		ts.close();

	}
	
}
