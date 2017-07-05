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

package arshad.util.microserver;

import arshad.util.microserver.controllers.handlers.WebHandler;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import arshad.util.microserver.logging.ConOutput;

/**
 *
 * @author Arshad
 */
public class MainHttpServer {
    
    private int port;
    private int backlog;
    private int numOfThreads;
    private HttpServer mainServer;
    private boolean serverStarted;
    
    public MainHttpServer() {
        this(8000, 0, 20);
    }
    
    public MainHttpServer(int port, int backlog, int numOfThreads) {
        this.port = port;
        this.backlog = backlog;
        this.numOfThreads = numOfThreads;
        serverStarted = false;
    }
    
    public void start(WebHandler handlers) throws Exception {
        if(!serverStarted) {
            mainServer = HttpServer.create(new InetSocketAddress(port), backlog);
            mainServer.setExecutor(Executors.newFixedThreadPool(numOfThreads));
            handlers.getHandlers().forEach((String mappedLocation, HttpHandler handler) -> {
                mainServer.createContext(mappedLocation, handler);
            });
            ConOutput.Print("Starting server...");
            mainServer.start();
            serverStarted = true;
            ConOutput.Print("Server started : STATUS OK");
        } else {
            throw new Exception("Server already started");
        }
    }
    
    public void stop() {
        if(serverStarted) {
            mainServer.stop(0);
            serverStarted = false;
            ConOutput.Print("Server stopped...");
        }
    }
}
