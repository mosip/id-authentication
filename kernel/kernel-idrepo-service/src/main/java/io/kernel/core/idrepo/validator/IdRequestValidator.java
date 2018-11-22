package io.kernel.core.idrepo.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

@Component
public class IdRequestValidator implements Validator {

    private static final String TIMESTAMP = "timestamp";

    private static final String REQUEST = "request";

    private static final String REGISTRATION_ID = "registrationId";

    private static final String STATUS_FIELD = "status";

    private static final String UIN = "uin";

    private static final String VER = "ver";

    private static final String ID_FIELD = "id";

    @Autowired
    private Environment env;

    @Resource
    private Map<String, String> id;

    @Resource
    private Map<String, String> status;

    @Autowired
    private UinValidatorImpl uinValidator;

    @Override
    public boolean supports(Class<?> clazz) {
	return clazz.isAssignableFrom(IdRequestDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
	IdRequestDTO request = (IdRequestDTO) target;

	validateReqTime(request.getTimestamp(), errors);

	if (!errors.hasErrors()) {
	    validateId(request.getId(), errors);
	    validateVer(request.getVer(), errors);
	    validateUin(request.getUin(), errors);
	    validateStatus(request.getStatus(), errors);
	    validateRegId(request.getRegistrationId(), errors);
	    validateRequest(request.getRequest(), errors);
	}
    }

    private void validateId(String id, Errors errors) {
	if (Objects.isNull(id)) {
	    errors.rejectValue(ID_FIELD, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), ID_FIELD));
	} else if (!this.id.containsValue(id)) {
	    errors.rejectValue(ID_FIELD, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), ID_FIELD));
	}
    }

    private void validateVer(String ver, Errors errors) {
	if (Objects.isNull(ver)) {
	    errors.rejectValue(VER, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), VER));
	} else if (!ver.equals(env.getProperty("mosip.idrepo.version"))) {
	    errors.rejectValue(VER, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), VER));
	}

    }

    private void validateUin(String uin, Errors errors) {
	if (Objects.isNull(uin)) {
	    errors.rejectValue(UIN, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), UIN));
	} else {
	    try {
		uinValidator.validateId(uin);
	    } catch (InvalidIDException e) {
		errors.rejectValue(UIN, IdRepoErrorConstants.INVALID_UIN.getErrorCode(),
			IdRepoErrorConstants.INVALID_UIN.getErrorMessage());
	    }
	}
    }

    private void validateStatus(String status, Errors errors) {
	if (Objects.isNull(status)) {
	    errors.rejectValue(STATUS_FIELD, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), STATUS_FIELD));
	} else if (!this.status.containsValue(status)) {
	    errors.rejectValue(STATUS_FIELD, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), STATUS_FIELD));
	}
    }

    private void validateRegId(String registrationId, Errors errors) {
	if (Objects.isNull(registrationId)) {
	    errors.rejectValue(REGISTRATION_ID, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REGISTRATION_ID));
	}
    }

    private void validateRequest(Object request, Errors errors) {
	if (Objects.isNull(request)) {
	    errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQUEST));
	}
    }

    private void validateReqTime(String timestamp, Errors errors) {
	if (Objects.isNull(timestamp)) {
	    errors.rejectValue(TIMESTAMP, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), TIMESTAMP));
	} else {
	    try {
		SimpleDateFormat timestampFormat = new SimpleDateFormat(env.getProperty("datetime.pattern"));
		timestampFormat.setLenient(false);
		timestampFormat.parse(timestamp);
	    } catch (ParseException e) {
		errors.rejectValue(TIMESTAMP, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
			String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TIMESTAMP));
	    }
	}
    }

}
