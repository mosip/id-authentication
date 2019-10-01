package io.mosip.kernel.masterdata.httpfilter;

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
import io.mosip.kernel.masterdata.config.LoggerConfiguration;

public class ReqResFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// init method overriding
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
//			System.out.println("Request : " + new String(requestWrapper.getContentAsByteArray(),"UTF-8"));
//			System.out.println("Response : " + new String(responseWrapper.getContentAsByteArray(),"UTF-8"));
			responseWrapper.copyBodyToResponse();
		} catch (Exception e) {
			Logger mosipLogger = LoggerConfiguration.logConfig(ReqResFilter.class);
			mosipLogger.error("", "", "", e.getMessage());
		}
	}

	@Override
	public void destroy() {
		// destroy method overriding
	}

}
