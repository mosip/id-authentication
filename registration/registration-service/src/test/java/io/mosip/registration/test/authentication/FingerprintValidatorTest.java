package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.validator.FingerprintValidator;

public class FingerprintValidatorTest {

	@InjectMocks
	FingerprintValidator fingerprintValidator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	RegistrationUserDetailDAO registrationUserDetailDAO;
	
	@Mock
	FingerprintFacade fingerprintFacade;

	AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();

	@Before
	public void initialize() {
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setPassword("mosip");
		authenticationValidatorDTO.setOtp("12345");
		List<FingerprintDetailsDTO> fingerPrintDetails = new ArrayList<>();
		FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();

		byte[] bytes = new byte[100];
		Arrays.fill(bytes, (byte) 1);

		fingerprintDetailsDTO.setFingerPrint(bytes);
		fingerprintDetailsDTO.setFingerprintImageName("fingerprintImageName");
		fingerprintDetailsDTO.setFingerPrintISOImage("fingerPrintISOImage".getBytes());
		fingerprintDetailsDTO.setFingerType("fingerType");
		fingerprintDetailsDTO.setForceCaptured(false);
		fingerprintDetailsDTO.setNumRetry(2);
		fingerprintDetailsDTO.setQualityScore(90.1);
		fingerPrintDetails.add(fingerprintDetailsDTO);
		authenticationValidatorDTO.setFingerPrintDetails(fingerPrintDetails);
	}

	@Test
	public void validateSingleTest() {
		fingerprintValidator.setFingerPrintType("single");

		assertThat(fingerprintValidator.getFingerPrintType(), is("single")); 
		UserBiometric userBiometric = new UserBiometric();
		userBiometric.setQualityScore(91);
		userBiometric.setBioMinutia("bioMinutia");
		List<UserBiometric> userBiometrics = new ArrayList<>();
		userBiometrics.add(userBiometric);

		when(registrationUserDetailDAO.getUserSpecificFingerprintDetails("mosip")).thenReturn(userBiometrics);
		when(fingerprintFacade.validateFP(authenticationValidatorDTO.getFingerPrintDetails().get(0), userBiometrics)).thenReturn(true);

		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(true));
	}
	
	@Test
	public void validateMultipleTest() {
		fingerprintValidator.setFingerPrintType("multiple");
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(false));
	}
	
	@Test
	public void validateTest() {
		fingerprintValidator.setFingerPrintType("");
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(false));
	}
}
