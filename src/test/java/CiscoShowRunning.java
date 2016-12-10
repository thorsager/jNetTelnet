/*
 * Copyright (c) 2016, Michael Thorsager <thorsager@gmail.com>
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

import dk.krakow.jnettelnet.CiscoSession;
import dk.krakow.jnettelnet.OmniStackSession;
import dk.krakow.jnettelnet.SessionException;

import java.io.IOException;

/**
 * This class is a "working" example of how the library can be use, but is VERY
 * simplistic and I only use it because I'm to lazy to write some real tests..
 */
public class CiscoShowRunning {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("USAGE: java CiscoShowRunning <ip/hostname> <username> <password> [enable-password]");
            System.exit(1);
        }

        CiscoSession bs = new CiscoSession(args[0]);
        try {
            bs.connect();
            bs.login(args[1],args[2]);
            if (args.length == 4) bs.enable(args[3]);

            String[] output = bs.cmd("show running");
            for ( String s: output) System.out.println(s);
            bs.close();
        } catch (SessionException se) {
            se.printStackTrace();
        }

    }
}
