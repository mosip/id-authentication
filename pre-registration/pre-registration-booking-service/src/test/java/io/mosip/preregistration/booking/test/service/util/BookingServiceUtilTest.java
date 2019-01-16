package io.mosip.preregistration.booking.test.service.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
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
	
	@Test(expected=DemographicStatusUpdationException.class)
	public void callUpdateStatusRestServiceTest() {
		
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
	public void callGetStatusRestServiceTest() {
		DemographicStatusUpdationException ex = new DemographicStatusUpdationException();
		
		@SuppressWarnings("rawtypes")
		MainListResponseDTO preRegResponse = new MainListResponseDTO();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		preRegResponse.setResponse(null);
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
	
	@Test
	public void timeSlotCalculatorTest() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalTime localTime1;
		LocalTime localTime2;
		String date1 = "2016-11-09 09:00:00";
		String date2 = "2016-11-09 17:00:00";
		String date3 = "2016-11-09 00:20:00";
		String date4 = "2016-11-09 13:00:00";
		String date5 = "2016-11-09 14:20:00";
		LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);
		LocalDateTime localDateTime2 = LocalDateTime.parse(date2, format);
		LocalDateTime localDateTime3 = LocalDateTime.parse(date3, format);
		LocalTime startTime = localDateTime1.toLocalTime();
		LocalTime endTime = localDateTime2.toLocalTime();
		LocalTime perKioskTime = localDateTime3.toLocalTime();
		LocalTime LunchStartTime = LocalDateTime.parse(date4, format).toLocalTime();
		LocalTime LunchEndTime = LocalDateTime.parse(date5, format).toLocalTime();
		RegistrationCenterDto centerDto = new RegistrationCenterDto();
		List<RegistrationCenterDto> centerList = new ArrayList<>();
		centerDto.setId("1");
		centerDto.setLanguageCode("LOC01");
		centerDto.setCenterStartTime(startTime);
		centerDto.setCenterEndTime(endTime);
		centerDto.setPerKioskProcessTime(perKioskTime);
		centerDto.setLunchStartTime(LunchStartTime);
		centerDto.setLunchEndTime(LunchEndTime);
		centerDto.setNumberOfKiosks((short) 4);
		centerList.add(centerDto);
		//regCenDto.setRegistrationCenters(centerList);
		
		RegistrationCenterHolidayDto CenholidayDto = new RegistrationCenterHolidayDto();
		HolidayDto holiday = new HolidayDto();
		List<String> holidayList = new ArrayList<>();
//		holiday.setHolidayDate("2018-12-12");
//		holidayList.add(holiday);
//		CenholidayDto.setHolidays(holidayList);
		holidayList.add(0, date1);
		serviceUtil.timeSlotCalculator(centerDto, holidayList, LocalDate.parse("2016-11-09"), bookingDAO);
	}
}
