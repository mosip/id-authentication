package io.mosip.kernel.idrepo.httpfilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.idrepo.constant.IdRepoConstants;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idrepo.config.IdRepoLogger;

/**
 * The Class IdRepoFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoFilter extends OncePerRequestFilter {

	/** The Constant GET. */
	private static final String GET = "GET";

	/** The Constant ID_REPO_FILTER. */
	private static final String ID_REPO_FILTER = "IdRepoFilter";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The Constant READ. */
	private static final String READ = "read";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant TYPE. */
	private static final String TYPE = "type";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoFilter.class);

	/** The path matcher. */
	AntPathMatcher pathMatcher = new AntPathMatcher();

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

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
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER,
				"Request URL: " + request.getRequestURL() + "  Method : " + request.getMethod());

		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Request received");

		if (request.getMethod().equals(GET) && (request.getParameterMap().size() > 1
				|| (request.getParameterMap().size() == 1 && !request.getParameterMap().containsKey(TYPE)))) {
			response.getWriter().write(buildErrorResponse());
		} else {
			filterChain.doFilter(request, response);
		}

		Instant responseTime = Instant.now();
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER, "Response sent at: " + responseTime);
		long duration = Duration.between(requestTime, responseTime).toMillis();
		mosipLogger.debug(uin, ID_REPO, ID_REPO_FILTER,
				"Time taken to respond in ms: " + duration
						+ ". Time difference between request and response in Seconds: " + ((double) duration / 1000)
						+ " for url : " + request.getRequestURL() + " method: " + request.getMethod());
	}

	/**
	 * Builds the error response.
	 *
	 * @return the string
	 */
	private String buildErrorResponse() {
		try {
			IdResponseDTO response = new IdResponseDTO();
			response.setId(id.get(READ));
			response.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
			ServiceError errors = new ServiceError(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(),
					IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage());
			response.setErrors(Collections.singletonList(errors));
			return mapper.writeValueAsString(response);
		} catch (IOException e) {
			mosipLogger.error(uin, ID_REPO, ID_REPO_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.UNKNOWN_ERROR);
		}
	}

}
