package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.ValidLangCodeErrorCode;
import io.mosip.kernel.masterdata.exception.RequestException;
import lombok.Data;

/**
 * To validate Language codes as per ISO:639-3 standard during creation and
 * updation of Masterdata
 * 
 * @author Neha
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
public class LanguageCodeValidator implements ConstraintValidator<ValidLangCode, String> {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.supported-languages}")
	private String supportedLanguages;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
	 * javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String langCode, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(langCode) || langCode.trim().length() > 3) {
			return false;
		} else {
			try {
				String[] langArray = supportedLanguages.split(",");
				for (String string : langArray) {
					if (langCode.equals(string)) {
						return true;
					}
				}
			} catch (RestClientException e) {
				throw new RequestException(ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorCode(),
						ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorMessage() + " " + e.getMessage());
			}
			return false;
		}
	}
}