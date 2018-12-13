package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dto.PolicyDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.repositories.PolicySyncRepository;
import io.mosip.registration.service.impl.PolicySyncServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistrationAppHealthCheckUtil.class})
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
		Map<String,Object> temp = new HashMap<String,Object>();
		temp.put("name", "1");
		applicationContext.setApplicationMap(temp);
	}
	
	@Test
	public void fetchPolicy() throws JsonParseException, JsonMappingException, IOException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);		
		KeyStore  keyStore = new KeyStore();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Date date = dateFormat.parse("2018-12-29");
			Timestamp timestamp = new Timestamp(date.getTime());
			keyStore.setValidTillDtimes(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);

		policySyncServiceImpl.fetchPolicy("centerId");

		

	}
	@Test
	public void fetch()
	{
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(null);
		policySyncServiceImpl.fetchPolicy("centerId");
		
	}
	@Test
	public void netWorkAvailable()
	{
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		policySyncServiceImpl.fetchPolicy("centerId");
		
		
	}

}
