package io.mosip.kernel.auth.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***********************************************************************************************************************
 * AUTH HEADERS FILTER
 * This filter is going to act as a CORS filter. It is assigned before AuthFilter in the filter chain.
 *
 * Tasks:
 * 1. Stores auth token to be used throughout the cycle across implementations.
 * 2. Sets headers to allow cross origin requests.
 * 3. Sets header to allow and expose "Authorization" header.
 * 4. Sets the authToken back to null.
 **********************************************************************************************************************/

@Component
public class AuthHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "content-type, Authorization");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}