package io.mosip.kernel.masterdata.validator;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TitleDto;

@Component
public class LanguageValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		if (RequestDto.class.isAssignableFrom(clazz)) {
			try {

				System.out.println("#######################################");
				System.out.println(clazz.getTypeParameters()[0]);
				System.out.println("#######################################");
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {

	}

}
