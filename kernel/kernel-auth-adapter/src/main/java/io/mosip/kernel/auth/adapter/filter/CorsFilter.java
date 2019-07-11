package io.mosip.kernel.auth.adapter.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/***********************************************************************************************************************
 * AUTH HEADERS FILTER This filter is going to act as a CORS filter. It is
 * assigned before AuthFilter in the filter chain.
 *
 * Tasks: 1. Sets headers to allow cross origin requests. 2. Sets header to
 * allow and expose "Authorization" header.
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String origin = request.getHeader("Origin");
		if (origin != null && !origin.isEmpty()) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		}
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
		response.setHeader("Access-Control-Allow-Headers",
				"Date, Content-Type, Accept, X-Requested-With, Authorization, From, X-Auth-Token, Request-Id");
		response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
		}
	}
}