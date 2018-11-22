package io.mosip.authentication.service.filter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class KycFilterTest{

	@Autowired
	Environment env;

	KycAuthFilter kycAuthFilter = new KycAuthFilter();

	@Autowired
	ObjectMapper mapper;
	
	  @Before
	    public void before() {
		ReflectionTestUtils.setField(kycAuthFilter, "mapper", mapper);
		ReflectionTestUtils.setField(kycAuthFilter, "env", env);
	    }

	@Test
	public void testValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method decodeMethod = KycAuthFilter.class.getDeclaredMethod("decodedRequest",
				Map.class);
		decodeMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) decodeMethod.invoke(kycAuthFilter,
				createEncodedRequest());
		assertNotNull(decodeValue);

	}

	@Test
	public void testInValidDecodedRequest() throws IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method decodeMethod = KycAuthFilter.class.getDeclaredMethod("decodedRequest",
				Map.class);
		Map<String, Object> map = new HashMap<>();
		map.put("authRequest", createResponse().get("response"));
		decodeMethod.setAccessible(true);
		try {
			Map<String, Object> decodeValue = (Map<String, Object>) decodeMethod.invoke(kycAuthFilter,
					map );
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}

	@Test
	public void testValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encodedResponse",
				Map.class);
		encodeMethod.setAccessible(true);
		Map<String, Object> decodeValue = (Map<String, Object>) encodeMethod.invoke(kycAuthFilter,
				createResponse());
		assertNotNull(decodeValue);

	}

	@Test
	public void testInValidEncodedRequest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method encodeMethod = KycAuthFilter.class.getDeclaredMethod("encodedResponse",
				Map.class);
		encodeMethod.setAccessible(true);
		Map<String, Object> map = new HashMap<>();
		map.put("response", "sdfsdfjhds");
		try{
			Map<String, Object> decodeValue = (Map<String, Object>) encodeMethod.invoke(kycAuthFilter,
					map);
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().getClass().equals(IdAuthenticationAppException.class));
		}

	}

	@Test
	public void testTxnId() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException {
		Method txvIdMethod = KycAuthFilter.class.getDeclaredMethod("setTxnId",
				Map.class, Map.class);
		txvIdMethod.setAccessible(true);
		Map<String, Object> requestBody = createEncodedRequest();
		Map<String, Object> authRequest = (Map<String, Object>) decode((String) createEncodedRequest().get("authRequest"));
		requestBody.replace("authRequest", authRequest);
		Map<String, Object> decodeValue = (Map<String, Object>) txvIdMethod.invoke(kycAuthFilter,requestBody,
				createResponse());
		assertNotNull(decodeValue);

	}

	private Map<String, Object> decode(String stringToDecode) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper2 = new ObjectMapper();
		return mapper2.readValue(Base64.getDecoder().decode(stringToDecode),
				new TypeReference<Map<String, Object>>() {
				});
	}

	public Map<String, Object> createEncodedRequest() throws IOException {
		KycAuthRequestDTO k = new  KycAuthRequestDTO();
		k.setConsentReq(true);
		k.setEKycAuthType(null);
		k.setEPrintReq(true);
		k.setSecLangReq(true);
		k.setId(null);
		k.setVer(null);
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
		k.setAuthRequest(authRequestDTO);

		String kycReq =mapper.writeValueAsString(k);

		Map<String, Object> map =(Map<String, Object>) mapper.readValue(kycReq.getBytes(), Map.class);

		String authRequest =mapper.writeValueAsString(authRequestDTO);
		String request =mapper.writeValueAsString(reqDTO);
		Map<String, Object> authRequestMap = (Map<String, Object>) map.get("authRequest");
		authRequestMap.put("request", Base64.getEncoder().encodeToString(request.getBytes()));
		map.put("authRequest", Base64.getEncoder().encodeToString(mapper.writeValueAsBytes(authRequestMap)));
		return map;
	}

	public Map<String, Object> createResponse() throws IOException{
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setTxnID("12345");
		AuthResponseInfo authResponseInfo = new AuthResponseInfo();
		authResponseDTO.setInfo(authResponseInfo);
		KycResponseDTO kycResponseDTO = new KycResponseDTO();
		kycResponseDTO.setAuth(authResponseDTO);
		kycResponseDTO.setKyc(new KycInfo());
		String kycResponse =mapper.writeValueAsString(kycResponseDTO);
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		kycAuthResponseDTO.setTxnID("12345");
		String kycAuthResponse =mapper.writeValueAsString(kycAuthResponseDTO);
		Map<String, Object> map =(Map<String, Object>) mapper.readValue(kycAuthResponse.getBytes(), Map.class);
		return map;

	}
}
