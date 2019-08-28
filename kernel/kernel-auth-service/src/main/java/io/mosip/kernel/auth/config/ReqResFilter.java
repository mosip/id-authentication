package io.mosip.kernel.auth.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * This class is for input logging of all parameters in HTTP requests
 * 
 * @author Bal Vikash Sharma
 *
 */
public class ReqResFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// init method overriding
	}

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
		System.out.println(DateUtils.getUTCCurrentDateTimeString()+" Request : "+new String(requestWrapper.getContentAsByteArray()));
		System.out.println(DateUtils.getUTCCurrentDateTimeString()+" Response : "+new String(responseWrapper.getContentAsByteArray()));
		System.out.println(DateUtils.getUTCCurrentDateTimeString()+" Cookie header in Response :"+httpServletResponse.getHeader("Set-Cookie"));
		responseWrapper.copyBodyToResponse();

	}

	@Override
	public void destroy() {
		// Auto-generated method stub
	}
}
