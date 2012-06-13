package dk.krakow.net.telnet;

/**
 * Exception class to bundle up most of the Exceptions that
 * can be thrown from within the Session
 * 
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
public class SessionException extends Exception {

	public SessionException(Throwable thrwbl) {
		super(thrwbl);
	}

	public SessionException(String string, Throwable thrwbl) {
		super(string, thrwbl);
	}

	public SessionException(String string) {
		super(string);
	}

	public SessionException() {
	}
	
}
