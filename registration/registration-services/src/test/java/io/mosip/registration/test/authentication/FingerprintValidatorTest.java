package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

<<<<<<< HEAD
=======
import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.validator.FingerprintValidatorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
public class FingerprintValidatorTest {

	@InjectMocks
	FingerprintValidatorImpl fingerprintValidator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private UserDetailDAO userDetailDAO;
	
	@Mock
	private FingerprintFacade fingerprintFacade;

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
		UserBiometric userBiometric = new UserBiometric();
		userBiometric.setQualityScore(91);
		userBiometric.setBioMinutia("bioMinutia");
		List<UserBiometric> userBiometrics = new ArrayList<>();
		userBiometrics.add(userBiometric);

		when(userDetailDAO.getUserSpecificBioDetails("mosip","Fingerprint")).thenReturn(userBiometrics);
		when(fingerprintFacade.validateFP(authenticationValidatorDTO.getFingerPrintDetails().get(0), userBiometrics)).thenReturn(true);
		authenticationValidatorDTO.setAuthValidationType("single");
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(true));
	}
	
	@Test
	public void validateMultipleTest() {
		authenticationValidatorDTO.setAuthValidationType("multiple");
<<<<<<< HEAD
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(false));
=======
		FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
		fingerprintDetailsDTO.setFingerType("right index");
		UserBiometric userBiometric = new UserBiometric();
		userBiometric.setQualityScore(91);
		userBiometric.setBioMinutia("bioMinutia");
		List<UserBiometric> userBiometrics = new ArrayList<>();
		userBiometrics.add(userBiometric);
		
		PowerMockito.mockStatic(SessionContext.class);
		SessionContext.map().put(RegistrationConstants.DUPLICATE_FINGER, fingerprintDetailsDTO);	

		when(userDetailDAO.getUserSpecificBioDetail("mosip", "FIN", "fingerType")).thenReturn(userBiometric);
		when(bioService.validateFP(authenticationValidatorDTO.getFingerPrintDetails().get(0), userBiometrics))
				.thenReturn(true);
		authenticationValidatorDTO.setAuthValidationType("multiple");
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.convert(authenticationValidatorDTO.getFingerPrintDetails().get(0).getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();

		BIR bir = new BIR(new BIRBuilder().withBdb(minutiae.getBytes()));
		BIR biType[] = new BIR[userBiometrics.size()];
		BIR b = new BIR(new BIRBuilder().withBdb(minutiae.getBytes()));
		biType[0] = b;
		Score score[] = new Score[1];
		Score score2 = new Score();
		score2.setInternalScore(30);
		score[0] = score2;
		when(bioApiImpl.match(Mockito.any(), Mockito.any(), (KeyValuePair[]) Mockito.isNull())).thenReturn(score);
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(true));
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	}
	
	@Test
	public void validateTest() {
		authenticationValidatorDTO.setAuthValidationType("");
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(false));
	}
	
	@Test
	public void validateAuthTest() {
		assertNull(fingerprintValidator.validate("mosip","123"));
	}
}
