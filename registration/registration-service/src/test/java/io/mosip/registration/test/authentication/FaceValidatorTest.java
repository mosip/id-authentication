package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.device.face.FaceFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.validator.FaceValidatorImpl;

public class FaceValidatorTest {
	
	@InjectMocks
	private FaceValidatorImpl faceValidatorImpl;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private FaceFacade faceFacade;
	
	@Mock
	private RegistrationUserDetailDAO registrationUserDetailDAO;
	
	private AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();

	@Before
	public void initialize() {
		authenticationValidatorDTO.setUserId("mosip");
		
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();

		byte[] bytes = new byte[100];
		Arrays.fill(bytes, (byte) 1);

		faceDetailsDTO.setFace(bytes);
		authenticationValidatorDTO.setFaceDetail(faceDetailsDTO);
	}
	
	@Test
	public void validateTest() {
		assertThat(faceValidatorImpl.validate(authenticationValidatorDTO), is(false));
	}

}
