/**
 * 
 */
package io.mosip.resident.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.UINCardDownloadService;

/**
 * @author M1022006
 *
 */
@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class ResidentServiceRequestAuthLockTest {

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

	@InjectMocks
	private ResidentService residentService = new ResidentServiceImpl();

	NotificationResponseDTO notificationResponseDTO;

	@Before
	public void setup() {

		notificationResponseDTO = new NotificationResponseDTO();
		notificationResponseDTO.setStatus("Notification success");

	}

	@Test
	public void testReqAauthLockSuccess()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(true);
		ResponseDTO response = new ResponseDTO();
		response.setMessage("Notification success");
		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");

		Mockito.when(idAuthService.authTypeStatusUpdate(authLockRequestDto.getIndividualId(),
				authLockRequestDto.getIndividualIdType().name(), authLockRequestDto.getAuthType(), AuthTypeStatus.LOCK))
				.thenReturn(true);
		Mockito.when(notificationService.sendNotification(Mockito.any())).thenReturn(notificationResponseDTO);
		ResponseDTO authLockResponse = residentService.reqAauthTypeStatusUpdate(authLockRequestDto,
				AuthTypeStatus.LOCK);
		assertEquals(authLockResponse.getMessage(), authLockResponse.getMessage());

	}

	@Test(expected = ResidentServiceException.class)
	public void testReqAauthLockOTPFailed()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(false);
		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");
		residentService.reqAauthTypeStatusUpdate(authLockRequestDto, AuthTypeStatus.LOCK);

	}

	@Test(expected = ResidentServiceException.class)
	public void testReqAauthLockFailed()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(true);

		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");
		Mockito.when(idAuthService.authTypeStatusUpdate(authLockRequestDto.getIndividualId(),
				authLockRequestDto.getIndividualIdType().name(), authLockRequestDto.getAuthType(), AuthTypeStatus.LOCK))
				.thenReturn(false);
		residentService.reqAauthTypeStatusUpdate(authLockRequestDto, AuthTypeStatus.LOCK);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void testReqAauthLockNotificationFailed()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(true);

		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");
		Mockito.when(idAuthService.authTypeStatusUpdate(authLockRequestDto.getIndividualId(),
				authLockRequestDto.getIndividualIdType().name(), authLockRequestDto.getAuthType(), AuthTypeStatus.LOCK))
				.thenReturn(true);
		Mockito.when(notificationService.sendNotification(Mockito.any()))
				.thenThrow(new ResidentServiceCheckedException());
		residentService.reqAauthTypeStatusUpdate(authLockRequestDto, AuthTypeStatus.LOCK);

	}

	@Test(expected = ResidentServiceException.class)
	public void testReqAauthLockException()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(true);

		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");
		Mockito.when(idAuthService.authTypeStatusUpdate(authLockRequestDto.getIndividualId(),
				authLockRequestDto.getIndividualIdType().name(), authLockRequestDto.getAuthType(), AuthTypeStatus.LOCK))
				.thenThrow(new ApisResourceAccessException());

		residentService.reqAauthTypeStatusUpdate(authLockRequestDto, AuthTypeStatus.LOCK);

	}

	@Test(expected = ResidentServiceException.class)
	public void testReqAauthLockOTPFailedException()
			throws ApisResourceAccessException, ResidentServiceCheckedException, OtpValidationFailedException {

		Mockito.when(idAuthService.validateOtp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new OtpValidationFailedException());
		AuthLockOrUnLockRequestDto authLockRequestDto = new AuthLockOrUnLockRequestDto();
		authLockRequestDto.setIndividualId("1234567889");
		authLockRequestDto.setIndividualIdType(IdType.UIN);
		authLockRequestDto.setOtp("1234");
		authLockRequestDto.setTransactionID("1234567898");
		residentService.reqAauthTypeStatusUpdate(authLockRequestDto, AuthTypeStatus.LOCK);

	}
}
