package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;
import io.mosip.kernel.masterdata.utils.LanguageUtils;

public class LanguageCodeValidator implements ConstraintValidator<ValidLangCode, String> {

	@Autowired
	private LanguageUtils languageUtils;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(value) && value.trim().length() > 3) {
			return false;
		} else {
			return languageUtils.isValid(value);
		}
	}

}
