package io.mosip.authentication.common.service.config;

import java.io.IOException;
import java.nio.charset.Charset;

import io.mosip.authentication.common.service.filter.BaseAuthFilter;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Kamesh Shekhar Prasad
 */

@Component
public class TrailingSlashRedirectFilter implements Filter {

    /** The mosip logger. */
    private static Logger mosipLogger = IdaLogger.getLogger(TrailingSlashRedirectFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String reqStr = IOUtils.toString(httpRequest.getInputStream(), Charset.defaultCharset());
        mosipLogger.info("inside Trailingslashredirectfilter");
        mosipLogger.info("request-"+reqStr);
        String path = httpRequest.getRequestURI();

        if (path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            HttpServletRequest newRequest = new CustomHttpServletRequestWrapper(httpRequest, newPath);
            chain.doFilter(newRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final String newPath;

        public CustomHttpServletRequestWrapper(HttpServletRequest request, String newPath) {
            super(request);
            this.newPath = newPath;
        }

        @Override
        public String getRequestURI() {
            return newPath;
        }

        @Override
        public StringBuffer getRequestURL() {
            StringBuffer url = new StringBuffer();
            url.append(getScheme()).append("://").append(getServerName()).append(":").append(getServerPort())
                    .append(newPath);
            return url;
        }
    }
}
