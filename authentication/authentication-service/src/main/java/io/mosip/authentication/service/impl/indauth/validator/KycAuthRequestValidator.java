package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.EkycAuthType;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class For KycAuthRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Prem Kumar
 * @author Dinesh Karuppiah.T
 * 
 * 
 */

@Component
public class KycAuthRequestValidator extends BaseAuthRequestValidator {
	
	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "secondaryLangCode";
	
	/** The Constant EKYC_ALLOWED_AUTH_TYPE. */
	private static final String EKYC_ALLOWED_AUTH_TYPE = "ekyc.auth.types.allowed";

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthRequestValidator.class);

	/** The Constant AuthRequest. */
	private static final String AUTH_REQUEST = "requestedAuth";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant eKycAuthType. */
	private static final String REQUESTEDAUTH = "requestedAuth";

	/** The environment. */
	@Autowired
	private Environment environment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) target;
		if (kycAuthRequestDTO != null) {
			BeanPropertyBindingResult authErrors = new BeanPropertyBindingResult(kycAuthRequestDTO,
					errors.getObjectName());
			authRequestValidator.validate(kycAuthRequestDTO, authErrors);
			errors.addAllErrors(authErrors);

			if (!errors.hasErrors()) {
				validateConsentReq(kycAuthRequestDTO, errors);
			}

			if (!errors.hasErrors()) {
				validateAuthType(errors, kycAuthRequestDTO);
				validateLangCode(kycAuthRequestDTO.getSecondaryLangCode(), errors, SECONDARY_LANG_CODE, SECONDARY_LANG_CODE);
			}

		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), AUTH_REQUEST));
		}

	}
	
	/**
	 * Validates the KycAuthrequest against the Authtype on the request.
	 *
	 * @param errors            the errors
	 * @param kycAuthRequestDTO the kyc auth request DTO
	 */
	private void validateAuthType(Errors errors, KycAuthRequestDTO kycAuthRequestDTO) {
		String values = environment.getProperty(EKYC_ALLOWED_AUTH_TYPE);
		List<String> allowedAuthTypesList = Arrays.stream(values.split(",")).collect(Collectors.toList());
		Map<Boolean, List<EkycAuthType>> authTypes = Stream.of(EkycAuthType.values()).collect(
				Collectors.partitioningBy(ekycAuthType -> allowedAuthTypesList.contains(ekycAuthType.getType())));
		List<EkycAuthType> allowedAuthTypes = authTypes.get(Boolean.TRUE);
		List<EkycAuthType> notAllowedAuthTypes = authTypes.get(Boolean.FALSE);

		boolean noNotAllowedAuthTypeEnabled = notAllowedAuthTypes.stream().noneMatch(
				ekycAuthType -> ekycAuthType.getAuthTypePredicate().test(kycAuthRequestDTO.getRequestedAuth()));
		boolean anyAllowedAuthTypeEnabled = allowedAuthTypes.stream().anyMatch(
				ekycAuthType -> ekycAuthType.getAuthTypePredicate().test(kycAuthRequestDTO.getRequestedAuth()));
		boolean isValidAuthtype = noNotAllowedAuthTypeEnabled && anyAllowedAuthTypeEnabled;
		if (!isValidAuthtype) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					INVALID_INPUT_PARAMETER + REQUESTEDAUTH);
			String notAllowedAuthTypesStr = notAllowedAuthTypes.stream().map(at -> at.getType()).collect(Collectors.joining(","));
			errors.rejectValue(REQUESTEDAUTH, IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(), String
					.format(IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage(), notAllowedAuthTypesStr));
		}

	}

}
