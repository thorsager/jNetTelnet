/*
 * Copyright (c) 2013, Michael Thorsager <thorsager@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 *  * Neither the name of Open Solutions nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.krakow.jnettelnet.samples;

import dk.krakow.jnettelnet.Session;
import dk.krakow.jnettelnet.SessionException;

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
