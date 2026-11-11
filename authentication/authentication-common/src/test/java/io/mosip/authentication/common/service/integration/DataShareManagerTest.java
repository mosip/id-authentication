package io.mosip.authentication.common.service.integration;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.authentication.common.service.helper.RestHelper;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@Disabled
public class DataShareManagerTest {
	
	@Mock
	private RestHelper restHelper;
	
	@Mock
	private RestRequestFactory restRequestFactory;
	
	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private ObjectMapper mapper;
	
	@InjectMocks
	private DataShareManager dataShareManager;

	@BeforeEach
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(dataShareManager, "mapper", mapper);
		RestRequestDTO restReqDTO = new RestRequestDTO();
		when(restRequestFactory.buildRequest(RestServicesConstants.DATA_SHARE_GET, null, String.class)).thenReturn(restReqDTO);
	}

	@AfterEach
	public void tearDown() throws Exception {

	}

	private DataShareManager getTestSubject() {
		return dataShareManager;
	}

	@Test
	public void testDownloadObjectJsonStr() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{}";
		when(restHelper.requestSync(any(), any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectJsonStrWithErrorsEmpty() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{ \"errors\":[]}";
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectJsonStrWithErrorsNonEmpty() throws Exception {
		assertThrows(IdAuthUncheckedException.class, () -> {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{ \"errors\":[{\"errorCode\":\"code\",\"errorMessage\":\"message\"}]}";
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
			});
	}
	
	@Test
	public void testDownloadObjectObjectResponse() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<Map> clazz = Map.class;
		boolean decryptionRequred = false;
		Map result;

		// default test
		testSubject = getTestSubject();
		Map<String, String> response = Map.of("aaa", "bbb");
		String responseStr = mapper.writeValueAsString(response);
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(responseStr);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectNonjsonStr() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "abc";
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectNonjsonStrDecryptionRequired() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = true;
		String result;

		// default test
		testSubject = getTestSubject();
		String encryptedResp = "Encrypted_abc";
		String response = "{ \"data\": \"abc\"}";
		ReflectionTestUtils.setField(testSubject, "dataShareGetDecryptRefId", "ds_ref_id_sample");
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(response.getBytes());
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectObjectResponseDecryptionRequired() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<Map> clazz = Map.class;
		boolean decryptionRequred = true;
		Map result;

		// default test
		testSubject = getTestSubject();
		Map<String, String> response = Map.of("aaa", "bbb");
		String responseStr = mapper.writeValueAsString(response);
		String encryptedResp = "Encrypted_abc";
		ReflectionTestUtils.setField(testSubject, "dataShareGetDecryptRefId", "ds_ref_id_sample");
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(responseStr.getBytes());
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObjectObjectResponseDecryptionRequiredParseException() throws Exception {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<Map> clazz = Map.class;
		boolean decryptionRequred = true;
		Map result;

		// default test
		testSubject = getTestSubject();
		Map<String, String> response = Map.of("aaa", "bbb");
		String responseStr ="-/\":invalid json" +  mapper.writeValueAsString(response) + "-/\":invalid json";
		String encryptedResp = "Encrypted_abc";
		ReflectionTestUtils.setField(testSubject, "dataShareGetDecryptRefId", "ds_ref_id_sample");
		when(restHelper.requestSync(any(), Mockito.any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(responseStr.getBytes());
		testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
			});
	}

    @Test
    public void testDownloadObjectResponseNull() throws Exception {
		assertThrows(IdAuthUncheckedException.class, () -> {
        DataShareManager testSubject = getTestSubject();
        String dataShareUrl = "";
        Class<String> clazz = String.class;
        boolean decryptionRequired = false;

        // Mock restHelper to return null
        when(restHelper.requestSync(any(), any())).thenReturn(null);

        testSubject.downloadObject(dataShareUrl, clazz, decryptionRequired);
    		});
	}
}