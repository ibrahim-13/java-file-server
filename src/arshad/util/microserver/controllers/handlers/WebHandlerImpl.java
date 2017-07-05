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

package arshad.util.microserver.controllers.handlers;

import arshad.util.microserver.contents.AutomatedPageGenerator;
import arshad.util.microserver.controllers.ResponseData;
import arshad.util.microserver.logging.ConOutput;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arshad
 */
public class WebHandlerImpl implements WebHandler {
    
    private final File root;
    private final Map<String, HttpHandler> handlerList;
    
    public WebHandlerImpl(File rootDirectory) {
        root = rootDirectory;
        handlerList = new HashMap<>();
    }

    @Override
    public Map<String, HttpHandler> getHandlers() {
        handlerList.put("/video", ((httpExchange) -> {
            String response = "<html>\n" +
                        "<head>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "	<h1>test</ht>\n" +
                        "	<video width=\"640\" hight\"480\" controls>\n" +
                        "		<source src=\"/files/aa.mp4\" type=\"video/mp4\">\n" +
                        "	</video>\n" +
                        "</body>\n" +
                        "</html>";
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
                os.flush();
            }
        }));
        handlerList.put("/favicon.ico", ((httpExchange) -> {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.flush();
            }
        }));
        handlerList.put("/", ((HttpExchange httpExchange) -> {
            String reqURL = httpExchange.getRequestURI().toString();
            reqURL = URLDecoder.decode(reqURL, "UTF-8");
            ConOutput.Print(httpExchange.getProtocol() + " : " + httpExchange.getRequestMethod() + " : " + reqURL);
            AutomatedPageGenerator pageGen = AutomatedPageGenerator.getDefault();
            
            ResponseData responseData;
            if(reqURL.equals("/")) {
                responseData = pageGen.getRootPage(root);
            } else if(reqURL.startsWith("/")) {
                try {
                    responseData = pageGen.getPage(reqURL);
                } catch (Exception ex) {
                    responseData = pageGen.getErrorPage("Error reading file from source"); 
                    Logger.getLogger(WebHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                responseData = pageGen.getErrorPage("No mapping available for this URL");               
            }
            
            responseData.getHeaders().forEach((pairs) -> {
                httpExchange.getResponseHeaders().add(pairs.getHeaderField(), pairs.getValue());
            });
            httpExchange.sendResponseHeaders(responseData.gettHttpConnectionCode(), responseData.getLength());
            try (OutputStream os = httpExchange.getResponseBody()) {
                byte[] buffer = new byte[1024];
                int read;
                OUTER:
                while(true) {
                    read = responseData.getData().read(buffer);
                    switch(read) {
                        case -1: {
                            break OUTER;
                        }
                        case 1024: {
                            os.write(buffer);
                            os.flush();
                            break;
                        }
                        default: {
                            byte[] tmp = new byte[read];
                            System.arraycopy(buffer, 0, tmp, 0, read);
                            os.write(tmp);
                            os.flush();
                            break OUTER;
                        }
                    }
                }
                os.flush();
                responseData.getData().close();
            }
        }));
        return handlerList;
    }
    
}
