package io.mosip.authentication.common.service.config;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import jakarta.servlet.ServletException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TrailingSlashRedirectFilterTest {

    private TrailingSlashRedirectFilter filter;
    private HttpServletRequest request;
    private ServletResponse response;
    private FilterChain chain;

    @Before
    public void setup() {
        filter = new TrailingSlashRedirectFilter();
        request = mock(HttpServletRequest.class);
        response = mock(ServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void testDoFilterTrailingSlash() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/test/");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);

        filter.doFilter(request, response, chain);

        // Capture the HttpServletRequest passed to chain
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        verify(chain).doFilter(requestCaptor.capture(), eq(response));

        HttpServletRequest wrappedRequest = requestCaptor.getValue();
        assertEquals("/test", wrappedRequest.getRequestURI());

        // Call getRequestURL() to cover that method
        StringBuffer url = wrappedRequest.getRequestURL();
        assertTrue(url.toString().contains("/test"));
        assertTrue(url.toString().startsWith("http://localhost:8080"));
    }

    @Test
    public void testDoFilterNoTrailingSlash() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, chain);

        // The same request should be passed
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilterObjectWithMetadata() throws IOException, ServletException {
        // Mock HttpServletRequest that ALSO implements ObjectWithMetadata
        ServletRequest objRequest = mock(HttpServletRequest.class,
                withSettings().extraInterfaces(ObjectWithMetadata.class));

        // Call the filter
        filter.doFilter(objRequest, response, chain);

        // Verify the same request object is passed unchanged
        verify(chain).doFilter(objRequest, response);
    }

}
