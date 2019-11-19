package io.mosip.authentication.internal.service.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;

/**
 * The Class AuthtypeStatusValidator.
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class AuthtypeStatusValidator extends IdAuthValidator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthtypeRequestDto.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		if (Objects.nonNull(target)) {
			AuthtypeRequestDto authtypeRequestDto = (AuthtypeRequestDto) target;
			validateIdvId(authtypeRequestDto.getIndividualId(), authtypeRequestDto.getIndividualIdType(), errors,
					IdAuthCommonConstants.IDV_ID);
		}
	}

}
