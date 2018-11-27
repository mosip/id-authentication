package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
 * 
 */
public class BaseAuthRequestValidator implements Validator {

    /** The mosip logger. */
    private static Logger mosipLogger = IdaLogger.getLogger(IdAuthValidator.class);

    /** The Constant ID_AUTH_VALIDATOR. */
    private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";

    /** The Constant SESSION_ID. */
    private static final String SESSION_ID = "SESSION_ID";

    /** The Constant MISSING_INPUT_PARAMETER. */
    private static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

    /** The Constant VALIDATE. */
    private static final String VALIDATE = "VALIDATE";

    /** The Constant ID. */
    private static final String ID = "id";

    /** The Constant VER. */
    private static final String VER = "ver";

    /** The Constant verPattern. */
    private static final Pattern verPattern = Pattern.compile("^\\d+(\\.\\d{1,1})?$");

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
	return BaseAuthRequestDTO.class.isAssignableFrom(clazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.validation.Validator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object req, Errors errors) {
	BaseAuthRequestDTO baseAuthRequestDTO = (BaseAuthRequestDTO) req;

	if (baseAuthRequestDTO != null) {
	    validateId(baseAuthRequestDTO.getId(), errors);
	    validateVer(baseAuthRequestDTO.getVer(), errors);
	}
    }

    /**
     * Validate id.
     *
     * @param id
     *            the id
     * @param errors
     *            the errors
     */
    protected void validateId(String id, Errors errors) {
	if (Objects.isNull(id)) {
	    mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + " - id");
	    errors.rejectValue(ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    new Object[] { ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
	}
    }

    /**
     * Validate ver.
     *
     * @param ver
     *            the ver
     * @param errors
     *            the errors
     */
    protected void validateVer(String ver, Errors errors) {
	if (Objects.isNull(ver)) {
	    mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + VER);
	    errors.rejectValue(VER, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
		    new Object[] { VER }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
	} else if (!verPattern.matcher(ver).matches()) {
	    mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
		    "INVALID_INPUT_PARAMETER - ver - value -> " + ver);
	    errors.rejectValue(VER, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
		    new Object[] { VER }, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
	}
    }

}
