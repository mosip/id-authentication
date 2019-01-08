package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.validator.IrisValidatorImpl;

public class IrisValidatorTest {

	@InjectMocks
	private IrisValidatorImpl irsiValidatorImpl;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private IrisFacade irisFacade;
	
	@Mock
	private RegistrationUserDetailDAO registrationUserDetailDAO;
	
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
		assertThat(irsiValidatorImpl.validate(authenticationValidatorDTO), is(false));
	}
}
