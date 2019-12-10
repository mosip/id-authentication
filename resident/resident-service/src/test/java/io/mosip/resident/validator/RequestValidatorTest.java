package io.mosip.resident.validator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.resident.dto.AuthLockRequestDto;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.exception.InvalidInputException;

@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class RequestValidatorTest {

	@InjectMocks
	private RequestValidator requestValidator = new RequestValidator();

	@Before
	public void setup() {
		ReflectionTestUtils.setField(requestValidator, "authLockId", "mosip.resident.authlock");
		ReflectionTestUtils.setField(requestValidator, "authTypes", "bio-FIR,bio-IIR");
		ReflectionTestUtils.setField(requestValidator, "version", "v1");
	}

	@Test(expected = InvalidInputException.class)
	public void testValidId() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();
		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidVersion() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();
		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("mosip.resident.authlock");
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidRequest() throws Exception {

		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();

		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidTransactionId() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();

		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("mosip.resident.authlock");
		requestWrapper.setVersion("v1");
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidIndividualType() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();
		authLockRequestDto.setTransactionID("12345");
		authLockRequestDto.setIndividualIdType("RID");
		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("mosip.resident.authlock");
		requestWrapper.setVersion("v1");
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidOtp() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();
		authLockRequestDto.setTransactionID("12345");
		authLockRequestDto.setIndividualIdType("UIN");

		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("mosip.resident.authlock");
		requestWrapper.setVersion("v1");
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}

	@Test(expected = InvalidInputException.class)
	public void testValidAuthTypes() throws Exception {
		AuthLockRequestDto authLockRequestDto = new AuthLockRequestDto();
		authLockRequestDto.setTransactionID("12345");
		authLockRequestDto.setIndividualIdType("UIN");
		authLockRequestDto.setOtp("1232354");
		List<String> authTypes = new ArrayList<String>();
		authTypes.add("bio-FMR");
		authLockRequestDto.setAuthType(authTypes);
		RequestWrapper<AuthLockRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("mosip.resident.authlock");
		requestWrapper.setVersion("v1");
		requestWrapper.setRequest(authLockRequestDto);
		requestValidator.validateAuthLockRequest(requestWrapper);

	}
}
