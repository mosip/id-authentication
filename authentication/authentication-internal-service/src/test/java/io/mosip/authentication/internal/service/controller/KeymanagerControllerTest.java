package io.mosip.authentication.internal.service.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDTO;
import io.mosip.authentication.common.service.integration.dto.TimestampRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.internal.service.manager.KeyServiceManager;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class KeymanagerControllerTest {
	
	@InjectMocks
	KeymanagerController keymanagerController;
	
	@Autowired
	private Environment env;
	
	@Mock
	KeyServiceManager keymanagerService;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(keymanagerController, "env", env);
		ReflectionTestUtils.setField(keymanagerController, "keymanagerService", keymanagerService);
	}
	
	@Test
	public void TestPublickKey() throws IdAuthenticationBusinessException {
		java.util.Optional<String> refId = java.util.Optional.of("refId");		
		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
	}
	
	@Test
	public void TestPublickKey_S001() throws IdAuthenticationBusinessException {
		java.util.Optional<String> refId = java.util.Optional.of("ida");		
		keymanagerController.getPublicKey("ida", "2001-05-01T10:00:00.000Z",refId);
	}
	
	@Test
	public void TestPublickKey_S002() throws IdAuthenticationBusinessException {
		java.util.Optional<String> refId = java.util.Optional.of("ida");		
		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
	}
	
	@Test
	public void TestPublickKey_S003() throws IdAuthenticationBusinessException {
		java.util.Optional<String> refId = java.util.Optional.of("refId");		
		keymanagerController.getPublicKey("appId", "2001-05-01T10:00:00.000Z",refId);
	}
	
	@Test
	public void TestEncrypt() throws IdAuthenticationBusinessException {
		CryptomanagerRequestDTO requestDto = new CryptomanagerRequestDTO();
		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
		request.setApplicationId("IDA");
		request.setData("Test");
		request.setReferenceId("REFID");
		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
		request.setTimeStamp(LocalDateTime.now());
		requestDto.setRequest(request);
		keymanagerController.encrypt(requestDto);
	}

	@Test
	public void TestDecrypt() throws IdAuthenticationBusinessException {
		CryptomanagerRequestDTO requestDto = new CryptomanagerRequestDTO();
		CryptomanagerRequestDto request = new CryptomanagerRequestDto();
		request.setAad("VGhpcyBpcyBzYW1wbGUgYWFk");
		request.setApplicationId("IDA");
		request.setData("Test");
		request.setReferenceId("REFID");
		request.setSalt("LA7YcvP9DdLIVI5CwFt1SQ");
		request.setTimeStamp(LocalDateTime.now());
		requestDto.setRequest(request);
		keymanagerController.decrypt(requestDto);
	}
	
	@Test
	public void Testverify() throws IdAuthenticationBusinessException {		
		keymanagerController.verify("wertyuiolkjbvfgvfghjoihgfdrthvcxdfg");
	}
	
	@Test
	public void TestValidate() throws IdAuthenticationBusinessException {
		TimestampRequestDTO request = new TimestampRequestDTO();
		String data="test";
		String signedData="rhjhgfghuhgvfgh";
		TimestampRequestDto dto= new TimestampRequestDto(signedData,data,LocalDateTime.now(ZoneId.of("UTC")));
		request.setRequest(dto);
		keymanagerController.validate(request);
	}
	
}
