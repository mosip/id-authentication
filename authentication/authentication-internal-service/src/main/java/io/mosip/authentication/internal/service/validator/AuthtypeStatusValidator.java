package io.mosip.authentication.internal.service.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */

@Component
public class AuthtypeStatusValidator extends IdAuthValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AuthtypeRequestDto.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (Objects.nonNull(target)) {
			AuthtypeRequestDto authtypeRequestDto = (AuthtypeRequestDto) target;
			validateIdvId(authtypeRequestDto.getIndividualId(), authtypeRequestDto.getIndividualIdType(), errors,
					IdAuthCommonConstants.IDV_ID);
		}
	}

}
