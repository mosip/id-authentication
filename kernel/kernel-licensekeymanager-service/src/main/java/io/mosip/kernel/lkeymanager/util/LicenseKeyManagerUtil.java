package io.mosip.kernel.lkeymanager.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
}
