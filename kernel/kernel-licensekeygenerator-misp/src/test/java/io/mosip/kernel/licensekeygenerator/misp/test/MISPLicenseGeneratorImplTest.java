package io.mosip.kernel.licensekeygenerator.misp.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.idgenerator.spi.MISPLicenseGenerator;
import io.mosip.kernel.licensekeygenerator.misp.util.MISPLicenseKeyGeneratorUtil;

/**
 * This class has test methods to check for implementation methods.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MISPLicenseGeneratorImplTest {

	/**
	 * The default length specified for license key.
	 */
	@Value("${mosip.kernel.idgenerator.misp.license-key-length}")
	private int licenseKeyLength;

	/**
	 * Autowired reference for {@link MISPLicenseGenerator}.
	 */
	@Autowired
	MISPLicenseGenerator<String> licenseGenerator;

	/**
	 * Autowired reference for {@link MISPLicenseKeyGeneratorUtil}.
	 */
	@Autowired
	MISPLicenseKeyGeneratorUtil licenseGeneratorUtil;

	/**
	 * Test Scenario : This tests the basic functionality that the method is
	 * generating a license key.
	 */
	@Test
	public void generateMISPLicenseKey() {
		assertThat(licenseGenerator.generateLicense(), isA(String.class));
	}

	/**
	 * Test Scenario : This tests that the license key generated is of the specified
	 * length in configuration.
	 */
	@Test
	public void testUtilMethod() {
		assertThat(licenseGeneratorUtil.generate().length(), is(licenseKeyLength));
	}
}
