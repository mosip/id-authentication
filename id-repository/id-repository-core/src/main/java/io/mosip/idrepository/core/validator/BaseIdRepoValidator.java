package io.mosip.idrepository.core.validator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@Component
public abstract class BaseIdRepoValidator {
	
	private static final String BASE_ID_REPO_VALIDATOR = "BaseIdRepoValidator";

	Logger mosipLogger = IdRepoLogger.getLogger(BaseIdRepoValidator.class);
	
	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requesttime";

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant ID. */
	protected static final String ID = "id";

	/** The Environment */
	@Autowired
	protected Environment env;

	/** The id. */
	@Resource
	protected Map<String, String> id;

	/**
	 * Validate req time.
	 *
	 * @param reqTime the timestamp
	 * @param errors  the errors
	 */
	public void validateReqTime(LocalDateTime reqTime, Errors errors) {
		if (Objects.isNull(reqTime)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateReqTime", "requesttime is null");
			errors.rejectValue(REQUEST_TIME, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQUEST_TIME));
		} else {
			if (DateUtils.after(reqTime, DateUtils.getUTCCurrentDateTime())) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateReqTime", "requesttime is InValid");
				errors.rejectValue(REQUEST_TIME, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), String
						.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST_TIME));
			}
		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver    the ver
	 * @param errors the errors
	 */
	public void validateVersion(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateVersion", "version is null");
			errors.rejectValue(VER, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), VER));
		} else if ((!Pattern.compile(env.getProperty(IdRepoConstants.VERSION_PATTERN.getValue())).matcher(ver)
				.matches())) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateVersion", "version is InValid");
			errors.rejectValue(VER, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), VER));
		}
	}

	/**
	 * This method will validate the id field in the request
	 * 
	 * @param id
	 * @param errors
	 * @param operation
	 */
	public void validateId(String id,String operation) throws IdRepoAppException {
		if (Objects.isNull(id)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateId", "id is null");
			throw new IdRepoAppException(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), ID));
		} else if (!this.id.get(operation).equals(id)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), BASE_ID_REPO_VALIDATOR, "validateId", "id is invalid");
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), ID));
		}
	}
}
