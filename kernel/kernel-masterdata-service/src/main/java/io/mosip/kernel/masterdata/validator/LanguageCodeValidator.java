package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.masterdata.constant.ValidLangCodeErrorCode;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;
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

	/**
	 * Environment instance
	 */
	@Value("${mosip.kernel.syncdata-service-configs-url}")
	private String configsUrl;

	@Value("${mosip.kernel.supported-languages-key}")
	private String supportedLanguages;

	@Value("${mosip.kernel.global-config-name-key}")
	private String globalConfigName;

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
				String configString = restTemplate.getForObject(configsUrl, String.class);
				if (!EmptyCheckUtils.isNullEmpty(configString)) {
					JSONObject config = new JSONObject(configString);
					JSONObject globalConfig = config.getJSONObject(globalConfigName);
					if (!EmptyCheckUtils.isNullEmpty(globalConfig)) {
						String supportedLanguage = (String) globalConfig.get(supportedLanguages);
						String[] langArray = supportedLanguage.split(",");
						for (String string : langArray) {
							if (langCode.equals(string)) {
								return true;
							}
						}
					}
				}

			} catch (JSONException | RestClientException e) {
				throw new RequestException(ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorCode(),
						ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorMessage() + " " + e.getMessage());
			}
			return false;
		}
	}
}