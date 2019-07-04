package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;

/**
 * Test class for Tokenid Manager.
 * 
 * @author Prem kumar
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
public class TokenIdManagerTest {

	@InjectMocks
	private TokenIdManager tokenIdManager;

	/**
	 * The Rest Helper
	 */
	@Mock
	private RestHelperImpl restHelper;

	/**
	 * The Rest request factory
	 */
	@Mock
	private RestRequestFactory restFactory;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(tokenIdManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(tokenIdManager, "restFactory", restFactory);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
	}

	@Test
	public void testTokenIdGenerator_Valid() throws RestServiceException, JsonParseException, JsonMappingException,
			IOException, IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = new RestRequestDTO();
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TOKEN_ID_GENERATOR, null, Map.class))
				.thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getTokenId_Valid());
		String tokenId = tokenIdManager.generateTokenId("3568174910", "1873299273");
		String tokenexpected = "294283191679206709381119968230906377";
		assertEquals(tokenexpected, tokenId);

	}

	@Test
	public void testTokenIdGenerator_InValid() throws RestServiceException, JsonParseException, JsonMappingException,
			IOException, IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = new RestRequestDTO();
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TOKEN_ID_GENERATOR, null, Map.class))
				.thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getTokenId_Invalid());
		String tokenId = tokenIdManager.generateTokenId("3568174910", "1873299273");
		String tokenexpected = null;
		assertEquals(tokenexpected, tokenId);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testTokenIdGenerator_InValid_IDDataValidationException() throws RestServiceException,
			JsonParseException, JsonMappingException, IOException, IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = new RestRequestDTO();
		IDDataValidationException idDataValidationException = new IDDataValidationException();
		idDataValidationException.addInfo("12213", "idDataValidationException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TOKEN_ID_GENERATOR, null, Map.class))
				.thenThrow(idDataValidationException);
		Mockito.when(restHelper.requestSync(buildRequest)).thenReturn(getTokenId_Valid());
		tokenIdManager.generateTokenId("3568174910", "1873299273");

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testTokenIdGenerator_InValid_RestServiceException() throws RestServiceException, JsonParseException,
			JsonMappingException, IOException, IdAuthenticationBusinessException {
		RestRequestDTO buildRequest = new RestRequestDTO();
		RestServiceException restServiceException = new RestServiceException();
		restServiceException.addInfo("12213", "restServiceException");
		Mockito.when(restFactory.buildRequest(RestServicesConstants.TOKEN_ID_GENERATOR, null, Map.class))
				.thenReturn(buildRequest);
		Mockito.when(restHelper.requestSync(buildRequest)).thenThrow(restServiceException);
		tokenIdManager.generateTokenId("3568174910", "1873299273");
	}

	private Object getTokenId_Valid() throws JsonParseException, JsonMappingException, IOException {
		Object readValue = mapper.readValue("{\r\n" + "  \"id\": null,\r\n" + "  \"version\": null,\r\n"
				+ "  \"responsetime\": \"2019-04-09T10:14:35.126Z\",\r\n" + "  \"metadata\": null,\r\n"
				+ "  \"response\": {\r\n" + "    \"tokenID\": \"294283191679206709381119968230906377\"\r\n" + "  },\r\n"
				+ "  \"errors\": null\r\n" + "}", new TypeReference<Object>() {
				});
		return readValue;
	}

	// Response is changed to responses
	private Object getTokenId_Invalid() throws JsonParseException, JsonMappingException, IOException {
		Object readValue = mapper.readValue("{\r\n" + "  \"id\": null,\r\n" + "  \"version\": null,\r\n"
				+ "  \"responsetime\": \"2019-04-09T10:14:35.126Z\",\r\n" + "  \"metadata\": null,\r\n"
				+ "  \"responses\": {\r\n" + "    \"tokenID\": \"294283191679206709381119968230906377\"\r\n"
				+ "  },\r\n" + "  \"errors\": null\r\n" + "}", new TypeReference<Object>() {
				});
		return readValue;
	}

}
