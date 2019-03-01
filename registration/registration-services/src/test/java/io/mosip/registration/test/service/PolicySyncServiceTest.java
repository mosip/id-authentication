package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
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

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dto.PublicKeyResponse;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.impl.PolicySyncServiceImpl;
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
		temp.put("KEY_POLICY_SYNC_THRESHOLD_VALUE", "1");
		applicationContext.setApplicationMap(temp);
	}

	@Test
	public void fetch() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		PublicKeyResponse<String> publicKeyResponse = new PublicKeyResponse<>();
		publicKeyResponse.setAlias("ALIAS");
		publicKeyResponse.setExpiryAt(LocalDateTime.now());
		publicKeyResponse.setIssuedAt(LocalDateTime.now());
		publicKeyResponse.setPublicKey("MY_PUBLIC_KEY");
		String machineId = "machineId";
		String centerId = "centerId";
		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(userOnboardDAO.getCenterID(Mockito.anyString())).thenReturn(centerId);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean()))
				.thenReturn(publicKeyResponse);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(Timestamp.valueOf(publicKeyResponse.getExpiryAt()));
		keyStore.setValidFromDtimes(Timestamp.valueOf(publicKeyResponse.getIssuedAt()));
		keyStore.setPublicKey(publicKeyResponse.getPublicKey().getBytes());
		Mockito.doNothing().when(policySyncDAO).updatePolicy(keyStore);

		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}

	@Test
	public void netWorkAvailable() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}

	@Test
	public void testKeyStore() throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date date = dateFormat.parse("2020-12-29");
		Timestamp timestamp = new Timestamp(date.getTime());

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(timestamp);
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);

		assertNotNull(policySyncServiceImpl.fetchPolicy());

	}

	@Test
	public void failureTest() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean()))
				.thenThrow(KeyManagementException.class);

		assertNotNull(policySyncServiceImpl.fetchPolicy());
	}

	@Test
	public void getPublicKeyfailureTest()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean()))
				.thenThrow(HttpClientErrorException.class);

		assertNotNull(policySyncServiceImpl.fetchPolicy());
	}

}
