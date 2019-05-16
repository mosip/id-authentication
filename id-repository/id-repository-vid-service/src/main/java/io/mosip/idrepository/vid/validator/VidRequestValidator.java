package io.mosip.idrepository.vid.validator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.validator.BaseIdRepoValidator;
import io.mosip.idrepository.vid.dto.RequestDTO;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class will validate the Vid Request.
 * 
 * @author Manoj SP
 * @author Prem Kumar
 *
 */
@Component
public class VidRequestValidator extends BaseIdRepoValidator implements Validator {

	Logger mosipLogger = IdRepoLogger.getLogger(VidRequestValidator.class);

	private static final String VID_REQUEST_VALIDATOR = "VidRequestValidator";

	private static final String VID_TYPE = "vidType";

	private static final String CREATE = "create";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant STATUS_FIELD. */
	private static final String STATUS_FIELD = "vidStatus";

	private static final String UIN = "UIN";

	@Autowired
	private VidPolicyProvider policyProvider;

	/** The Vid Validator */
	@Autowired
	private VidValidator<String> vidValidator;

	@Autowired
	private UinValidator<String> uinValidator;

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
		validateReqTime(request.getRequesttime(), errors);
		validateVersion(request.getVersion(), errors);
		validateRequest(request.getRequest(), errors);

		if (!errors.hasErrors() && request.getId().equals(id.get(CREATE))) {
			validateVidType(request.getRequest().getVidType(), errors);
			validateUin(request.getRequest().getUin(), errors);
		}

		if (!errors.hasErrors() && request.getId().equals(id.get(UPDATE))) {
			validateStatus(request.getRequest().getVidStatus(), errors);
		}
	}

	private void validateVidType(String vidType, Errors errors) {
		if (Objects.isNull(vidType)) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_REQUEST_VALIDATOR, "validateVidType", "vidType is null");
			errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), VID_TYPE));
		} else if (!policyProvider.getAllVidTypes().contains(vidType)) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_REQUEST_VALIDATOR, "validateVidType",
					"vidType is invalid - " + vidType);
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), VID_TYPE));
		}
	}

	private void validateUin(String uin, Errors errors) {
		try {
			uinValidator.validateId(uin);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_REQUEST_VALIDATOR, "validateUin",
					"\n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), UIN));
		}
	}

	/**
	 * 
	 * @param request
	 * @param errors
	 */
	private void validateRequest(RequestDTO request, Errors errors) {
		if (Objects.isNull(request)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * This method will validate the Status of Vid.
	 * 
	 * @param vidStatus
	 * @param errors
	 */
	private void validateStatus(String vidStatus, Errors errors) {
		if (Objects.isNull(vidStatus)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), STATUS_FIELD));
		} else if (!allowedStatus.contains(vidStatus)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), STATUS_FIELD));
		}

	}

	/**
	 * This method will validate the Vid value.
	 * 
	 * @param vid
	 * @throws IdRepoAppException
	 */
	public void validateVid(String vid) {
		vidValidator.validateId(vid);
	}

}
