//package io.mosip.authentication.internal.service.config;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//
//import io.mosip.authentication.core.logger.IdaLogger;
//import io.mosip.kernel.core.logger.spi.Logger;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import org.apache.commons.io.IOUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author Kamesh Shekhar Prasad
// */
//
//@Component
//public class TrailingSlashRedirectFilter implements Filter {
//
//    /** The mosip logger. */
//    private static Logger mosipLogger = IdaLogger.getLogger(TrailingSlashRedirectFilter.class);
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String reqStr = IOUtils.toString(httpRequest.getInputStream(), Charset.defaultCharset());
//        mosipLogger.info("Inside TrailingSlashRedirectFilter");
//        mosipLogger.info("Request Body: " + reqStr);
//        String path = httpRequest.getRequestURI();
//
//        if (path.endsWith("/")) {
//            String newPath = path.substring(0, path.length() - 1);
//            HttpServletRequest newRequest = new CustomHttpServletRequestWrapper(httpRequest, newPath, reqStr);
//            chain.doFilter(newRequest, response);
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
//
//        private final String newPath;
//        private final byte[] cachedBody;
//
//        public CustomHttpServletRequestWrapper(HttpServletRequest request, String newPath, String cachedBodyString) throws IOException {
//            super(request);
//            this.newPath = newPath;
//            this.cachedBody = cachedBodyString.getBytes(StandardCharsets.UTF_8);
//        }
//
//        @Override
//        public String getRequestURI() {
//            return newPath;
//        }
//
//        @Override
//        public StringBuffer getRequestURL() {
//            StringBuffer url = new StringBuffer();
//            url.append(getScheme()).append("://").append(getServerName()).append(":").append(getServerPort())
//                    .append(newPath);
//            return url;
//        }
//
//        @Override
//        public ServletInputStream getInputStream() throws IOException {
//            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
//            return new ServletInputStream() {
//                @Override
//                public int read() throws IOException {
//                    return byteArrayInputStream.read();
//                }
//
//                @Override
//                public boolean isFinished() {
//                    return byteArrayInputStream.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener listener) {
//                    // No-op
//                }
//            };
//        }
//
//        @Override
//        public BufferedReader getReader() throws IOException {
//            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
//        }
//    }
//}
