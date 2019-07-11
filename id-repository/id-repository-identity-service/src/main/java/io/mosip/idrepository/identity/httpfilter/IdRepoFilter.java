package io.mosip.idrepository.identity.httpfilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.httpfilter.BaseIdRepoFilter;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdRepoFilter.
 *
 * @author Manoj SP
 */
@Component
public final class IdRepoFilter extends BaseIdRepoFilter {

	/** The Constant GET. */
	private static final String GET = "GET";

	/** The Constant ID_REPO_FILTER. */
	private static final String ID_REPO_FILTER = "IdRepoFilter";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";

	/** The Constant READ. */
	private static final String READ = "read";

	/** The Constant TYPE. */
	private static final String TYPE = "type";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoFilter.class);

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/* (non-Javadoc)
	 * @see io.mosip.idrepository.core.httpfilter.BaseIdRepoFilter#buildResponse(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected final String buildResponse(HttpServletRequest request) {
		if (request.getMethod().equals(GET) && (request.getParameterMap().size() > 1
				|| (request.getParameterMap().size() == 1 && !request.getParameterMap().containsKey(TYPE)))) {
			try {
				IdResponseDTO idResponse = new IdResponseDTO();
				idResponse.setId(id.get(READ));
				idResponse.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
				ServiceError errors = new ServiceError(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(),
						IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage());
				idResponse.setErrors(Collections.singletonList(errors));
				return mapper.writeValueAsString(idResponse);
			} catch (IOException e) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REPO_FILTER, "\n" + ExceptionUtils.getStackTrace(e));
				throw new IdRepoAppUncheckedException(IdRepoErrorConstants.UNKNOWN_ERROR);
			}
		} else {
			return null;
		}
	}

}
