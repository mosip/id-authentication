package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import io.mosip.registration.validator.AuthenticationService;
import io.mosip.registration.validator.AuthenticationValidatorImplementation;
import io.mosip.registration.validator.FingerprintValidator;

public class FingerPrintCaptureServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private AuthenticationService authenticationValidatorFactory;

	@Mock
	private FingerprintValidator fingerprintValidator;

	@InjectMocks
	private FingerPrintCaptureServiceImpl fingerPrintCaptureServiceImpl;

	@Test
	public void validateFingerprintTest() {
		List<FingerprintDetailsDTO> fingerprintDetailsDTOs = new ArrayList<>();
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("abcd");
		authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
		AuthenticationValidatorImplementation authenticationValidatorImplementation = fingerprintValidator;
		Mockito.when(authenticationValidatorFactory.getValidator("Fingerprint"))
				.thenReturn(authenticationValidatorImplementation);
		authenticationValidatorImplementation.setFingerPrintType("multiple");
		Mockito.when(authenticationValidatorImplementation.validate(Mockito.any(AuthenticationValidatorDTO.class)))
				.thenReturn(true);
		assertEquals(true, fingerPrintCaptureServiceImpl.validateFingerprint(fingerprintDetailsDTOs));
	}

}
