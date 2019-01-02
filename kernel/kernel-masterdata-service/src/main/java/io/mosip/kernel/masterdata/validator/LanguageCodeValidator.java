package io.mosip.kernel.masterdata.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;

public class LanguageCodeValidator implements ConstraintValidator<ValidLangCode, String> {

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(value) && value.trim().length() > 3) {
			return false;
		} else {
			try {
				String url = env.getProperty("global.config.uri");
				String json = restTemplate.getForObject(url, String.class);
				JSONObject result = new JSONObject(json);
				JSONArray arr = result.getJSONArray("languagesSupported");
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
