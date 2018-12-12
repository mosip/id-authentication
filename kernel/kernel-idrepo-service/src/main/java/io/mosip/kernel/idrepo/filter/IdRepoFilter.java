package io.mosip.kernel.idrepo.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Lists;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idrepo.config.IdRepoLogger;

/**
 * The Class IdRepoFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoFilter extends OncePerRequestFilter  {
	
	/** The Constant ID_REPO_FILTER. */
	private static final String ID_REPO_FILTER = "IdRepoFilter";
	
	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";
	
	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoFilter.class);
	
	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		Instant requestTime = Instant.now();
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Request Received at: " + requestTime);
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Request URL: " + request.getServletPath());
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(request);
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Request body : \n" 
		+ IOUtils.toString(requestWrapper.getInputStream(), Charset.defaultCharset()));
		requestWrapper.resetInputStream();
		
		CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
		
		filterChain.doFilter(requestWrapper, responseWrapper);
		
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Response body : \n" 
				+ responseWrapper.toString());
		
		response.getWriter().write(responseWrapper.toString());
		
		Instant responseTime = Instant.now();
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Response sent at: " + responseTime);
		long duration = Duration.between(requestTime, responseTime).toMillis();
		mosipLogger.info(SESSION_ID, ID_REPO, ID_REPO_FILTER, "Time taken to respond in ms: " 
		+ duration
		+ ". Time difference between request and response in Seconds: " + (((double) duration / 1000) % 60));
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		AntPathMatcher pathMatcher = new AntPathMatcher();
	    return Lists.newArrayList("").stream()
	        .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
	}

}
