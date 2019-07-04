package io.mosip.kernel.licensekeygenerator.misp.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.MISPLicenseGenerator;
import io.mosip.kernel.licensekeygenerator.misp.util.MISPLicenseKeyGeneratorUtil;

/**
 * Implementation class for {@link MISPLicenseGenerator}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class MISPLicenseKeyGeneratorImpl implements MISPLicenseGenerator<String> {

	/**
	 * Autowired reference for {@link MISPLicenseKeyGeneratorUtil}.
	 */
	@Autowired
	MISPLicenseKeyGeneratorUtil mispLicenseGeneratorUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idgenerator.spi.MISPLicenseGenerator#generateLicense()
	 */
	@Override
	public String generateLicense() {
		return mispLicenseGeneratorUtil.generate();
	}
}
