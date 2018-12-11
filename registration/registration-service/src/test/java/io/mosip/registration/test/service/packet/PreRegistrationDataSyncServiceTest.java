package io.mosip.registration.test.service.packet;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dto.PreRegistrationResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.service.sync.impl.PreRegistrationDataSyncServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class PreRegistrationDataSyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private PreRegistrationDataSyncServiceImpl preRegistrationDataSyncServiceImpl;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private SyncManager syncManager;

	@Mock
	private PreRegistrationDataSyncDAO preRegistrationDAO;

	@Mock
	private PreRegistrationResponseDTO preRegistrationResponseDTO;

	@Mock
	SyncTransaction syncTransaction;
	
	@Mock
	PreRegistrationList preRegistrationList;

	@Test
	@Ignore
	public void getPreRegistrationsTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		ArrayList<String> ids = new ArrayList<String>();
		ids.add("123423");

		HashMap<String, Object> map = new HashMap<>();
		map.put("preRegistrationIds", ids);

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		list.add(map);

		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any()))
				.thenReturn(preRegistrationResponseDTO);
		Mockito.when(preRegistrationResponseDTO.getResponse()).thenReturn(list);
		byte[] packet = new byte[100];

		Mockito.when(preRegistrationDAO.getPreRegistration(Mockito.anyString())).thenReturn(null);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any())).thenReturn(packet);
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		Mockito.when(preRegistrationDAO.savePreRegistration(preRegistrationList)).thenReturn(preRegistrationList);
	

		preRegistrationDataSyncServiceImpl.getPreRegistrationIds(null);

		//Test-2
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any()))
				.thenThrow(HttpClientErrorException.class);
		preRegistrationDataSyncServiceImpl.getPreRegistrationIds(null);
		
		
		

	}

	@Test
	public void getPreRegistrationTest()
			throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any())).thenThrow(HttpClientErrorException.class);
		Mockito.when(syncManager.createSyncTransaction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(syncTransaction);
		
			
		preRegistrationDataSyncServiceImpl.getPreRegistration(null);

	}

}
