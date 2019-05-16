package io.mosip.idrepository.core.validator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@Component
@ConfigurationProperties("mosip.idrepo.id")
public abstract class BaseIdRepoValidator {

	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requesttime";

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant ID. */
	private static final String ID = "id";

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
			errors.rejectValue(REQUEST_TIME, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), REQUEST_TIME));
		} else {
			if (DateUtils.after(reqTime, DateUtils.getUTCCurrentDateTime())) {
				errors.rejectValue(REQUEST_TIME, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(), String
						.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), REQUEST_TIME));
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
			errors.rejectValue(VER, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), VER));
		} else if ((!Pattern.compile(env.getProperty(IdRepoConstants.VERSION_PATTERN.getValue())).matcher(ver)
				.matches())) {
			errors.rejectValue(VER, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), VER));
		}
	}

	/**
	 * This method will validate the id field in the request
	 * 
	 * @param id
	 * @param errors
	 * @param operation
	 */
	public void validateId(String id, Errors errors, String operation) {
		if (Objects.isNull(id)) {
			errors.rejectValue(ID, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), ID));
		} else if (!this.id.get(operation).equals(id)) {
			errors.rejectValue(ID, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), ID));
		}
	}
}
