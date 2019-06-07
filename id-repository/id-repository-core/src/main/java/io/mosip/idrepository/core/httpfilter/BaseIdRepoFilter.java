package io.mosip.idrepository.core.httpfilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.logger.spi.Logger;
/**
 *
 * @author Manoj SP
 * @author Prem Kumar
 *
 */
@Component
public abstract class BaseIdRepoFilter extends OncePerRequestFilter  {
	/** The Constant ID_REPO_FILTER. */
	private static final String ID_REPO_FILTER = "IdRepoFilter";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(BaseIdRepoFilter.class);

	/** The path matcher. */
	AntPathMatcher pathMatcher = new AntPathMatcher();

	/** The id. */
	@Resource
	private Map<String, String> id;

	String uin;

	/**
	 * Allowed end points.
	 *
	 * @return the string[] allowed endpoints
	 */
	private String[] allowedEndPoints() {
		return new String[] { "/assets/**", "/icons/**", "/screenshots/**", "/favicon**", "/**/favicon**", "/css/**",
				"/js/**", "/**/error**", "/**/webjars/**", "/**/v2/api-docs", "/**/configuration/ui",
				"/**/configuration/security", "/**/swagger-resources/**", "/**/swagger-ui.html" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.filter.OncePerRequestFilter#shouldNotFilter(javax.
	 * servlet.http.HttpServletRequest)
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		if (Objects.nonNull(request.getPathInfo())) {
			return Arrays.stream(allowedEndPoints()).anyMatch(p -> pathMatcher.match(p, request.getPathInfo()));
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Instant requestTime = Instant.now();
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Request Received at: " + requestTime);
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Request URL: " + request.getRequestURL());

		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Request received");

		doFilter(request, response, filterChain);

		Instant responseTime = Instant.now();
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Response sent at: " + responseTime);
		long duration = Duration.between(requestTime, responseTime).toMillis();
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER,
				"Time taken to respond in ms: " + duration
						+ ". Time difference between request and response in Seconds: " + ((double) duration / 1000)
						+ " for url : " + request.getRequestURL() + " method: " + request.getMethod());
	}

	protected abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException;

}
