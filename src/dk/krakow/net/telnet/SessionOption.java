/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.krakow.net.telnet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains a Telnet Protocol option, and handles the unsigned byte
 * stuff of the protocol in the Java environment.
 *
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
class SessionOption {
	private int optionCode;
	private int option;

	public static int WILL = 0xFB;
	public static int WONT = 0xFC;
	public static int DO = 0xFD;
	public static int DONT = 0xFE;

	private static final Map<Integer,String> ocNames ;
	private static final Map<Integer,String> oNames ;
	static {
		Map<Integer, String> oc = new HashMap<Integer,String>();
        oc.put(250, "Subnegotiation");
        oc.put(251, "Will");
        oc.put(252, "Won't");
        oc.put(253, "Do");
        oc.put(254, "Don't");
        ocNames = Collections.unmodifiableMap(oc);

		Map<Integer, String> o = new HashMap<Integer,String>();
        o.put(1, "Echo");
        o.put(3, "Suppress Go Ahead");
        o.put(5, "Status");
        o.put(6, "Timing Mark");
        o.put(10, "Output Carriage-Return Disposition");
        o.put(11, "Output Horizontal Tab Stops");
        o.put(12, "Output Horizontal Tab Stop Disposition");
        o.put(13, "Output Formfeed Disposition");
        o.put(14, "Output Vertical Tabstops");
        o.put(15, "Output Vertical Tab Disposition");
        o.put(16, "Output Linefeed Disposition");
        o.put(17, "Extended ASCII");
        o.put(24, "Terminal Type");
        o.put(31, "Negotiate About Window Size");
        o.put(32, "Terminal Speed");
        o.put(33, "Remote Flow Control");
        o.put(34, "Linemode");
        o.put(34, "Linemode");
        o.put(37, "Linemode");
        oNames = Collections.unmodifiableMap(o);
	}

	/** 
	 * Create the option from two bytes
	 * @param optionCode Option code byte
	 * @param option  option byte
	 */
	public SessionOption(byte optionCode, byte option) {
		this.optionCode |= optionCode & 0xFF ;
		this.option |= option & 0xFF;
	}

	/**
	 * Create the option from two ints, as this is how java would like
	 * us to handle unsigned bytes.
	 * @param optionCode Option code byte
	 * @param option  Option byte
	 */
	public SessionOption(int optionCode, int option) {
		this.optionCode = optionCode;
		this.option = option;
	}

	/**
	 * Get option part of the SessionOption
	 * @return option byte (as an int)
	 */
	public int getOption() {
		return option;
	}

	/**
	 * Get option code part of the SessionOption
	 * @return option code byte (as an int)
	 */
	public int getOptionCode() {
		return optionCode;
	}

	/**
	 * Convert the option to a byte array, ready to be transmitted to remote host
	 * @return  option as byte array.
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[] {(byte)0xFF, (byte)optionCode, (byte)option};
		return bytes;
	}

	@Override
	public String toString() {
		return ocNames.get(optionCode)+"("+optionCode+") "+oNames.get(option) +"("+option+")";
	}
}
