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

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.UINCardDownloadService;

@RunWith(SpringRunner.class)
public class ResidentServiceReqEUinTest {
	@InjectMocks
	ResidentServiceImpl residentServiceImpl;
	
	@Mock
	private VidValidator<String> vidValidator;

	@Mock
	private UinValidator<String> uinValidator;

	@Mock
	private RidValidator<String> ridValidator;

	@Mock
	private UINCardDownloadService uinCardDownloadService;

	@Mock
	private IdAuthService idAuthService;

	@Mock
	NotificationService notificationService;
	byte[] card=new byte[10];
	
	@Before
	public void setup() throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(true);
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(card);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(mock(NotificationResponseDTO.class));
	}
	
	@Test
	public void testReqEuin() throws OtpValidationFailedException {
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("UIN");
		dto.setIndividualId("123456789");
		dto.setCardType("UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test
	public void testReqEuinwithVID() throws OtpValidationFailedException {
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinNull() throws OtpValidationFailedException, ApisResourceAccessException {
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		 residentServiceImpl.reqEuin(dto);
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinUINCardFetchFailed() throws OtpValidationFailedException, ApisResourceAccessException {
		Mockito.when(uinCardDownloadService.getUINCard(Mockito.anyString(),Mockito.anyString(), Mockito.any())).thenThrow(new ApisResourceAccessException("Unable to fetch uin card"));
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuinSendNotificationFailed() throws OtpValidationFailedException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenThrow(new ResidentServiceCheckedException());
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		assertEquals(card, residentServiceImpl.reqEuin(dto));
	}
	
	@Test(expected=ResidentServiceException.class)
	public void testReqEuininvalidIdType() throws OtpValidationFailedException, ApisResourceAccessException, ResidentServiceCheckedException {
		
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VI");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		residentServiceImpl.reqEuin(dto);
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuininvalidId() throws OtpValidationFailedException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("",""));
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		residentServiceImpl.reqEuin(dto);
	}
	@Test(expected=ResidentServiceException.class)
	public void testReqEuininotpvalidationfailed() throws OtpValidationFailedException, ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(idAuthService.validateOtp(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(false);
		EuinRequestDTO dto=new EuinRequestDTO();
		dto.setOtp("1235");
		dto.setTransactionID("1234567890");
		dto.setIndividualIdType("VID");
		dto.setIndividualId("123456789");
		dto.setCardType("MASKED_UIN");
		residentServiceImpl.reqEuin(dto);
	}
	
}
