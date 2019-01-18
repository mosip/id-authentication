package io.mosip.preregistration.booking.test.service.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookingServiceUtilTest {

	@MockBean
	private BookingAvailabilityRepository bookingAvailabilityRepository;

	@MockBean
	private RegistrationBookingRepository registrationBookingRepository;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@MockBean
	ObjectMapper mapper;
	
	@Autowired
	private BookingServiceUtil serviceUtil;

	@MockBean
	private BookingDAO bookingDAO;

	@Test
	public void mandatoryParameterCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2018-12-06");
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("10");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		boolean flag = serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBooking, newBooking);
		assertEquals(flag, true);
	}

	@Test(expected = BookingPreIdNotFoundException.class)
	public void regIdNullCheckTest() {
		String preRegistrationId = null;
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2018-12-06");
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("10");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBooking, newBooking);

	}

	@Test(expected = BookingRegistrationCenterIdNotFoundException.class)
	public void regCenterNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId(null);
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBooking, null);

	}

	@Test(expected = BookingTimeSlotNotSeletectedException.class)
	public void slotNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime(null);
		oldBooking.setSlotToTime(null);
		oldBooking.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBooking, null);

	}
	
	@Test(expected = BookingDateNotSeletectedException.class)
	public void regDateNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate(null);

		serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBooking, null);

	}

	@Test(expected = BookingRegistrationCenterIdNotFoundException.class)
	public void regCenterNullinNewBookingCheckTest() {
		String preRegistrationId = "23587986034785";

		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId(null);
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, null, newBooking);

	}

	@Test(expected = BookingTimeSlotNotSeletectedException.class)
	public void slotNullinNewBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("10");
		newBooking.setSlotFromTime(null);
		newBooking.setSlotToTime(null);
		newBooking.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, null, newBooking);

	}
	
	@Test(expected = BookingDateNotSeletectedException.class)
	public void bookingDateNotSeletectedExceptionTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("10");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate(null);

		serviceUtil.mandatoryParameterCheck(preRegistrationId, null, newBooking);

	}

	@Test
	public void isNotDuplicateCheckSuccessTest() {
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2018-12-06");
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("10");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		boolean flag = serviceUtil.isNotDuplicate(oldBooking, newBooking);
		assertEquals(flag, true);
	}

	@Test(expected = AppointmentReBookingFailedException.class)
	public void appointmentReBookingFailedExceptionTest() {
		BookingRegistrationDTO oldBooking = new BookingRegistrationDTO();
		oldBooking.setRegistrationCenterId("1");
		oldBooking.setSlotFromTime("09:00");
		oldBooking.setSlotToTime("09:13");
		oldBooking.setRegDate("2018-12-06");
		BookingRegistrationDTO newBooking = new BookingRegistrationDTO();
		newBooking.setRegistrationCenterId("1");
		newBooking.setSlotFromTime("09:00");
		newBooking.setSlotToTime("09:13");
		newBooking.setRegDate("2018-12-06");

		serviceUtil.isNotDuplicate(oldBooking, newBooking);

	}

	@Test
	public void cancelBookingDtoSetterTest() {
		String preRegistrationId = "23587986034785";
		BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
		bookingRegistrationDTO.setRegistrationCenterId("1");
		bookingRegistrationDTO.setSlotFromTime("09:00");
		bookingRegistrationDTO.setSlotToTime("09:13");
		bookingRegistrationDTO.setRegDate("2018-12-06");

		CancelBookingDTO cancelBookingDTO = serviceUtil.cancelBookingDtoSetter(preRegistrationId,
				bookingRegistrationDTO);
		assertEquals(cancelBookingDTO.getPreRegistrationId(), preRegistrationId);
	}

	@Test(expected=AvailablityNotFoundException.class)
	public void AvailablityNotFoundException() {
		AvailibityEntity availableEntity = new AvailibityEntity();
		availableEntity.setAvailableKiosks(0);
		availableEntity.setRegcntrId("1");
		availableEntity.setRegDate(LocalDate.parse("2018-12-04"));
		availableEntity.setCrBy("987654321");
		availableEntity.setCrDate(DateUtils.parseDateToLocalDateTime(new Date()));
		availableEntity.setDeleted(false);
		serviceUtil.isKiosksAvailable(availableEntity);
	}
	
	@Test(expected=BookingPreIdNotFoundException.class)
	public void bookingPreIdNotFoundExceptionforCancelTest() {
		
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setPreRegistrationId(null);
		serviceUtil.mandatoryParameterCheckforCancel(cancelBookingDTO);
		
	}
	
	@Test(expected=BookingRegistrationCenterIdNotFoundException.class)
	public void bookingRegistrationCenterIdNotFoundExceptionforCancelTest() {
		
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setPreRegistrationId("23587986034785");
		cancelBookingDTO.setRegistrationCenterId(null);
		serviceUtil.mandatoryParameterCheckforCancel(cancelBookingDTO);
		
	}
	
	@Test(expected=BookingDateNotSeletectedException.class)
	public void bookingDateNotSeletectedExceptionforCancelTest() {
		
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setPreRegistrationId("23587986034785");
		cancelBookingDTO.setRegistrationCenterId("1");
		cancelBookingDTO.setSlotFromTime("09:00");
		cancelBookingDTO.setSlotToTime("09:13");
		cancelBookingDTO.setRegDate(null);
		serviceUtil.mandatoryParameterCheckforCancel(cancelBookingDTO);
		
	}
	
	@Test(expected=BookingTimeSlotNotSeletectedException.class)
	public void bookingTimeSlotNotSeletectedExceptionforCancelTest() {
		
		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setPreRegistrationId("23587986034785");
		cancelBookingDTO.setRegistrationCenterId("1");
		cancelBookingDTO.setRegDate("2018-12-06");
		cancelBookingDTO.setSlotFromTime(null);
		cancelBookingDTO.setSlotToTime(null);
		serviceUtil.mandatoryParameterCheckforCancel(cancelBookingDTO);
		
	}
	
	@Test(expected=MasterDataNotAvailableException.class)
	public void callRegCenterDateRestServiceTest() {
		
		RegistrationCenterResponseDto preRegResponse= new RegistrationCenterResponseDto();
		preRegResponse.setRegistrationCenters(null);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		ResponseEntity<RegistrationCenterResponseDto> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterResponseDto.class))).thenReturn(res);
		
		serviceUtil.callRegCenterDateRestService();
		
	}
	
	@Test(expected=RestCallException.class)
	public void HttpClientErrorExceptionTest() {
		HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.OK);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(RegistrationCenterResponseDto.class))).thenThrow(ex);
		
		serviceUtil.callRegCenterDateRestService();
		
	}
	
	@Test(expected=DemographicStatusUpdationException.class)
	public void callUpdateStatusRestServiceTest() {
		RestClientException ex = new RestClientException(null);
		MainResponseDTO<String> preRegResponse= new MainResponseDTO<>();
		preRegResponse.setStatus(false);
		preRegResponse.setResponse(null);
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		preRegResponse.setErr(err);
		preRegResponse.setResTime(serviceUtil.getCurrentResponseTime());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenThrow(ex);
		
		serviceUtil.callUpdateStatusRestService("23587986034785", "Pending_Appointment");
		
	}
	
	@Test(expected=DemographicStatusUpdationException.class)
	public void callUpdateStatusRestService1Test() {
		
		MainResponseDTO<String> preRegResponse= new MainResponseDTO<>();
		preRegResponse.setStatus(false);
		preRegResponse.setResponse(null);
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		preRegResponse.setErr(err);
		preRegResponse.setResTime(serviceUtil.getCurrentResponseTime());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MainResponseDTO> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(MainResponseDTO.class))).thenReturn(res);
		
		serviceUtil.callUpdateStatusRestService("23587986034785", "Pending_Appointment");
		
	}

	
	@SuppressWarnings("unchecked")
	@Test(expected=AppointmentCannotBeCanceledException.class)
	public void callGetStatusRestServiceforCancelTest() {
		
		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		@SuppressWarnings("rawtypes")
		MainListResponseDTO preRegResponse = new MainListResponseDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		preRegResponse.setResponse(statusList);
		preRegResponse.setStatus(true);
		preRegResponse.setErr(null);
		preRegResponse.setResTime(serviceUtil.getCurrentResponseTime());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MainListResponseDTO> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(res);
		
		serviceUtil.callGetStatusForCancelRestService("23587986034785");
		
	}
	
	@Test(expected=DemographicGetStatusException.class)
	public void demographicGetStatusExceptionTest() {
		RestClientException ex =new RestClientException(null);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenThrow(ex);
		
		serviceUtil.callGetStatusForCancelRestService("23587986034785");
		
	}
	
	@Test(expected=DemographicGetStatusException.class)
	public void callGetStatusRestServiceforCancel1Test() {
		
		@SuppressWarnings("rawtypes")
		MainListResponseDTO preRegResponse = new MainListResponseDTO();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		preRegResponse.setStatus(false);
		preRegResponse.setErr(err);
		preRegResponse.setResTime(serviceUtil.getCurrentResponseTime());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MainListResponseDTO> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(res);
		
		serviceUtil.callGetStatusForCancelRestService("23587986034785");
		
	}
	
	@Test(expected=DemographicGetStatusException.class)
	public void callGetStatusRestServiceTest() {
		
		@SuppressWarnings("rawtypes")
		MainListResponseDTO preRegResponse = new MainListResponseDTO();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		preRegResponse.setStatus(false);
		preRegResponse.setErr(err);
		preRegResponse.setResTime(serviceUtil.getCurrentResponseTime());
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MainListResponseDTO> res = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenReturn(res);
		
		serviceUtil.callGetStatusRestService("23587986034785");
		
	}
	
	@Test(expected=DemographicGetStatusException.class)
	public void callGetStatusRestService1Test() {
		RestClientException ex =new RestClientException(null);
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(MainListResponseDTO.class))).thenThrow(ex);
		
		serviceUtil.callGetStatusRestService("23587986034785");
		
	}
	
	

}
