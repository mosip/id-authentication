package io.mosip.admin.configvalidator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.configvalidator.constant.ConfigValidatorErrorCode;
import io.mosip.admin.configvalidator.exception.ConfigValidationException;
import io.mosip.admin.configvalidator.exception.PropertyNotFoundException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class ProcessFlowConfigValidator {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.admin.processflow.reg}")
	String regUrl;

	@Value("${mosip.admin.processflow.regclient}")
	String regProcessorUrl;

	/**
	 * @return true if validation successfull
	 */
	public boolean validateDocumentProcess() {
		String regClientProp = null;
		String regProcProp = null;
		try {
			regClientProp = restTemplate.getForObject(regUrl, String.class);
		} catch (RestClientException e) {
			throw new PropertyNotFoundException(ConfigValidatorErrorCode.REG_CLIENT_PROPERTY_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.REG_CLIENT_PROPERTY_NOT_FOUND.errorMessage());
		}
		try {
			regProcProp = restTemplate.getForObject(regProcessorUrl, String.class);
		} catch (RestClientException e) {
			throw new PropertyNotFoundException(ConfigValidatorErrorCode.REG_PROC_PROPERTY_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.REG_PROC_PROPERTY_NOT_FOUND.errorMessage());
		}

		boolean result = false;
		Properties props = new Properties();
		try {
			props.load(new StringReader(regClientProp));

		} catch (IOException e) {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}
		Properties prop = new Properties();
		try {
			prop.load(new StringReader(regProcProp));
		} catch (IOException e) {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}

		String flag = props.getProperty("mosip.registration.document_disable_flag");
		String flags = prop.getProperty("registration.processor.validateApplicantDocument");

		if (("y".equals(flag) && "false".equals(flags)) || ("n".equals(flag) && "true".equals(flags))) {
			result = true;
			return result;

		} else {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_NOT_SUCCESSFULLY_VALIDATED.errorCode(),
					ConfigValidatorErrorCode.CONFIG_NOT_SUCCESSFULLY_VALIDATED.errorMessage());
		}
	}

}
