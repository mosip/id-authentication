package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.masterdata.constant.ValidLangCodeErrorCode;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;

/**
 * To validate Language codes as per ISO:639-3 standard during creation and
 * updation of Masterdata
 * 
 * @author Neha
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public class LanguageCodeValidator implements ConstraintValidator<ValidLangCode, String> {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Environment instance
	 */
	@Value("${mosip.kernel.syncdata-service-globalconfigs-url}")
	private String globalconfigsUrl;

	@Value("${mosip.kernel.supported-languages}")
	private String supportedLanguages;

	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String langCode, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(langCode) || langCode.trim().length() > 3) {
			return false;
		} else {
			try {
				String jsonString = restTemplate.getForObject(globalconfigsUrl, String.class);
				if (!EmptyCheckUtils.isNullEmpty(jsonString)) {
					JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(supportedLanguages);
					for (int i = 0; i < jsonArray.length(); i++) {
						if (langCode.equals(jsonArray.getString(i))) {
							return true;
						}
					}
				}
			} catch (JSONException | RestClientException e) {
				throw new MasterDataServiceException(
						ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorCode(),
						ValidLangCodeErrorCode.LANG_CODE_VALIDATION_EXCEPTION.getErrorMessage() + " " + e.getMessage());
			}
			return false;
		}
	}
}
