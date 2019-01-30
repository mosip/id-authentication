package io.mosip.kernel.lkeymanager.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.lkeymanager.constant.LicenseKeyManagerExceptionConstants;
import io.mosip.kernel.lkeymanager.errorresponse.InvalidArgumentsErrorResponse;
import io.mosip.kernel.lkeymanager.exception.LicenseKeyServiceException;

@Component
public class LicenseKeyManagerUtil {
	/**
	 * 
	 */
	@Value("${mosip.kernel.licensekey.length}")
	private String licenseKeyLength;

	/**
	 * @return
	 */
	public String generateLicense() {
		return RandomStringUtils.randomAlphanumeric(Integer.parseInt(licenseKeyLength));
	}

	/**
	 * @param parameters
	 * @return
	 */
	public void hasNullOrEmptyParameters(String... parameters) {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		for (String parameter : parameters) {
			if (parameter == null || parameter.trim().length() == 0) {
				validationErrorsList.add(
						new ServiceError(LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorCode(),
								LicenseKeyManagerExceptionConstants.ILLEGAL_INPUT_ARGUMENTS.getErrorMessage()));
			}
		}
		if (!validationErrorsList.isEmpty()) {
			throw new InvalidArgumentsErrorResponse(validationErrorsList);
		}

	}

	/**
	 * @param parameterList
	 * @param parameters
	 */
	public void hasNullOrEmptyParameters(List<String> parameterList, String... parameters) {
		Throwable e = null;
		for (String parameter : parameterList) {
			if (parameter == null || parameter.trim().length() == 0)
				throw new LicenseKeyServiceException("", "", e);
		}
		for (String parameter : parameters) {
			if (parameter == null || parameter.trim().length() == 0)
				throw new LicenseKeyServiceException("", "", e);
		}
	}
}
