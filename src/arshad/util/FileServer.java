//    Copyright (C) 2017 MD. Ibrahim Khan
//
//    Project Name: 
//    Author: MD. Ibrahim Khan
//    Author's Email: ib.arshad777@gmail.com
//
//    Redistribution and use in source and binary forms, with or without modification,
//    are permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this
//       list of conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above copyright notice, this
//       list of conditions and the following disclaimer in the documentation and/or
//       other materials provided with the distribution.
//
//    3. Neither the name of the copyright holder nor the names of the contributors may
//       be used to endorse or promote products derived from this software without
//       specific prior written permission.
//
//    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//    IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
//    INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING
//    BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//    DATA, OR PROFITS; OR BUSINESS INTERRUPTIONS) HOWEVER CAUSED AND ON ANY THEORY OF
//    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
//    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
//    OF THE POSSIBILITY OF SUCH DAMAGE.

package arshad.util;

import arshad.util.microserver.MainHttpServer;
import arshad.util.microserver.controllers.handlers.WebHandler;
import arshad.util.microserver.controllers.handlers.WebHandlerImpl;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arshad
 */
public class FileServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WebHandler handler = new WebHandlerImpl(new File("."));
        final MainHttpServer server = new MainHttpServer();
        try {
            server.start(handler);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
            }));
        } catch (Exception ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread animationThread = new Thread(() -> {
            while(true) {
                try {
                    System.out.print("\r        ");
                    System.out.print("\r=");
                    Thread.sleep(500);
                    System.out.print("\r        ");
                    System.out.print("\r==");
                    Thread.sleep(500);
                    System.out.print("\r        ");
                    System.out.print("\r===");
                    Thread.sleep(500);
                    System.out.print("\r        ");
                    System.out.print("\r==");
                    Thread.sleep(500);
                    System.out.print("\r        ");
                    System.out.print("\r=");
                    Thread.sleep(500);
                    System.out.print("\r        ");
                    System.out.print("\r");
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        animationThread.setDaemon(true);
        animationThread.start();
    }
    
}
