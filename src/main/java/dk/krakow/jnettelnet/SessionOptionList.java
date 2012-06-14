package dk.krakow.jnettelnet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Very simple Extension of ArrayList<SessionOption> that handles the 
 * conversion from SessionOptions to bytes that can be transmitted to
 * remote host.
 *
 * @author Michael Thorsager &lt;thorsager@gmail.com&gt;
 */
public class SessionOptionList extends ArrayList<SessionOption> {

	/**
	 * Convert all Options in list to a single array of bytes
	 * @return array of bytes ready for transfer.
	 */
	public byte[] getBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] array = null;
		for (SessionOption o: this) {
			try {
				baos.write(o.getBytes());
				array = baos.toByteArray();
				baos.close();
			} catch (IOException ignored) { }
		}
		return array != null ? array : new byte[0] ;
	}
	
}
