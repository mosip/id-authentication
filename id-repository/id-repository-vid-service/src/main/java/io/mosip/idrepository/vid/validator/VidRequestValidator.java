package io.mosip.idrepository.vid.validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.util.DateUtils;

/**
 * This class will validate the Vid Request.
 * 
 * @author Prem Kumar
 *
 */
@Component
@ConfigurationProperties("mosip.idrepo.vid")
public class VidRequestValidator implements Validator {

	/** The Constant ID. */
	private static final String ID = "id";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requestTime";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant STATUS_FIELD. */
	private static final String STATUS_FIELD = "vidStatus";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile(IdRepoConstants.VERSION_PATTERN.getValue());

	/** The Environment */
	@Autowired
	Environment env;

	/** The Vid Validator */
	@Autowired
	private VidValidator<String> vidValidator;

	/** The allowed types. */
	@Resource
	private List<String> allowedStatus;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(VidRequestDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		VidRequestDTO request = (VidRequestDTO) target;
		validateReqTime(request.getRequestTime(), errors);
		if (!errors.hasErrors()) {
			validateVersion(request.getVersion(), errors);
		}
		if (!errors.hasErrors() && Objects.nonNull(request.getId())) {
			if (request.getId().equals(id.get(UPDATE))) {
				validateStatus(request.getRequest().getVidStatus(), errors);
			} else {
				errors.rejectValue(ID, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), ID));
			}
		}

	}

	/**
	 * This method will validate the Status of Vid.
	 * 
	 * @param vidStatus
	 * @param errors
	 */
	private void validateStatus(String vidStatus, Errors errors) {
		if (Objects.nonNull(vidStatus) && !allowedStatus.contains(vidStatus)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), STATUS_FIELD));
		}

	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime the timestamp
	 * @param errors  the errors
	 */
	private void validateReqTime(LocalDateTime reqTime, Errors errors) {
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
	private void validateVersion(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			errors.rejectValue(VER, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), VER));
		} else if ((!verPattern.matcher(ver).matches())) {
			errors.rejectValue(VER, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), VER));
		}
	}

	/**
	 * This method will validate the Vid value.
	 * 
	 * @param vid
	 * @throws IdRepoAppException
	 */
	public void validateId(String vid) throws IdRepoAppException {
		vidValidator.validateId(vid);
	}
}
