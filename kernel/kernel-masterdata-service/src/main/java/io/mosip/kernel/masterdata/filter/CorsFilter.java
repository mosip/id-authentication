package io.mosip.kernel.masterdata.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is a filter for giving Access Headers to solve CORS
 * 
 * @author Mindtree Ltd.
 *
 */
public class CorsFilter implements Filter {
	/**
	 * Default Constructor
	 */
	public CorsFilter() {
		// Default Constructor
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String origin = request.getHeader("Origin");
		if (origin != null && !origin.isEmpty()) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		}
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"Date, Content-Type, Accept, X-Requested-With, Authorization, From, X-Auth-Token, Request-Id");
		response.setHeader("Access-Control-Expose-Headers", "Date");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		/*
		 * response.setHeader("X-Frame-Options", "SAMEORIGIN");
		 * response.setHeader("X-Content-Type-Options", "nosniff");
		 * response.setHeader("X-XSS-Protection", "1; mode=block");
		 * response.setHeader("Cache-Control", "No-store"); response.setHeader("Pragma",
		 * "no-cache");
		 */
		if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		// init method from Filter
	}

	@Override
	public void destroy() {
		// destroy method from Filter
	}
}