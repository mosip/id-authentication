package io.mosip.registration.test.packetStatusSync;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.RegPacketStatusServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(SpringRunner.class)
public class RegPacketStatusServiceTest {
	
	@Mock
	ServiceDelegateUtil serviceDelegateUtil;
	
	@Mock
	MosipLogger logger;
	
	@Mock
	RegPacketStatusDAO packetStatusDao;
	
	@InjectMocks
	RegPacketStatusServiceImpl packetStatusService;

	@Test
	public void packetSyncStatusSuccessTest() throws HttpClientErrorException, RegBaseCheckedException {
		ReflectionTestUtils.setField(packetStatusService, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		LinkedHashMap<String, String> registration = new LinkedHashMap<>();
		registration.put("registrationId", "12345");
		registration.put("statusCode","DECRYPTED");
		registrations.add(registration);
		when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(registrations);
		Assert.assertNotNull(packetStatusService.packetSyncStatus().getSuccessResponseDTO());
	}
	
	@Test
	public void packetSyncStatusFailureTest() throws HttpClientErrorException, RegBaseCheckedException {
		ReflectionTestUtils.setField(packetStatusService, "LOGGER", logger);
		doNothing().when(logger).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
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
	
}
