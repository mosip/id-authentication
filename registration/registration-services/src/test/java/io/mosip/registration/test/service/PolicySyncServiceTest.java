package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.sync.impl.PolicySyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class PolicySyncServiceTest {
	@Rule
	public MockitoRule MockitoRule = MockitoJUnit.rule();

	private ApplicationContext applicationContext = ApplicationContext.getInstance();

	@Mock
	private PolicySyncDAO policySyncDAO;
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@Mock
	private UserOnboardDAO userOnboardDAO;

	@InjectMocks
	private PolicySyncServiceImpl policySyncServiceImpl;

	@Before
	public void initialize() {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("mosip.registration.key_policy_sync_threshold_value", "1");
		applicationContext.setApplicationMap(temp);
	}

	@Test
	public void fetch() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		

		Map<String, Object> responseMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<>();
		valuesMap.put("publicKey",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCR2L_MwUv4ctfGulWf4ZoWkSyBHbfkVtE_xAmzzIDWHP1V5hGxg8jt8hLtYYFwBNj4l_PTZGkblcVg-IePHilmQiVDptTVVA2PGtwRdud7QL4xox8RXmIf-xa-JmP2E804iVM-Ki8aPf1yuxXNUwLxZsflFww73lc-SGVUHupD8Os0qNZbbJl0BYioNG4WmPMHy3WJ-7jGN0HEV-9E18yf_enR0YewUmUI6Rxxb606-w8iQyWfSJq6UOfFmH5WAn-oTOoTIwg_fBxXuG_FlDoNWs6N5JtI18BMsUQA_GQZJct6TyXcBNUrcBYhZERvPlRGqIOoTl-T2sPJ5ST9eswIDAQAB");
		valuesMap.put("issuedAt", "2020-04-09T05:51:17.334");
		valuesMap.put("expiryAt", "2020-04-09T05:51:17.334");
		responseMap.put(RegistrationConstants.RESPONSE, valuesMap);
		String machineId = "machineId";
		String centerId = "centerId";
		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(Mockito.anyString())).thenReturn(centerId);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(responseMap);

		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}

	@Test
	public void netWorkAvailable() throws RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}

	@Test
	public void testKeyStore()
			throws ParseException, RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date date = dateFormat.parse("2019-4-5");
		Timestamp timestamp = new Timestamp(date.getTime());

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(timestamp);
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);

		Map<String, Object> responseMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> valuesMap = new LinkedHashMap<>();
		valuesMap.put("publicKey",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCR2L_MwUv4ctfGulWf4ZoWkSyBHbfkVtE_xAmzzIDWHP1V5hGxg8jt8hLtYYFwBNj4l_PTZGkblcVg-IePHilmQiVDptTVVA2PGtwRdud7QL4xox8RXmIf-xa-JmP2E804iVM-Ki8aPf1yuxXNUwLxZsflFww73lc-SGVUHupD8Os0qNZbbJl0BYioNG4WmPMHy3WJ-7jGN0HEV-9E18yf_enR0YewUmUI6Rxxb606-w8iQyWfSJq6UOfFmH5WAn-oTOoTIwg_fBxXuG_FlDoNWs6N5JtI18BMsUQA_GQZJct6TyXcBNUrcBYhZERvPlRGqIOoTl-T2sPJ5ST9eswIDAQAB");
		valuesMap.put("issuedAt", "2020-04-09T05:51:17.334");
		valuesMap.put("expiryAt", "2020-04-09T05:51:17.334");
		responseMap.put(RegistrationConstants.RESPONSE, valuesMap);
		

		String machineId = "machineId";
		String centerId = "centerId";
		String refId = centerId + "_" + machineId;

		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(machineId)).thenReturn(centerId);

		Mockito.when(policySyncDAO.getPublicKey(refId)).thenReturn(keyStore);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(responseMap);

		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}
	
	@Test
	public void testKeyStoreError()
			throws ParseException, RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date date = dateFormat.parse("2019-4-5");
		Timestamp timestamp = new Timestamp(date.getTime());

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(timestamp);
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);

		Map<String, Object> responseMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> valuesMap = new ArrayList<>();
		LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
		errorMap.put("errorCode", "KER-KMS-005");
		errorMap.put("message", "Required String parameter 'timeStamp' is not present");
		valuesMap.add(errorMap);
		responseMap.put(RegistrationConstants.RESPONSE, null);
		responseMap.put(RegistrationConstants.ERRORS, valuesMap);
		

		String machineId = "machineId";
		String centerId = "centerId";
		String refId = centerId + "_" + machineId;

		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(machineId)).thenReturn(centerId);

		Mockito.when(policySyncDAO.getPublicKey(refId)).thenReturn(keyStore);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(responseMap);

		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void failureTestException() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException, ParseException {
		KeyStore keys = new KeyStore();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date date = dateFormat.parse("2019-4-5");
		Timestamp timestamp = new Timestamp(date.getTime());
		keys.setValidTillDtimes(timestamp);
		Map<String, Object> responseMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> valuesMap = new ArrayList<>();
		LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
		errorMap.put("errorCode", "KER-KMS-005");
		errorMap.put("message", "Required String parameter 'timeStamp' is not present");
		valuesMap.add(errorMap);
		responseMap.put(RegistrationConstants.RESPONSE, null);
		responseMap.put(RegistrationConstants.ERRORS, valuesMap);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keys);
		doNothing().when(policySyncDAO).updatePolicy(keys);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(SocketTimeoutException.class);
		policySyncServiceImpl.fetchPolicy();
	}

	@Test
	public void failureTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(KeyManagementException.class);

		//assertNotNull(policySyncServiceImpl.fetchPolicy());
	}

	@Test
	public void getPublicKeyfailureTest()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(HttpClientErrorException.class);

		//assertNotNull(policySyncServiceImpl.fetchPolicy());
	}

	@Test
	public void checkKeyValidationExpiryTest() throws RegBaseCheckedException {

		String machineId = "machineId";
		String centerId = "centerId";
		String refId = centerId + "_" + machineId;
		KeyStore keyStore = new KeyStore();

		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(machineId)).thenReturn(centerId);
		keyStore.setValidTillDtimes(Timestamp.valueOf(LocalDateTime.now()));
		Mockito.when(policySyncDAO.getPublicKey(refId)).thenReturn(keyStore);
		policySyncServiceImpl.checkKeyValidation();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void checkKeyValidationExpiryException() throws RegBaseCheckedException {

		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenThrow(RuntimeException.class);
		policySyncServiceImpl.checkKeyValidation();
	}

	@Test
	public void checkKeyValidationTest() throws RegBaseCheckedException {

		String machineId = "machineId";
		String centerId = "centerId";
		String refId = centerId + "_" + machineId;
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(Timestamp.valueOf(("2019-03-28 13:00:57.172")));
		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(machineId)).thenReturn(centerId);
		Mockito.when(policySyncDAO.getPublicKey(refId)).thenReturn(keyStore);
		policySyncServiceImpl.checkKeyValidation();
	}

	@Test
	public void checkKeyValidationTestFailure() {
		KeyStore keyStore = new KeyStore();
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		policySyncServiceImpl.checkKeyValidation();
	}

}
