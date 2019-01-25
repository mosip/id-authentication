package io.mosip.registration.test.service;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.PolicySyncRepository;
import io.mosip.registration.service.impl.PolicySyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
@PowerMockIgnore({ "javax.net.ssl.*", "javax.security.*" })
public class PolicySyncServiceTest {
	@Rule
	public MockitoRule MockitoRule = MockitoJUnit.rule();

	private ApplicationContext applicationContext = ApplicationContext.getInstance();

	@Mock
	private PolicySyncDAO policySyncDAO;

	@Mock
	private PolicySyncRepository policySyncRepository;

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
		ReflectionTestUtils.setField(policySyncServiceImpl, "url",
				"https://integ.mosip.io/keymanager/v1.0/publickey/{applicationId}");
		policySyncServiceImpl.fetchPolicy("centerId");

	}

	@Test
	public void netWorkAvailable() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		policySyncServiceImpl.fetchPolicy("centerId");

	}

	@Test
	public void testKeyStore() throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date date = dateFormat.parse("2020-12-29");
		Timestamp timestamp = new Timestamp(date.getTime());
		ReflectionTestUtils.setField(policySyncServiceImpl, "url",
				"https://integ.mosip.io/keymanager/v1.0/publickey/{applicationId}");
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(timestamp);
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		policySyncServiceImpl.fetchPolicy("centerId");

	}

	@Test
	public void testPublicKey() throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

		Date date = dateFormat.parse("2018-12-29");
		Timestamp timestamp = new Timestamp(date.getTime());
		ReflectionTestUtils.setField(policySyncServiceImpl, "url",
				"https://integ.mosip.io/keymanager/v1.0/publickey/{applicationId}");
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		KeyStore keyStore = new KeyStore();
		keyStore.setValidTillDtimes(timestamp);

		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);

		policySyncServiceImpl.fetchPolicy("centerId");

	}
  
}
