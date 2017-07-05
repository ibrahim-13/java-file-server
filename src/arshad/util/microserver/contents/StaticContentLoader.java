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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Arshad
 */
public class StaticContentLoader {
    private static final String HTML_ERROR = "/arshad/util/microserver/contents/html/errorPage.html";
    private static final String HTML_FILE = "/arshad/util/microserver/contents/html/filePage.html";
    
    private final String loadedHtmlError;
    private final String loadedHtmlFile;
    
    private final Object lockHtmlError;
    private final Object lockHtmlFile;
    
    private final int bufferSize = 1024;
    
    private static volatile StaticContentLoader defaultInstance;
    
    private StaticContentLoader() {
        lockHtmlError = new Object();
        lockHtmlFile = new Object();
        
        loadedHtmlError = loadContent(HTML_ERROR);
        loadedHtmlFile = loadContent(HTML_FILE);
    }
    
    private String loadContent(String resourceLocation) {
        InputStream is = StaticContentLoader.class.getResourceAsStream(resourceLocation);
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        OUTER:
        while(true) {
            try {
                i = is.read(buffer);
                switch(i) {
                    case -1:
                        break OUTER;
                    case bufferSize:
                        baos.write(buffer);
                    default:
                        byte[] tmp = new byte[i];
                        System.arraycopy(buffer, 0, tmp, 0, i);
                        baos.write(tmp);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        String tmp = "Error loading the error page, meh !!!";
        try {
             tmp = new String(baos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return tmp;
    }
    
    public static StaticContentLoader getDefault() {
        StaticContentLoader instance = StaticContentLoader.defaultInstance;
        if(instance == null) {
            synchronized(StaticContentLoader.class) {
                instance = StaticContentLoader.defaultInstance;
                if(instance == null) {
                    StaticContentLoader.defaultInstance = instance = new StaticContentLoader();
                }
            }
        }
        return instance;
    }
    
    public String getErrorHTML() {
        synchronized(lockHtmlError) {
            return loadedHtmlError;
        }
    }
    
    public String getFileHTML() {
        synchronized(lockHtmlFile) {
            return loadedHtmlFile;
        }
    }
}
