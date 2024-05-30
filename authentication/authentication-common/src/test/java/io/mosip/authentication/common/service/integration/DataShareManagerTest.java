package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.helper.RestHelper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
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

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(dataShareManager, "mapper", mapper);
		RestRequestDTO restReqDTO = new RestRequestDTO();
		when(restRequestFactory.buildRequest(RestServicesConstants.DATA_SHARE_GET, null, String.class)).thenReturn(restReqDTO);
	}

	@After
	public void tearDown() throws Exception {

	}

	private DataShareManager getTestSubject() {
		return dataShareManager;
	}

	@Test
	public void testDownloadObject_jsonStr() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{}";
		when(restHelper.requestSync(any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObject_jsonStrWithErrorsEmpty() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{ \"errors\":[]}";
		when(restHelper.requestSync(any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test(expected = IdAuthUncheckedException.class)
	public void testDownloadObject_jsonStrWithErrorsNonEmpty() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "{ \"errors\":[{\"errorCode\":\"code\",\"errorMessage\":\"message\"}]}";
		when(restHelper.requestSync(any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObject_ObjectResponse() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<Map> clazz = Map.class;
		boolean decryptionRequred = false;
		Map result;

		// default test
		testSubject = getTestSubject();
		Map<String, String> response = Map.of("aaa", "bbb");
		String responseStr = mapper.writeValueAsString(response);
		when(restHelper.requestSync(any())).thenReturn(responseStr);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObject_NonjsonStr() throws Exception {
		DataShareManager testSubject;
		String dataShareUrl = "";
		Class<String> clazz = String.class;
		boolean decryptionRequred = false;
		String result;

		// default test
		testSubject = getTestSubject();
		String response = "abc";
		when(restHelper.requestSync(any())).thenReturn(response);
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObject_NonjsonStr_decryptionRequired() throws Exception {
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
		when(restHelper.requestSync(any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(response.getBytes());
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test
	public void testDownloadObject_objectResponse_decryptionRequired() throws Exception {
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
		when(restHelper.requestSync(any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(responseStr.getBytes());
		result = testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
		assertEquals(response, result);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testDownloadObject_objectResponse_decryptionRequired_parseException() throws Exception {
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
		when(restHelper.requestSync(any())).thenReturn(encryptedResp);
		when(securityManager.decrypt(Mockito.anyString(), Mockito.anyString(), isNull(), isNull(), Mockito.anyBoolean())).thenReturn(responseStr.getBytes());
		testSubject.downloadObject(dataShareUrl, clazz, decryptionRequred);
	}
}