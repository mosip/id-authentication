package io.mosip.admin.configvalidator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.configvalidator.constant.ConfigValidatorErrorCode;
import io.mosip.admin.configvalidator.exception.ConfigValidationException;

@Service
public class ProcessFlowConfigValidator {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.admin.processflow.reg}")
	String regUrl;

	@Value("${mosip.admin.processflow.regclient}")
	String regProcessorUrl;

	@Value("${mosip.admin.processflow.prereg}")
	String preRegUrl;

	public boolean validateDocumentProcess() {
		String str = restTemplate.getForObject(regUrl, String.class);
		String str2 = restTemplate.getForObject(regProcessorUrl, String.class);
		String str3 = restTemplate.getForObject(preRegUrl, String.class);
		boolean result = false;
		Properties props = new Properties();
		try {
			props.load(new StringReader(str));

		} catch (IOException e) {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}
		Properties prop = new Properties();
		try {
			prop.load(new StringReader(str2));
		} catch (IOException e) {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}
		Properties props1 = new Properties();
		try {
			props1.load(new StringReader(str3));

		} catch (IOException e) {
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					ConfigValidatorErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}
		String flag = props.getProperty("mosip.registration.document_disable_flag");
		String flags = prop.getProperty("registration.processor.validateApplicantDocument");
		// String flags1 =
		// props1.getProperty("registration.processor.validateApplicantDocument");
		// preregistration.workflow.documentupload
		// if (("y".equals(flag) && "false".equals(flags) && "false".equals(flags1))
		// || ("n".equals(flag) && "true".equals(flags) && "true".equals(flags1))) {

		if (("y".equals(flag) && "false".equals(flags)) || ("n".equals(flag) && "true".equals(flags))) {
			result = true;
			System.out.println("result = " + result);
			return result;

		} else {
			// System.out.println(result);
			throw new ConfigValidationException(ConfigValidatorErrorCode.CONFIG_NOT_VALIDATED.errorCode(),
					ConfigValidatorErrorCode.CONFIG_NOT_VALIDATED.errorMessage());
		}
	}

}
