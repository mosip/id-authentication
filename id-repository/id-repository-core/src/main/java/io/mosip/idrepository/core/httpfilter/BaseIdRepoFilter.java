package io.mosip.idrepository.core.httpfilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class BaseIdRepoFilter - base class that logs request time and time taken
 * to complete the request.
 *
 * @author Manoj SP
 * @author Prem Kumar
 */
@Component
public abstract class BaseIdRepoFilter implements Filter  {
	
	/** The Constant ID_REPO_FILTER. */
	private static final String ID_REPO_FILTER = "IdRepoFilter";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(BaseIdRepoFilter.class);
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Instant requestTime = Instant.now();
		mosipLogger.debug(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER, "Request Received at: " + requestTime);
		mosipLogger.debug(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER, "Request URL: " + ((HttpServletRequest) request).getRequestURL());

		mosipLogger.debug(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER, "Request received");
		String responseString = buildResponse((HttpServletRequest) request);
		if (Objects.isNull(responseString)) {
			chain.doFilter(request, response);
		} else {
			response.getWriter().write(responseString);
		}
		Instant responseTime = Instant.now();
		mosipLogger.debug(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER, "Response sent at: " + responseTime);
		long duration = Duration.between(requestTime, responseTime).toMillis();
		mosipLogger.debug(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER,
				"Time taken to respond in ms: " + duration
						+ ". Time difference between request and response in Seconds: " + ((double) duration / 1000)
						+ " for url : " + ((HttpServletRequest) request).getRequestURL() + " method: "
						+ ((HttpServletRequest) request).getMethod());
	}

	/**
	 * Builds the response.
	 *
	 * @param request the request
	 * @return the string
	 */
	protected abstract String buildResponse(HttpServletRequest request);

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
