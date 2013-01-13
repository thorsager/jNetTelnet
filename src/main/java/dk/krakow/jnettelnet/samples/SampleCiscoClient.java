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
