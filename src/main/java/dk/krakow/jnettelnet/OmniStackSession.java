/*
 * Copyright (c) 2015, Michael Thorsager <thorsager@gmail.com>
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
package dk.krakow.jnettelnet;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Extends the <Code>Session</code> to Omnistack 6200 devices, adding features to handle
 * authentication and such.
 * <p>
 * Please note that this Class has an Internal <code>SessionOptionHandler</code>
 * that will automatically turn of anything the server wants it to do. This means
 * that no Terminal size negotiation will take place.
 * </p>
 *
 *
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
public class OmniStackSession extends Session implements SessionOptionHandler {

	/** String that is the last char of an Enabled prompt */
	private static final String ENABLED_PROMPT_CHR = "#";

	/** String Containing the regex. to find prompts */
	private static final String PROMPT_PATTERN = "(User Name:|Password:|\\w+[>#]|\\w+\\(config\\)#|\\w+\\(config-\\w+\\)#)$" ;

	private boolean authenticated = false;
	private boolean enabled = false;

	/**
	 * Create Session on the default Telnet tcp port-number of 23
	 * @param hostname  Name of remote host
	 */
	public OmniStackSession(String hostname) {
        super(hostname, null, null,Pattern.compile(PROMPT_PATTERN));
	}

	/**
	 * Create session on a non-standard tcp port-number
	 * @param hostname  Name of remote host
	 * @param port port-number
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public OmniStackSession(String hostname, Integer port) {
		super(hostname, port, null,Pattern.compile(PROMPT_PATTERN));
		this.setSessionOptionHandler(this);
	}

	/**
	 * Authenticate against the OmniStack login-prompt, using username/password or just password
	 * <p>
	 * Cisco hosts may or may not ask for at user-name when a Telnet connection is started.
	 * If the the host asks for at username and none is present a <code>SessionException</code>
	 * will be thrown.
	 * </p>
	 * 
	 * @param username Username to be used for authentication or null if not used
	 * @param password Password to be used for authentication.
	 * @throws SessionException If username is needed and not present or if authentication fails
	 * @throws IOException If communication with remote host fails.
	 */
	public void login(String username, String password) throws SessionException, IOException {
		if ( ! socket.isConnected() ) throw new SessionException("Not Connected!");
		ReadData read = _read2prompt();

		if ( read.getPrompt().equals("User Name:") ) {
			if ( username == null ) throw new SessionException("Need username to Authenticate");
			_sendln(username);
			read = _read2prompt();
		}

		if ( read.getPrompt().equals("Password:")) {
			if ( password == null ) throw new SessionException("Need password to Authenticate");
			_sendln(password);
			read = _read2prompt();
		}

		if ( read.getPrompt().equals("User Name:") || read.getPrompt().equals("Password:") ) {
			throw new SessionException("Authentication failed") ;
		} else  {
            cmd("terminal datadump");
			authenticated = true;
		}

	}

	/**
	 * Execute a command on the remote host, and retrieve the resulting
	 * output.
	 * @param command Command line to be executed on remote host
	 * @return Remote host output
	 * @throws SessionException If unable to execute command.
	 */
	public String[] cmd(String command) throws SessionException {
		try {
			_sendln(command);
			ReadData read = _read2prompt();
			if (!read.isEmpty() && read.get(0).equals(command) ) read.remove(0);
			return read.getDataAsStringArray();
		} catch (IOException ex) {
			throw new SessionException("Unable to send command",ex);
		}
	}

	/**
	 * Switch to enabled mode on remote host
	 * @param password enable secret to be used on the remote host
	 * @throws SessionException If unable to enable.
	 */
	public void enable(String password) throws SessionException {
		try {
			_sendln("enable");
			ReadData read = _read2prompt();
			if ( read.getPrompt().equals("Password:") ) {
				_sendln(password);
			}
			read = _read2prompt();

			if ( ! read.getPrompt().endsWith(ENABLED_PROMPT_CHR) ) {
				throw new SessionException("Failed to enable");
			} else {
				enabled = true;
			}

		} catch (IOException ex) {
			throw new SessionException("Failed to enable",ex);
		}
	}

	/**
	 * Get Authentication status
	 * @return true if authenticated ie. login has been successful
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Get Enabled status
	 * @return true if in enabled mode.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	
	// ---------------------------------- Implementation of SessionOptionHandler
	/**
	 * Implements the <code>SessionOptionHandler</code> interface.
	 * <p>
	 * This basic OptionHandler will turn off all features, it will reply all
	 * DOs with a WON't and all WILLs with a DON'T
	 * <p>
	 * 
	 * @param optionList SessionOptions read by the Session
	 * @return  List of parameters to send to remote host.
	 */
	public SessionOptionList onOptionsRead(SessionOptionList optionList) {
		SessionOptionList sendList = new SessionOptionList();
		for (SessionOption o: optionList ) {
			if ( o.getOptionCode() == SessionOption.DO ) {
				sendList.add( new SessionOption(SessionOption.WONT, o.getOption()) );
			}
			else if ( o.getOptionCode() == SessionOption.WILL ) {
				sendList.add(new SessionOption(SessionOption.DONT, o.getOption()) );
			}
		}
		return sendList;
	}
	
}
