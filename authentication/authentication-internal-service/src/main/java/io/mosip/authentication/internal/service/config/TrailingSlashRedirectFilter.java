package io.mosip.authentication.internal.service.config;

import java.io.IOException;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

@Component
public class TrailingSlashRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Check if request is of type ObjectWithMetadata, preserve it unchanged
        if (request instanceof ObjectWithMetadata) {
            // Continue with the filter chain, pass the original request unchanged
            chain.doFilter(request, response);
            return;
        }

        // For other types of requests, handle the trailing slash redirection
        if (path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            HttpServletRequest newRequest = new CustomHttpServletRequestWrapper(httpRequest, newPath);
            chain.doFilter(newRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    // Custom wrapper to modify request URI without altering original request structure
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

