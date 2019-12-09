package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.service.bio.BioService;
import io.mosip.registration.validator.IrisValidatorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationContext.class})
public class IrisValidatorTest {

	@InjectMocks
	private IrisValidatorImpl irsiValidatorImpl;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private BioService bioService;
	
	@Mock
	private UserDetailDAO userDetailDAO;
	
	private AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();

	@Before
	public void initialize() {
		authenticationValidatorDTO.setUserId("mosip");
		
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();


		byte[] bytes = new byte[100];
		Arrays.fill(bytes, (byte) 1);

		irisDetailsDTO.setIris(bytes);
		irisDetailsDTO.setIrisType("leftIris");
		irisDetailsDTO.setForceCaptured(false);
		irisDetailsDTO.setQualityScore(90.1);
		irisDetailsDTOs.add(irisDetailsDTO);
		authenticationValidatorDTO.setIrisDetails(irisDetailsDTOs);;
	}
	
	@Test
	public void validateTest() {
		Map<String, Object> applicationMap = new HashMap<>();	
		PowerMockito.mockStatic(ApplicationContext.class);
		when(ApplicationContext.map()).thenReturn(applicationMap);
		ApplicationContext.map().put(RegistrationConstants.DEDUPLICATION_FINGERPRINT_ENABLE_FLAG, RegistrationConstants.DISABLE);
		assertThat(irsiValidatorImpl.validate(authenticationValidatorDTO), is(false));
	}
	
	@Test
	public void validateUserTest() {
		authenticationValidatorDTO.setUserId("");
		Map<String, Object> applicationMap = new HashMap<>();	
		PowerMockito.mockStatic(ApplicationContext.class);
		when(ApplicationContext.map()).thenReturn(applicationMap);
		ApplicationContext.map().put(RegistrationConstants.DEDUPLICATION_FINGERPRINT_ENABLE_FLAG, RegistrationConstants.DISABLE);
		irsiValidatorImpl.validate(authenticationValidatorDTO);
	}
	
	@Test
	public void validateAuthTest() {
		assertNull(irsiValidatorImpl.validate("mosip","123", true));
	}
}
