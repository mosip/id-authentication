package io.mosip.kernel.licensekeygenerator.misp.test;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.licensekeygenerator.misp.exception.LengthNotSameException;
import io.mosip.kernel.licensekeygenerator.misp.util.MISPLicenseKeyGeneratorUtil;

/**
 * This class has test methods to check for exceptions.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootTest(classes = MISPLicenseKeyGeneratorUtil.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = RandomStringUtils.class)
public class MISPLicenseGeneratorExceptionTest {
	/**
	 * The default length specified for license key.
	 */
	@Value("${mosip.kernel.idgenerator.misp.license-key-length}")
	private int licenseKeyLength;

	/**
	 * Autowired reference for {@link MISPLicenseKeyGeneratorUtil}.
	 */
	@Autowired
	MISPLicenseKeyGeneratorUtil licenseGeneratorUtil;

	/**
	 * Test Scenario : It should throw an exception when the license key generated
	 * has length different than the specified one.
	 */
	@Test(expected = LengthNotSameException.class)
	public void lengthNotSameExceptionTest() {
		String dummyValue = "";
		if (licenseKeyLength == 0) {
			dummyValue = "failTheTestCase";
		}
		mockStatic(RandomStringUtils.class);
		when(RandomStringUtils.randomAlphanumeric(licenseKeyLength)).thenReturn(dummyValue);
		licenseGeneratorUtil.generate();
	}
}
