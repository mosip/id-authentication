package io.mosip.authentication.internal.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusRequestDto;

@Component
public class UpdateAuthtypeStatusValidator extends IdAuthValidator{

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateAuthtypeStatusRequestDto.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
	}

}
