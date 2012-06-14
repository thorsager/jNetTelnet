package dk.krakow.jnettelnet;

/**
 * Describes the SessionOptionHandlers, all classes that are to handle 
 * SessionOptions from the Session must implement this interface.
 * 
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
public interface SessionOptionHandler {

	/**
	 * Method that is call from the <code>Session</code> with a list of SessionOptions
	 * read from the remote host.
	 * <p>
	 * The handler must read and "understand" all the options, and generate a new
	 * set of options that are to be transmitted to the remote host.
	 * </p>
	 * <p>
	 * Any options sent to remote host should confirm to the protocol described 
	 * in RFC-854 and RFC-855
	 * </p>
	 * 
	 * @param optionList Options read from the remote host
	 * @return  Options to be sent to the remote host.
	 */
	public SessionOptionList onOptionsRead(SessionOptionList optionList);
}
