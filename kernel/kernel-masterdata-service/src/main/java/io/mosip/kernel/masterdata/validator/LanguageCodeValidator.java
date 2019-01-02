package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;

/**
 * To validate Language codes as per ISO:639-3 standard during creation and
 * updation of Masterdata
 * 
 * @author Neha
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

	/**
	 * 
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(value) && value.trim().length() > 3) {
			return false;
		} else {
			try {
				JSONObject configJson = new JSONObject(restTemplate.getForObject(globalconfigsUrl, String.class));
				JSONArray arr = configJson.getJSONArray("supportedLanguages");
				for (int i = 0; i < arr.length(); i++) {
					if (value.equals(arr.getString(i))) {
						return true;
					}
				}
			} catch (Exception e) {
				throw new MasterDataServiceException("KER-MSD-1001", "LanguageCodeValidator error");
			}
			return false;
		}
	}

}
