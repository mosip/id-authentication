package io.mosip.registration.test.notificationservice;

import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;

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
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.dto.NotificationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.impl.NotificationServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class NotificationServiceTest {

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@Mock
	private AuditFactoryImpl auditFactory;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private NotificationServiceImpl notificationServiceImpl;

	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void sendEmailTest()
			throws HttpClientErrorException, RegBaseCheckedException, ResourceAccessException, SocketTimeoutException {

		NotificationDTO emailDTO = new NotificationDTO();
		emailDTO.setStatus("Email Request submitted");

		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject())).thenReturn(emailDTO);
		ResponseDTO responseDTO = notificationServiceImpl.sendEmail("Hi", "qwerty@gmail.com", "regid");

		Assert.assertEquals("Success", responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void sendSMSTest()
			throws HttpClientErrorException, RegBaseCheckedException, ResourceAccessException, SocketTimeoutException {
		NotificationDTO smsdto = new NotificationDTO();
		smsdto.setMessage("Test");
		smsdto.setNumber("9994019598");
		smsdto.setStatus("success");

		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject())).thenReturn(smsdto);
		ResponseDTO responseDTO = notificationServiceImpl.sendSMS("Hi", "9999999999", "regid");

		Assert.assertEquals("Success", responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void sendSMSFailuretest() throws ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject()))
				.thenThrow(HttpClientErrorException.class);
		ResponseDTO responseDTO = notificationServiceImpl.sendSMS("Hi", null, "regid");
		Assert.assertEquals("Unable to send SMS Notification", responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void sendEmailFailuretest() throws ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(NotificationDTO.class)))
				.thenThrow(HttpClientErrorException.class);
		ResponseDTO responseDTO = notificationServiceImpl.sendEmail("Hi", null, "regid");
		Assert.assertEquals("Unable to send EMAIL Notification",
				responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}
}
