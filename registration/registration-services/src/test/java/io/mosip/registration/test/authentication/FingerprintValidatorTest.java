package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.service.bio.BioService;
import io.mosip.registration.validator.FingerprintValidatorImpl;

public class FingerprintValidatorTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	FingerprintValidatorImpl fingerprintValidator;

	@Mock
	private UserDetailDAO userDetailDAO;
	
	@Mock
	private BioService bioService;
	
	@Mock
	private BioApiImpl bioApiImpl;

	AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
	private ApplicationContext applicationContext = ApplicationContext.getInstance();

	@Before
	public void initialize() {
		
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("mosip.registration.finger_print_score", "1");
		applicationContext.setApplicationMap(temp);
		
		
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

	//@Test
	public void validateSingleTest() {
		FingerprintDetailsDTO fingerprintDetailsDTO=new FingerprintDetailsDTO();
		fingerprintDetailsDTO.setFingerType("right index");
		UserBiometric userBiometric = new UserBiometric();
		userBiometric.setQualityScore(91);
		userBiometric.setBioMinutia("bioMinutia");
		List<UserBiometric> userBiometrics = new ArrayList<>();
		userBiometrics.add(userBiometric);

		when(userDetailDAO.getUserSpecificBioDetail("mosip","FIN","fingerType")).thenReturn(userBiometric);
		when(bioService.validateFP(authenticationValidatorDTO.getFingerPrintDetails().get(0), userBiometrics)).thenReturn(true);
		authenticationValidatorDTO.setAuthValidationType("single");
		BIRType birType=new BIRType();
		birType.setBDB(authenticationValidatorDTO.getFingerPrintDetails().get(0).getFingerPrintISOImage());
		BIRType biType[]=new BIRType[userBiometrics.size()];
		BIRType b=new BIRType();
		b.setBDB(userBiometrics.get(0).getBioMinutia().getBytes());
		biType[0]=b;
		Score score[]=new Score[1];
		Score score2=new Score();
		score2.setInternalScore(30);
		score[0]=score2;
		when(bioApiImpl.match(birType, biType, null)).thenReturn(score);
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(true));
	}
	
	//@Test
	public void validateMultipleTest() {
		authenticationValidatorDTO.setAuthValidationType("multiple");
		FingerprintDetailsDTO fingerprintDetailsDTO=new FingerprintDetailsDTO();
		fingerprintDetailsDTO.setFingerType("right index");
		UserBiometric userBiometric = new UserBiometric();
		userBiometric.setQualityScore(91);
		userBiometric.setBioMinutia("bioMinutia");
		List<UserBiometric> userBiometrics = new ArrayList<>();
		userBiometrics.add(userBiometric);

		when(userDetailDAO.getUserSpecificBioDetail("mosip","FIN","fingerType")).thenReturn(userBiometric);
		when(bioService.validateFP(authenticationValidatorDTO.getFingerPrintDetails().get(0), userBiometrics)).thenReturn(true);
		authenticationValidatorDTO.setAuthValidationType("single");
		BIRType birType=new BIRType();
		birType.setBDB(authenticationValidatorDTO.getFingerPrintDetails().get(0).getFingerPrintISOImage());
		BIRType biType[]=new BIRType[userBiometrics.size()];
		BIRType b=new BIRType();
		b.setBDB(userBiometrics.get(0).getBioMinutia().getBytes());
		biType[0]=b;
		Score score[]=new Score[1];
		Score score2=new Score();
		score2.setInternalScore(30);
		score[0]=score2;
		when(bioApiImpl.match(birType, biType, null)).thenReturn(score);
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(true));
	}
	
	@Test
	public void validateTest() {
		authenticationValidatorDTO.setAuthValidationType("");
		assertThat(fingerprintValidator.validate(authenticationValidatorDTO), is(false));
	}
}
