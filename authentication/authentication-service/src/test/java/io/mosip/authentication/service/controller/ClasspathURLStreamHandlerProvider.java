package io.mosip.authentication.service.controller;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

/**
 * This is used to load the mosip-cbeff.xsd from the classpath into the cbeffutil.
 * 
 * @author Loganathan Sekar
 *
 */
public class ClasspathURLStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("classpath".equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return ClassLoader.getSystemClassLoader().getResource(u.getPath()).openConnection();
                }
            };
        }
        return null;
    }

}