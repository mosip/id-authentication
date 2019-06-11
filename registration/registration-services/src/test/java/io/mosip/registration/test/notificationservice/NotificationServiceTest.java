package io.mosip.registration.test.notificationservice;

import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.UserContext;
import io.mosip.registration.dto.NotificationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.template.impl.NotificationServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class })
public class NotificationServiceTest {

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@Mock
	private AuditManagerSerivceImpl auditFactory;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private NotificationServiceImpl notificationServiceImpl;

	@Before
	public void initialize() throws Exception {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		PowerMockito.when(SessionContext.userContext().getUserId()).thenReturn("mosip");
	}

	@Test
	public void sendEmailTest()
			throws HttpClientErrorException, RegBaseCheckedException, ResourceAccessException, SocketTimeoutException {

		//NotificationDTO emailDTO = new NotificationDTO();
		//emailDTO.setStatus("Email Request submitted");
		HashMap<String, Object> finalMap=new HashMap<>();
		HashMap<String, Object> email=new HashMap<>();
		email.put("status", "success");
		email.put("message", "Email Request submitted");
		finalMap.put("response", email);
		
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject(),Mockito.anyString())).thenReturn(finalMap);
		ResponseDTO responseDTO = notificationServiceImpl.sendEmail("Hi", "qwerty@gmail.com", "regid");

		Assert.assertEquals("success", responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void sendSMSTest()
			throws HttpClientErrorException, RegBaseCheckedException, ResourceAccessException, SocketTimeoutException {
		NotificationDTO smsdto = new NotificationDTO();
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("message", "Hi");
		requestMap.put("number", "99999999");
		smsdto.setRequest(requestMap);
		smsdto.setStatus("success");
		HashMap<String, Object> finalMap=new HashMap<>();
		HashMap<String, Object> email=new HashMap<>();
		email.put("status", "success");
		email.put("message", "Sms Request submitted");
		finalMap.put("response", email);

		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject(),Mockito.anyString())).thenReturn(finalMap);
		ResponseDTO responseDTO = notificationServiceImpl.sendSMS("Hi", "9999999999", "regid");

		Assert.assertEquals("success", responseDTO.getSuccessResponseDTO().getMessage());
	}

	@Test
	public void sendSMSFailuretest() throws ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject(),Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		ResponseDTO responseDTO = notificationServiceImpl.sendSMS("Hi", null, "regid");
		Assert.assertEquals("Unable to send SMS Notification", responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}

	@Test
	public void sendEmailFailuretest() throws ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(NotificationDTO.class),Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		ResponseDTO responseDTO = notificationServiceImpl.sendEmail("Hi", null, "regid");
		Assert.assertEquals("Unable to send EMAIL Notification",
				responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}
	
	@Test
	public void sendEmailFailuretest1() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException{
		Map<String, List<Map<String,String>>> emailDTO=new HashMap<>();
		List<Map<String,String>> list= new ArrayList();
		Map<String,String> map=new HashMap<>();
		map.put("errorCode", "Err_Code_KER200");
		map.put("message", "To must be valid. It can't be empty or null.");
		list.add(map);
		emailDTO.put("errors",list);
		
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.anyObject(),Mockito.anyString())).thenReturn(emailDTO);
		ResponseDTO responseDTO = notificationServiceImpl.sendEmail("Hi", "qwerty@gmail.com", "regid");

		Assert.assertEquals("To must be valid. It can't be empty or null.", responseDTO.getErrorResponseDTOs().get(0).getMessage());
	}
}
