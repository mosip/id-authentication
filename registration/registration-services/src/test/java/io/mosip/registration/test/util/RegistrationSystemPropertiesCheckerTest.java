package io.mosip.registration.test.util;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

public class RegistrationSystemPropertiesCheckerTest {

	@Mock
	private RegistrationSystemPropertiesChecker registrationSystemPropertiesChecker;

	private static final String PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

	@Ignore
	@Test
	public void macAddressValidationTest() {
		String macAddress = RegistrationSystemPropertiesChecker.getMachineId();
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(macAddress);
		assertTrue(matcher.matches());
	}
}
