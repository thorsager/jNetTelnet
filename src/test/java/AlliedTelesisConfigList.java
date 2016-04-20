import dk.krakow.jnettelnet.AlliedTelesisSession;
import dk.krakow.jnettelnet.OmniStackSession;
import dk.krakow.jnettelnet.SessionException;

import java.io.IOException;

/**
 * This class is a "working" example of how the library can be use, but is VERY
 * simplistic and I only use it because I'm to lazy to write some real tests..
 */
public class AlliedTelesisConfigList {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("USAGE: java alliedTelesisConfigList <ip/hostname> <username> <password> ");
            System.exit(1);
        }

        AlliedTelesisSession bs = new AlliedTelesisSession(args[0]);
        try {
            bs.connect();
            bs.login(args[1],args[2]);

            String[] output = bs.cmd("system config list");
            for ( String s: output) System.out.println(s);
            bs.close();
        } catch (SessionException se) {
            se.printStackTrace();
        }

    }
}
