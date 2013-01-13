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

package dk.krakow.jnettelnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Implements the simple basics of the Telnet Protocol. This
 * makes you able to communicate with any device running a Telnet server.
 * <p>
 * Please note that this Class has NO internal handling of Telnet protocol
 * options, such as Control of Local Echo and stuff like that. That must all
 * be handled through the use of The <code>SessionOptionHandler</code> interface.
 * </p>
 *
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
public class Session {

	private SessionOptionHandler sesOptHand = null;
	private static int IAC = 0xFF;
	private String hostname;
	protected int port;
	protected Socket socket = null;
	private Pattern promptPattern = null;
	protected InputStream input = null;
	protected OutputStream output = null;

	// ---------------------------------------------------------- Public Methods

	/**
	 * Create a Telnet session against a server, using standard tcp port number.
	 * @param hostname Name of the host
	 */
	public Session(String hostname) {
		this(hostname, null, null, null);
	}

	/**
	 * <p>
	 * Create a Telnet session against a server.
	 * </p>
	 * <p>
	 * <b>Note:</b> Using this simple constructor will bake the session ignore ALL read Options.
	 * </p>
	 * 
	 * @param hostname Name of the host
	 * @param port Port on which to connect
	 */
	public Session(String hostname, Integer port) {
		this(hostname, port, null, null);
	}

	/**
	 * Create a Telnet session against a server.
	 * 
	 * @param hostname Name of the host
	 * @param port Port on which to connect
	 * @param sesOptHndlr SessionOptionHandler, class to handle Read Session Options, if set to null all read Options will be ignored
	 * @param promptPattern A regex. pattern used to identify the prompt.
	 */
	public Session(String hostname, Integer port, SessionOptionHandler sesOptHndlr, Pattern promptPattern) {
		if (promptPattern == null) {
			this.promptPattern = Pattern.compile(".*[#\\$:]$");
		} else {
			this.promptPattern = promptPattern;
		}

		this.sesOptHand = sesOptHndlr;
		this.hostname = hostname;
		this.port = port == null ? 23 : port;
	}

	/**
	 * Write/Send a command or string to the server
	 * @param data String of data to be sent
	 * @throws SessionException  If unable to send data to server
	 */
	public void write(String data) throws SessionException {
		try {
			_sendln(data);
		} catch (IOException ex) {
			throw new SessionException("Unable to send data", ex);
		}
	}

	/**
	 * Read/Receive data/command output from the server
	 * @return Array of strings 
	 * @throws SessionException If unable to read data from server
	 */
	public String[] read() throws SessionException {
		try {
			return _read2prompt().getDataAsStringArray();
		} catch (IOException ex) {
			throw new SessionException("Unable to send data", ex);
		}
	}

