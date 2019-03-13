package io.mosip.kernel.lkeymanager.config;

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

/**
 * Class that implements {@link Filter} and serves as the filter for
 * Request-Response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class ReqResFilter implements Filter {
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// over-ridden method
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		ContentCachingRequestWrapper requestWrapper = null;
		ContentCachingResponseWrapper responseWrapper = null;
		try {
			if (httpServletRequest.getRequestURI().endsWith(".stream")) {
				chain.doFilter(request, response);
				return;
			}
			requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
			responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);
			chain.doFilter(requestWrapper, responseWrapper);
			responseWrapper.copyBodyToResponse();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void destroy() {
		// over-ridden method
	}
}
