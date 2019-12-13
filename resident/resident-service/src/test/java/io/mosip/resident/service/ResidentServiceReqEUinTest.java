package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.service.NotificationService;
import io.mosip.resident.util.UINCardDownloadService;

@RunWith(SpringRunner.class)
public class ResidentServiceReqEUinTest {
	@InjectMocks
	ResidentServiceImpl residentServiceImpl;

	@Mock
	private UINCardDownloadService uinCardDownloadService;

	@Mock
	private IdAuthService idAuthService;

	@Mock
	NotificationService notificationService;
	byte[] card=new byte[10];
	
	@Before
	public void setup() throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(true);
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(card);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(mock(NotificationResponseDTO.class));
	}
	
	@Test
	public void testReqEuin() throws  ResidentServiceCheckedException {
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.UIN);
		dto.setIndividualId("123456789");
		dto.setCardType("UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test
	public void testReqEuinwithVID() throws ResidentServiceCheckedException {
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinNull() throws ResidentServiceCheckedException, ApisResourceAccessException {
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		 residentServiceImpl.reqEuin(dto);
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinUINCardFetchFailed() throws ResidentServiceCheckedException, ApisResourceAccessException {
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(),Mockito.anyString(), Mockito.any())).thenThrow(new ApisResourceAccessException("Unable to fetch uin card"));
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinSendNotificationFailed() throws ResidentServiceCheckedException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenThrow(new ResidentServiceCheckedException());
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	
	@Test(expected=ResidentServiceException.class)
	public void testReqEuininotpvalidationfailed() throws ResidentServiceCheckedException, ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(false);
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		residentServiceImpl.reqEuin(dto);
	}
	
	@Test(expected=ResidentServiceException.class)
	public void testReqEuininotpvalidationException() throws OtpValidationFailedException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenThrow(new OtpValidationFailedException());
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType(IdType.VID);
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		residentServiceImpl.reqEuin(dto);
	}
	
}
