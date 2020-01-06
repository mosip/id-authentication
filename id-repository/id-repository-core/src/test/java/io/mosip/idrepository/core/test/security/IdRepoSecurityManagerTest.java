package io.mosip.idrepository.core.test.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoSecurityManagerTest {

	@Mock
	private RestRequestBuilder restBuilder;

	/** The rest helper. */
	@Mock
	private RestHelper restHelper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	@InjectMocks
	private IdRepoSecurityManager securityManager;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(securityManager, "env", env);
		ReflectionTestUtils.setField(securityManager, "mapper", mapper);
	}

	@Test
	public void testHash() {
		assertEquals("88D4266FD4E6338D13B845FCF289579D209C897823B9217DA3E161936F031589",
				securityManager.hash("abcd".getBytes()));
	}

	@Test
	public void testEncrypt()
			throws IdRepoAppException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		ResponseWrapper<ObjectNode> response = new ResponseWrapper<>();
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("data", "data");
		response.setResponse(responseNode);
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		when(restHelper.requestSync(Mockito.any()))
				.thenReturn(mapper.readValue(mapper.writeValueAsString(response), ObjectNode.class));
		assertEquals("data", new String(securityManager.encrypt("1".getBytes())));
	}

	@Test
	public void testDecrypt()
			throws IdRepoAppException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		ResponseWrapper<ObjectNode> response = new ResponseWrapper<>();
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("data", CryptoUtil.encodeBase64String("data".getBytes()));
		response.setResponse(responseNode);
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
				.thenReturn(new RestRequestDTO());
		when(restHelper.requestSync(Mockito.any()))
				.thenReturn(mapper.readValue(mapper.writeValueAsString(response), ObjectNode.class));
		assertEquals("data", new String(securityManager.decrypt("1".getBytes())));
	}

	@Test
	public void testEncryptError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		try {
			ResponseWrapper<ObjectNode> response = new ResponseWrapper<>();
			ObjectNode responseNode = mapper.createObjectNode();
			responseNode.put("data", "data");
			response.setResponse(responseNode);
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
					.thenReturn(new RestRequestDTO());
			when(restHelper.requestSync(Mockito.any()))
					.thenThrow(new RestServiceException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED));
			assertEquals("data", new String(securityManager.encrypt("1".getBytes())));
		} catch (IdRepoAppException e) {
			assertEquals(e.getErrorCode(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode());
			assertEquals(e.getErrorText(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage());
		}
	}

	@Test
	public void testDecryptError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		try {
			ResponseWrapper<ObjectNode> response = new ResponseWrapper<>();
			ObjectNode responseNode = mapper.createObjectNode();
			responseNode.put("data", "data");
			response.setResponse(responseNode);
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
					.thenReturn(new RestRequestDTO());
			when(restHelper.requestSync(Mockito.any()))
					.thenThrow(new RestServiceException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED));
			assertEquals("data", new String(securityManager.decrypt("1".getBytes())));
		} catch (IdRepoAppException e) {
			assertEquals(e.getErrorCode(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode());
			assertEquals(e.getErrorText(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage());
		}
	}

	@Test
	public void testDecryptNoResponseData()
			throws IdRepoAppException, JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		try {
			ResponseWrapper<ObjectNode> response = new ResponseWrapper<>();
			ObjectNode responseNode = mapper.createObjectNode();
			response.setResponse(responseNode);
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any(Class.class)))
					.thenReturn(new RestRequestDTO());
			when(restHelper.requestSync(Mockito.any()))
					.thenReturn(mapper.readValue(mapper.writeValueAsString(response), ObjectNode.class));
			assertEquals("data", new String(securityManager.decrypt("1".getBytes())));
		} catch (IdRepoAppException e) {
			assertEquals(e.getErrorCode(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorCode());
			assertEquals(e.getErrorText(), IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED.getErrorMessage());
		}
	}
}
