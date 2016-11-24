package dk.krakow.jnettelnet;

import org.junit.Test;

/**
 * Simple testcase to test Connect Timeout
 */
public class AlliedTelesisSessionTest {

    @Test(expected = SessionException.class)
    public void not_connected() throws Exception {
        Session s = new AlliedTelesisSession("192.168.200.94",23);
        s.connect();
    }

}