package io.mosip.authentication.internal.service.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;

@Component
public class AuthTxnValidator extends IdAuthValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AutnTxnDto.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (Objects.nonNull(target)) {
			AutnTxnDto autnTxnDto = (AutnTxnDto) target;
			validateIdvId(autnTxnDto.getIndividualId(), autnTxnDto.getIndividualIdType(), errors,
					IdAuthCommonConstants.IDV_ID); 
		}

	}

}
