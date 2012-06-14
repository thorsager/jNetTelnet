/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.krakow.jnettelnet;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Extends the <Code>Session</code> to Cisco devices, adding features to handle
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
public class CiscoSession extends dk.krakow.jnettelnet.Session implements SessionOptionHandler {

	/** String that is the last char of an Enabled prompt */
	private static final String ENABLED_PROMPT_CHR = "#";

	/** String Containing the regex. to find prompts */
	private static final String PROMPT_PATTERN = "(User:|Password:|\\w+[>#]|\\w+\\(config\\)#|\\w+\\(config-\\w+\\)#)$" ;

	private boolean authenticated = false;
	private boolean enabled = false;

	/**
	 * Create Session on the default Telnet tcp port-number of 23
	 * @param hostname  Name of remote host
	 */
	public CiscoSession(String hostname) {
		super(hostname, null);
	}

	/**
	 * Create session on a non-standard tcp port-number
	 * @param hostname  Name of remote host
	 * @param port port-number
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public CiscoSession(String hostname, Integer port) {
		super(hostname, port, null,Pattern.compile(PROMPT_PATTERN));
		this.setSessionOptionHandler(this);
	}

	/**
	 * Authenticate against the Cisco login-prompt, using username/password or just password
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

		if ( read.getPrompt().equals("User:") ) {
			if ( username == null ) throw new SessionException("Need username to Authenticate");
			_sendln(username);
			_read2prompt();
		}

		if ( read.getPrompt().equals("Password:")) {
			if ( password == null ) throw new SessionException("Need password to Authenticate");
			_sendln(password);
			read = _read2prompt();
		}

		if ( read.getPrompt().equals("User:") || read.getPrompt().equals("Password:") ) {
			throw new SessionException("Authentication failed") ;
		} else  {
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
			if ( read.get(0).equals(command) ) read.remove(0);
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
	@Override
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
