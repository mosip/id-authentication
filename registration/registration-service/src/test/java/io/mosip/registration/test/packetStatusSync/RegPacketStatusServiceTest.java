package io.mosip.registration.test.packetStatusSync;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.impl.RegPacketStatusServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class RegPacketStatusServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	ServiceDelegateUtil serviceDelegateUtil;	
	@Mock
	RegPacketStatusDAO packetStatusDao;
	@InjectMocks
	RegPacketStatusServiceImpl packetStatusService;

	@Test
	public void packetSyncStatusSuccessTest() throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		LinkedHashMap<String, String> registration = new LinkedHashMap<>();
		registration.put("registrationId", "12345");
		registration.put("statusCode","DECRYPTED");
		registrations.add(registration);
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(registrations);
		Assert.assertNotNull(packetStatusService.packetSyncStatus().getSuccessResponseDTO());
	}
	
	@Test
	public void packetSyncStatusFailureTest() throws HttpClientErrorException, RegBaseCheckedException, SocketTimeoutException {
		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(registrations);
		Assert.assertNotNull(packetStatusService.packetSyncStatus().getErrorResponseDTOs());
	}
	
	/*@Test
	public void getErrorResponseTest() {
		ResponseDTO response = new ResponseDTO();
		String message = "Packet Status Not Available";
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<ErrorResponseDTO>();
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(RegConstants.ALERT_ERROR);
		errorResponse.setMessage(message);
		Map<String, Object> otherAttributes = new HashMap<String, Object>();
		otherAttributes.put("registration", null);
		errorResponses.add(errorResponse);
		response.setErrorResponseDTOs(errorResponses);
		assertThat(packetStatusService.getErrorResponse(response, message), is(response));
	}*/
	
	/*@Test
	public void getPacketIdsByStatusPostSyncedTest() {
		List<Registration> registrations = new ArrayList<>();
		Registration registration = new Registration();
		registration.setId("12345");
		registration.setClientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
		registrations.add(registration);
		when(registrationRepository
				.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode()))
						.thenReturn(registrations);
		List<String> regIds = new ArrayList<>();
		regIds.add("12345");
		assertThat(packetStatusDao.getPacketIdsByStatusUploaded(), is(regIds));
	}

	@Test
	public void updatePacketIdsByServerStatusTest() {
		Registration registration = new Registration();
		registration.setId("12345");
		registration.setAckFilename("12345_Ack.png");
		List<RegistrationTransaction> transactionList = new ArrayList<>();
		RegistrationTransaction regTxn = new RegistrationTransaction();
		regTxn.setRegId(registration.getId());
		registration.setRegistrationTransaction(transactionList);
		when(registrationRepository.findById(Registration.class, "12345")).thenReturn(registration);
		List<RegPacketStatusDTO> packetStatus = new ArrayList<>();
		RegPacketStatusDTO packetStatusDTO = new RegPacketStatusDTO("12345", "PROCESSED");
		packetStatus.add(packetStatusDTO);
		packetStatusDao.updatePacketIdsByServerStatus(packetStatus);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void updatePacketIdsByServerStatusTest1() {
		Registration registration = new Registration();
		registration.setId("78965");
		when(registrationRepository.findById(Registration.class, "78965")).thenReturn(registration);
		List<RegPacketStatusDTO> packetStatus = new ArrayList<>();
		RegPacketStatusDTO packetStatusDTO = new RegPacketStatusDTO("78965", "RESEND");
		packetStatus.add(packetStatusDTO);

		packetStatusDao.updatePacketIdsByServerStatus(packetStatus);
	}*/
	
}
