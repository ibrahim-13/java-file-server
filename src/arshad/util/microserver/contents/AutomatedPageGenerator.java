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

package arshad.util.microserver.contents;

import arshad.util.microserver.controllers.HeaderMgr;
import arshad.util.microserver.controllers.ResponseData;
import arshad.util.microserver.logging.ConOutput;
import arshad.util.microserver.misc.SizeCalcualtor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

/**
 *
 * @author Arshad
 */
public class AutomatedPageGenerator {
    
    private static final StaticContentLoader CONTENTS = StaticContentLoader.getDefault();
    private static volatile AutomatedPageGenerator defaultInstance;
    
    private final Object PAGELOCK;
    private final Object ROOTPAGELOCK;
    private final Object ERRORPAGELOCK;
    
    private AutomatedPageGenerator() {
        PAGELOCK = new Object();
        ROOTPAGELOCK = new Object();
        ERRORPAGELOCK = new Object();
        
    }
    
    public static AutomatedPageGenerator getDefault() {
        AutomatedPageGenerator instance = AutomatedPageGenerator.defaultInstance;
        if(instance == null) {
            synchronized(AutomatedPageGenerator.class) {
                instance = AutomatedPageGenerator.defaultInstance;
                if(instance == null) {
                    AutomatedPageGenerator.defaultInstance = instance = new AutomatedPageGenerator();
                }
            }
        }
        return instance;
    }
    
    public ResponseData getErrorPage(String msg) {
        synchronized(ERRORPAGELOCK) {
            byte[] retBytes = CONTENTS.getErrorHTML().replace("{{message}}", msg).getBytes();
            InputStream retIS = new ByteArrayInputStream(retBytes);
            List headers = HeaderMgr.getHeader(HeaderMgr.Operations.HTTP_VIEW, null);
            int responseCode = HttpURLConnection.HTTP_OK;
            ResponseData ret = new ResponseData(responseCode, headers, retIS, retBytes.length);
            return ret;
        }
    }
    
    public ResponseData getRootPage(File root) {
        synchronized(ROOTPAGELOCK) {
            StringBuilder sb = new StringBuilder();
            for(String aa : root.list()) {
                File currFile = new File(aa);
                String size = SizeCalcualtor.getFormatedSize(currFile.length());
                sb.append("<tr><td><a href=\"/")
                        .append(root.getPath().substring(1))
                        .append(aa)
                        .append("\">")
                        .append(aa)
                        .append("</a></td><td>")
                        .append(currFile.isDirectory() ? "Directory" : "File")
                        .append("</td><td>")
                        .append(size)
                        .append("</td></tr>\n");
            }
            String ret = CONTENTS.getFileHTML();

            byte[] retBytes = (ret.replace("{{filelist}}", sb.toString()).replace("{{location}}", "/")).getBytes();
            InputStream retIS = new ByteArrayInputStream(retBytes);
            List headers = HeaderMgr.getHeader(HeaderMgr.Operations.HTTP_VIEW, null);
            int responseCode = HttpURLConnection.HTTP_OK;
            ResponseData retData = new ResponseData(responseCode, headers, retIS, retBytes.length);
            return retData;
        }
    }
    
    public ResponseData getPage(String url) throws Exception {
        synchronized(PAGELOCK) {
            File file = new File("." + url);

            if(file.isDirectory()) {
                String ret = CONTENTS.getFileHTML();
                StringBuilder sb = new StringBuilder();

                String parent;
                if(file.getParent().equals(".")) {
                    parent = "/";
                } else {
                    parent = "/" + file.getParent().substring(2);
                }
                sb.append("<tr><td><a href=\"")
                            .append(parent)
                            .append("\">")
                            .append("Parent")
                            .append("</a></td></tr>\n");
                for(String aa : file.list()) {
                    File currFile = new File("." + url + "/" + aa);
                    ConOutput.Print("." + url + "/" + aa);
                    ConOutput.Print(file.getPath().substring(2).replace("\\", "/"));
                    String size = SizeCalcualtor.getFormatedSize(currFile.length());
                    sb.append("<tr><td><a href=\"")
                            .append(url)
                            .append("/")
                            .append(aa)
                            .append("\">")
                            .append(aa)
                            .append("</a></td><td>")
                            .append(currFile.isDirectory() ? "Directory" : "File")
                            .append("</td><td>")
                            .append(size)
                            .append("</td></tr>\n");
                }

                byte[] retBytes = (ret.replace("{{filelist}}", sb.toString()).replace("{{location}}", url)).getBytes();
                InputStream retIS = new ByteArrayInputStream(retBytes);
                List headers = HeaderMgr.getHeader(HeaderMgr.Operations.HTTP_VIEW, null);
                int responseCode = HttpURLConnection.HTTP_OK;
                ResponseData retData = new ResponseData(responseCode, headers, retIS, retBytes.length);
                return retData;
            } else if(file.isFile()) {
                InputStream retIS = new FileInputStream(file);
                long length = file.length();
                List headers = HeaderMgr.getHeader(HeaderMgr.Operations.HTTP_DOWNLOAD, file);
                int responseCode = HttpURLConnection.HTTP_OK;

                ResponseData retData = new ResponseData(responseCode, headers, retIS, length);
                return retData;
            } else {
                return getErrorPage("File does not exist in this server.");
            }
        }
    }
}