	/**
	 * Disconnect the session from the host, and close all
	 * steams.
	 */
	public void close() {
		if (input != null) {
			try {
				input.close();
			} catch (IOException ignored) {
			}
		}
		if (output != null) {
			try {
				output.close();
			} catch (IOException ignored) {
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * Connects the session to the host
	 */
	public void connect() throws SessionException {
		try {
			socket = new Socket(hostname, port);

			input = socket.getInputStream();
			output = socket.getOutputStream();

			_readln();// get options
		} catch (UnknownHostException ex) {
			throw new SessionException("Unable to connect to '" + hostname + "'", ex);
		} catch (IOException ex) {
			throw new SessionException("Unable to connect to '" + hostname + "'", ex);
		}
	}

	/**
	 * Set the SessionOptoinHanlder class for the session.
	 * @param sesOptHand Class implementing teh SessionOptionHandler interface.
	 */
	public void setSessionOptionHandler(SessionOptionHandler sesOptHand) {
		this.sesOptHand = sesOptHand;
	}

	/**
	 * Returns whether or not Session is connected to remote host. 
	 * @return true if Session is connected.
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}

	// ------------------------------------------------------- Protected Methods
	/**
	 * Send an Array of bytes to the remote host.
	 * @param data Data to be sent to remote host.
	 * @throws IOException 
	 */
	protected void _send(byte[] data) throws IOException {
		output.write(data);
		output.flush();
	}

	/**
	 * Send a text string to the remote host
	 * @param data String to be sent to remote host
	 * @throws IOException 
	 */
	protected void _sendln(String data) throws IOException {
		_send((data + "\r\n").getBytes());
	}

	/**
	 * <p>
	 * Reads a line of Data from the remote host and collects all Protocol- ,or 
	 * I call them in this project Session- Options in the process.
	 * </p>
	 * <p>
	 * All options are passed to the Session Option handler and the Line of data
	 * is returned.
	 * </p>
	 * <p>
	 * <b>Note:</b>A line of data is defined as what is terminated by "\r\n".
	 * </p>
	 * 
	 * @return line of data read.
	 * @throws IOException if unable to read from Socket.
	 */
	protected String _readln() throws IOException {
		int i;
		SessionOptionList ol = new SessionOptionList();
		StringBuilder buf = new StringBuilder();
		while ((i = input.read()) != -1) {
			if (i == IAC) {
				byte[] opt = new byte[2];
                //noinspection ResultOfMethodCallIgnored
                input.read(opt);
				ol.add(new SessionOption(opt[0], opt[1]));
				continue;
			}
			char c = (char) i;
			buf.append(c);
			if (buf.toString().endsWith("\r\n")) {
				break;
			}
			Matcher matcher = promptPattern.matcher(buf.toString());
			if (matcher.find()) {
				break;
			}
		}

		// If options was read, call onReadOptions hook...
		if (!ol.isEmpty()) {
			onReadOptions(ol);
		}

		if (buf.length() == 0) {
			return null;
		} else {
			return buf.toString().replaceAll("\r\n", "");
		}
	}

	/**
	 * Reads data "on-line-at-the-time" as it is done by <code>_readln</code> thus
	 * handling SessionOptions along the way, until what passes for a Prompt is 
	 * reached.<br>
	 * Default prompt matching patter is <i>".*[#\\$:]$"</i>
	 * @return Data read from remote host
	 * @throws IOException  If unable to read data from remote host.
	 */
	protected ReadData _read2prompt() throws IOException {
		ReadData data = new ReadData();
		String line;
		while ((line = _readln()) != null) {
			Matcher matcher = promptPattern.matcher(line);
			if (matcher.find()) {
				if (matcher.groupCount() > 0) {
					data.setPrompt(matcher.group(1));
				} else {
					data.add(line);
				}
				break;
			}
			data.add(line);
		}
        if (data.getPrompt() == null) {
            throw new IOException("Did not find Prompt in read data");
        }
		return data;
	}

	// --------------------------------------------------------- Private Methods
	/**
	 * Gets called whenever <code>_readln</code> has encountered SessionOptions
	 * to forward the received options to the SessionOptionHandler.<br>
	 * If the SessionOptionHandler returns a non-empty <code>SessionOptionList</code>
	 * the content of this list, will be sent to the remote host.
	 * @param ol List of Options read by <code>_readln</n>
	 * @throws IOException 
	 */
	private void onReadOptions(SessionOptionList ol) throws IOException {
		if (sesOptHand != null) {
			System.out.println("got: " + ol);
			SessionOptionList sendlist = sesOptHand.onOptionsRead(ol);
			_send(sendlist.getBytes());
			System.out.println("send: " + sendlist);
		} else {
			System.out.println("ignoring: " + ol);
			// ignore alle options, if we have no option handler..
		}
	}

//---------------------------------------------------------------- Inner Classes
	/**
	 * Utility class used as a buffer for the data read from remote host, and
	 * to keep track of any prompts.
	 */
	protected class ReadData extends ArrayList<String> {

		private String prompt;

		public String[] getDataAsStringArray() {
			return this.toArray(new String[this.size()]);
		}

		protected String getPrompt() {
			return prompt;
		}

		protected void setPrompt(String prompt) {
			this.prompt = prompt;
		}

		@Override
		public String toString() {
			return "ReadData{" + "prompt=" + prompt + ", buffer=" + super.toString() + '}';
		}
	}
}
