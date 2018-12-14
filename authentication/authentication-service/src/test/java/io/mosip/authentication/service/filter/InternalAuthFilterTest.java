package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthFilterTest {

	@Autowired
	Environment env;

	InternalAuthFilter internalAuthFilter = new InternalAuthFilter();

	@Autowired
	ObjectMapper mapper;
	
	  @Before
	    public void before() {
		ReflectionTestUtils.setField(internalAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(internalAuthFilter, "env", env);
	    }

	@Test
	public void testValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method decodeMethod = InternalAuthFilter.class.getDeclaredMethod("decodedRequest",
				Map.class);
		decodeMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) decodeMethod.invoke(internalAuthFilter,
				createEncodedRequest());
		assertNotNull(decodeValue);

	}

	@Test
	public void testValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method encodeMethod = InternalAuthFilter.class.getDeclaredMethod("encodedResponse",
				Map.class);
		encodeMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) encodeMethod.invoke(internalAuthFilter,
				createResponse());
		assertNotNull(decodeValue);

	}

	@Test
	public void testTxnId() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method txvIdMethod = InternalAuthFilter.class.getDeclaredMethod("setTxnId",
				Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(internalAuthFilter, createEncodedRequest(),
				createResponse());
		assertNotNull(decodeValue);

	}

	public Map<String, Object> createEncodedRequest() throws IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setTxnID("121332");
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty("mosip.primary.lang-code"));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty("mosip.secondary.lang-code"));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);

		String authRequest =mapper.writeValueAsString(authRequestDTO);

		Map<String, Object> map =(Map<String, Object>) mapper.readValue(authRequest.getBytes(), Map.class);
		return map;
	}

	public Map<String, Object> createResponse() throws IOException{
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setTxnID("12345");
		AuthResponseInfo authResponseInfo = new AuthResponseInfo();
		authResponseDTO.setInfo(authResponseInfo);
		String authResponse =mapper.writeValueAsString(authResponseDTO);
		Map<String, Object> map =(Map<String, Object>) mapper.readValue(authResponse.getBytes(), Map.class);
		return map;

	}


}
