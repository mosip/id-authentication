package io.mosip.kernel.emailnotification.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Request Response Filter class that implements {@link Filter}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class ReqResFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// over-ridden method
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		ContentCachingRequestWrapper requestWrapper = null;
		ContentCachingResponseWrapper responseWrapper = null;
		
			// Default processing for url ends with .stream
			if (httpServletRequest.getRequestURI().endsWith(".stream")) {
				chain.doFilter(request, response);
				return;
			}
			requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
			responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
			chain.doFilter(requestWrapper, responseWrapper);
			responseWrapper.copyBodyToResponse();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// over-ridden method
	}
}
