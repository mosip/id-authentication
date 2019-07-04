package io.mosip.kernel.licensekeygenerator.misp.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.licensekeygenerator.misp.constant.MISPLicenseKeyGeneratorConstant;
import io.mosip.kernel.licensekeygenerator.misp.exception.LengthNotSameException;

/**
 * Class that provides utility methods.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class MISPLicenseKeyGeneratorUtil {
	/**
	 * Specified length for the license key to be generated.
	 */
	@Value("${mosip.kernel.idgenerator.misp.license-key-length}")
	private int licenseKeyLength;

	/**
	 * Method to generate license key.
	 * 
	 * @return the generated license key.
	 */
	public String generate() {
		String generatedLicenseKey = RandomStringUtils.randomAlphanumeric(licenseKeyLength);
		if (generatedLicenseKey.length() != licenseKeyLength) {
			throw new LengthNotSameException(MISPLicenseKeyGeneratorConstant.LENGTH_NOT_SAME.getErrorCode(),
					MISPLicenseKeyGeneratorConstant.LENGTH_NOT_SAME.getErrorMessage());
		}
		return generatedLicenseKey;
	}
}
