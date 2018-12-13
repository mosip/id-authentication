package io.mosip.authentication.service.validator;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * The Class IdAuthValidator.
 *
 * @author Manoj SP
 */
@Component
public abstract class IdAuthValidator implements Validator {

	private static final String IDV_ID_TYPE = "idvIdType";

	private static final String IDV_ID = "idvId";

	/** The Constant MISSING_INPUT_PARAMETER. */
	private static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";
	
	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";
	
	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";
	
	/** The Constant DATETIME_PATTERN. */
	protected static final String DATETIME_PATTERN = "datetime.pattern";
	
	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";
	
	/** The Constant TXN_ID. */
	private static final String TXN_ID = "txnID";
	
	/** The Constant MUA_CODE. */
	private static final String MUA_CODE = "muaCode";
	
	/** The Constant VER. */
	private static final String VER = "ver";
	
	/** The Constant ID. */
	private static final String ID = "id";
	
	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^\\d+(\\.\\d{1,1})?$");
	
	/** The Constant A_Z0_9_10. */
	private static final Pattern A_Z0_9_10 = Pattern.compile("^[A-Z0-9]{10}");

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthValidator.class);

	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;

	/** The vid validator. */
	@Autowired
	private VidValidatorImpl vidValidator;

	/**
	 * Validate id - check whether id is null or not.
	 *
	 * @param id            the id
	 * @param errors            the errors
	 */
	public void validateId(String id, Errors errors) {
		if (Objects.isNull(id)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + " - id");
			errors.rejectValue(ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] {ID},
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate individual's id - check whether id is null or not and if valid,
	 * validates idType and UIN/VID.
	 *
	 * @param id            the id
	 * @param idType the id type
	 * @param idFieldName the id field name
	 * @param idTypeFieldName the id type field name
	 * @param errors            the errors
	 */
	public void validateIdvId(String id, String idType, Errors errors) {
		if (Objects.isNull(id)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + IDV_ID);
			errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] {IDV_ID},
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validateIdtypeUinVid(id, idType, errors);
		}
	}

	/**
	 * Validate ver - check whether version is one digit with one fraction.
	 *
	 * @param ver            the ver
	 * @param errors            the errors
	 */
	public void validateVer(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + VER);
			errors.rejectValue(VER, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] {VER},
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!verPattern.matcher(ver).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - ver - value -> " + ver);
			errors.rejectValue(VER, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {VER},
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate mua code - check whether it is of length 10 and alphanumeric.
	 *
	 * @param muaCode the mua code
	 * @param errors  the errors
	 */
	protected void validateMuaCode(String muaCode, Errors errors) {
		if (Objects.isNull(muaCode)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + MUA_CODE);
			errors.rejectValue(MUA_CODE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MUA_CODE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!A_Z0_9_10.matcher(muaCode).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - muaCode - value -> " + muaCode);
			errors.rejectValue(MUA_CODE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MUA_CODE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate txn id - check whether it is of length 10 and alphanumeric.
	 *
	 * @param txnID            the txn ID
	 * @param errors            the errors
	 */
	protected void validateTxnId(String txnID, Errors errors) {
		if (Objects.isNull(txnID)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + TXN_ID);
			errors.rejectValue(TXN_ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] {TXN_ID},
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!A_Z0_9_10.matcher(txnID).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - txnID - value -> " + txnID);
			errors.rejectValue(TXN_ID, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {TXN_ID},
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime            the req time
	 * @param errors            the errors
	 */
	protected void validateReqTime(String reqTime, Errors errors) {
		if (Objects.isNull(reqTime)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate UIN, VID.
	 *
	 * @param id the id
	 * @param idType the id type
	 * @param idFieldName the id field name
	 * @param idTypeFieldName the id type field name
	 * @param errors            the errors
	 */
	private void validateIdtypeUinVid(String id, String idType, Errors errors) {
		if (Objects.isNull(idType)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + IDV_ID_TYPE);
			errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (idType.equals(IdType.UIN.getType())) {
			try {
				uinValidator.validateId(id);
			} catch (InvalidIDException e) {
				mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "InvalidIDException - " + e);
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				vidValidator.validateId(id);
			} catch (InvalidIDException e) {
				mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "InvalidIDException - " + e);
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
			}
		} else {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "INCORRECT_IDTYPE - " + idType);
			errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
