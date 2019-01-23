package io.mosip.authentication.service.impl.spin.validator;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Prem Kumar
 *
 */
@Component
public class StaticPinRequestValidator extends IdAuthValidator {

	private static final String ID_AUTH_VALIDATOR2 = "IdAuthValidator";

	/** The Constant A_Z0_9_10. */
	private static final Pattern PATTERN = Pattern.compile("^[0-9]{6}");
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	private static final String SPIN_VALIDATE = "STATIC PIN_VALIDATOR";

	private static final int MISSING_INPUT_PARAMETER = 0;

	private static final String REQUEST = "request";
	private static final String PINVALUE = "pinValue";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthValidator.class);

	
	@Override
	public boolean supports(Class<?> clazz) {
		return StaticPinRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		if (Objects.nonNull(target)) {
			StaticPinRequestDTO staticPinRequestDTO= (StaticPinRequestDTO)target;
			validateId(staticPinRequestDTO.getId(), errors);
			validateReqTime(staticPinRequestDTO.getReqTime(), errors);
			validateUinVidValue(staticPinRequestDTO,errors);
			validatePinValue(staticPinRequestDTO.getRequest().getPinValue(),errors);
		}
	}

	private void validatePinValue(String pinValue, Errors errors) {
		if(Objects.isNull(pinValue) ||pinValue.isEmpty()) {
			
				mosipLogger.error(SESSION_ID, SPIN_VALIDATE, ID_AUTH_VALIDATOR2, MISSING_INPUT_PARAMETER + PINVALUE);
				errors.rejectValue(REQUEST,  IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { PINVALUE }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else if (!PATTERN.matcher(pinValue).matches()) {
				mosipLogger.error(SESSION_ID, SPIN_VALIDATE, ID_AUTH_VALIDATOR2,
						"INVALID_INPUT_PARAMETER - pinValue - value -> " + pinValue);
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { PINVALUE }, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	

	private void validateUinVidValue(StaticPinRequestDTO staticPinRequestDTO, Errors errors) {
		String uin = staticPinRequestDTO.getRequest().getIdentity().getUin();
		String vid = staticPinRequestDTO.getRequest().getIdentity().getVid();
		if(uin!=null) {
			validateIdvId(uin,IdType.UIN.getType(),errors);
		} else if(vid!=null){
			validateIdvId(vid,IdType.VID.getType(),errors);
			}
		}
	}


