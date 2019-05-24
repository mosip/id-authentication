package io.mosip.preregistration.booking.test.service.util;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.codes.RequestCodes;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
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
import io.mosip.preregistration.booking.test.BookingApplicationTest;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { BookingApplicationTest.class })
public class BookingServiceUtilTest {

	@MockBean
	private BookingAvailabilityRepository bookingAvailabilityRepository;

	@MockBean
	private RegistrationBookingRepository registrationBookingRepository;

	@MockBean
	RestTemplate restTemplate;

	@MockBean
	ObjectMapper mapper;

	@Autowired
	private BookingServiceUtil serviceUtil;

	@MockBean
	private BookingDAO bookingDAO;

	@Test
	public void mandatoryParameterCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate("2018-12-06");

		boolean flag = serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);
		assertEquals(true, flag);
	}

	@Test(expected = BookingPreIdNotFoundException.class)
	public void regIdNullCheckTest() {
		String preRegistrationId = null;
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingRegistrationCenterIdNotFoundException.class)
	public void regCenterNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId(null);
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingTimeSlotNotSeletectedException.class)
	public void slotNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime(null);
		bookingRequestDTO.setSlotToTime(null);
		bookingRequestDTO.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingDateNotSeletectedException.class)
	public void regDateNullinOldBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("1");
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate(null);

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingRegistrationCenterIdNotFoundException.class)
	public void regCenterNullinNewBookingCheckTest() {
		String preRegistrationId = "23587986034785";

		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId(null);
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingTimeSlotNotSeletectedException.class)
	public void slotNullinNewBookingCheckTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("10");
		bookingRequestDTO.setSlotFromTime(null);
		bookingRequestDTO.setSlotToTime(null);
		bookingRequestDTO.setRegDate("2018-12-06");

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

	}

	@Test(expected = BookingDateNotSeletectedException.class)
	public void bookingDateNotSeletectedExceptionTest() {
		String preRegistrationId = "23587986034785";
		BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
		bookingRequestDTO.setRegistrationCenterId("10");
		bookingRequestDTO.setSlotFromTime("09:00");
		bookingRequestDTO.setSlotToTime("09:13");
		bookingRequestDTO.setRegDate(null);

		serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO);

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
		assertEquals("1", bookingRegistrationDTO.getRegistrationCenterId());
	}

	@Test
	public void validateAppointmentDateTest() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, String> dateMap = new HashMap<>();
		dateMap.put(RequestCodes.REG_DATE.getCode(), sdf.format(cal.getTime()));
		dateMap.put(RequestCodes.FROM_SLOT_TIME.getCode(), "09:30");
		boolean isValid = serviceUtil.validateAppointmentDate(dateMap);
		assertEquals(Boolean.TRUE, isValid);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void validateAppointmentDateExceptionTest() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, String> dateMap = new HashMap<>();
		dateMap.put(RequestCodes.REG_DATE.getCode(), sdf.format(cal.getTime()));
		dateMap.put(RequestCodes.FROM_SLOT_TIME.getCode(), "09:30");
		boolean isValid = serviceUtil.validateAppointmentDate(dateMap);
		assertEquals(Boolean.FALSE, isValid);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void validateAppointmentTimeExceptionTest() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, String> dateMap = new HashMap<>();
		dateMap.put(RequestCodes.REG_DATE.getCode(), sdf.format(cal.getTime()));
		dateMap.put(RequestCodes.FROM_SLOT_TIME.getCode(), "09:30");
		boolean isValid = serviceUtil.validateAppointmentDate(dateMap);
		assertEquals(Boolean.FALSE, isValid);
	}

	@Test(expected = AvailablityNotFoundException.class)
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

	@Test(expected = BookingPreIdNotFoundException.class)
	public void bookingPreIdNotFoundExceptionforCancelTest() {

		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		serviceUtil.mandatoryParameterCheckforCancel(null);

	}

	// @Test(expected=BookingRegistrationCenterIdNotFoundException.class)
	public void bookingRegistrationCenterIdNotFoundExceptionforCancelTest() {

		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setRegistrationCenterId(null);
		serviceUtil.mandatoryParameterCheckforCancel("23587986034785");

	}

	// @Test(expected=BookingDateNotSeletectedException.class)
	public void bookingDateNotSeletectedExceptionforCancelTest() {

		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setRegistrationCenterId("1");
		cancelBookingDTO.setSlotFromTime("09:00");
		cancelBookingDTO.setSlotToTime("09:13");
		cancelBookingDTO.setRegDate(null);
		serviceUtil.mandatoryParameterCheckforCancel("23587986034785");

	}

	// @Test(expected=BookingTimeSlotNotSeletectedException.class)
	public void bookingTimeSlotNotSeletectedExceptionforCancelTest() {

		CancelBookingDTO cancelBookingDTO = new CancelBookingDTO();
		cancelBookingDTO.setRegistrationCenterId("1");
		cancelBookingDTO.setRegDate("2018-12-06");
		cancelBookingDTO.setSlotFromTime(null);
		cancelBookingDTO.setSlotToTime(null);
		serviceUtil.mandatoryParameterCheckforCancel("23587986034785");

	}

	@Test(expected = MasterDataNotAvailableException.class)
	public void callRegCenterDateRestServiceTest() {

		RegistrationCenterResponseDto preRegResponse = new RegistrationCenterResponseDto();
		preRegResponse.setRegistrationCenters(null);
		ResponseWrapper<RegistrationCenterResponseDto> resp = new ResponseWrapper<>();
		resp.setResponse(preRegResponse);
		ResponseEntity<ResponseWrapper<RegistrationCenterResponseDto>> res = new ResponseEntity<>(resp, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterResponseDto>>() {
				}))).thenReturn(res);

		serviceUtil.getRegCenterMasterData();

	}

	@Test(expected = RestCallException.class)
	public void HttpClientErrorExceptionTest() {
		HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<RegistrationCenterResponseDto>>() {
				}))).thenThrow(ex);

		serviceUtil.getRegCenterMasterData();

	}

	@Test(expected = DemographicStatusUpdationException.class)
	public void callUpdateStatusRestServiceTest() {

		Map<String, Object> map = new HashMap<>();
		map.put("pre_rgistration_id", "23587986034785");

		RestClientException ex = new RestClientException(null);
		MainResponseDTO<String> preRegResponse = new MainResponseDTO<>();
		preRegResponse.setResponse(null);
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		List<ExceptionJSONInfoDTO> errList = new ArrayList<>();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		errList.add(err);
		preRegResponse.setErrors(errList);
		preRegResponse.setResponsetime(serviceUtil.getCurrentResponseTime());
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {
				}), Mockito.anyMap())).thenThrow(ex);
		serviceUtil.callUpdateStatusRestService("23587986034785", "Pending_Appointment");

	}

	@Test(expected = DemographicStatusUpdationException.class)
	public void callUpdateStatusRestService1Test() {

		MainResponseDTO<String> preRegResponse = new MainResponseDTO<>();
		preRegResponse.setResponse(null);
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		List<ExceptionJSONInfoDTO> errList = new ArrayList<>();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		errList.add(err);
		preRegResponse.setErrors(errList);
		preRegResponse.setResponsetime(serviceUtil.getCurrentResponseTime());
		ResponseEntity<MainResponseDTO<String>> resp2 = new ResponseEntity<>(preRegResponse, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<String>>() {
				}), Mockito.anyMap())).thenReturn(resp2);
		serviceUtil.callUpdateStatusRestService("23587986034785", "Pending_Appointment");

	}

	@SuppressWarnings("unchecked")
	@Test(expected = BookingDataNotFoundException.class)
	public void callGetStatusRestServiceforCancelTest() {

		List<PreRegistartionStatusDTO> statusList = new ArrayList<>();
		PreRegistartionStatusDTO preRegistartionStatusDTO = new PreRegistartionStatusDTO();
		@SuppressWarnings("rawtypes")
		MainResponseDTO preRegResponse = new MainResponseDTO();
		preRegistartionStatusDTO.setStatusCode(StatusCodes.PENDING_APPOINTMENT.getCode());
		preRegistartionStatusDTO.setPreRegistartionId("23587986034785");
		statusList.add(preRegistartionStatusDTO);

		preRegResponse.setResponse(preRegistartionStatusDTO);
		preRegResponse.setErrors(null);
		preRegResponse.setResponsetime(serviceUtil.getCurrentResponseTime());
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		serviceUtil.callGetStatusForCancelRestService("23587986034785");

	}

	@Test(expected = DemographicGetStatusException.class)
	public void demographicGetStatusExceptionTest() {
		RestClientException ex = new RestClientException(null);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenThrow(ex);
		serviceUtil.callGetStatusForCancelRestService("23587986034785");

	}

	@Test(expected = DemographicGetStatusException.class)
	public void callGetStatusRestServiceforCancel1Test() {

		@SuppressWarnings("rawtypes")
		MainResponseDTO preRegResponse = new MainResponseDTO();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		List<ExceptionJSONInfoDTO> list = new ArrayList<>();
		list.add(err);
		preRegResponse.setErrors(list);
		preRegResponse.setResponsetime(serviceUtil.getCurrentResponseTime());
		@SuppressWarnings("unchecked")
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		serviceUtil.callGetStatusForCancelRestService("23587986034785");

	}

	@Test(expected = DemographicGetStatusException.class)
	public void callGetStatusRestServiceTest() {

		@SuppressWarnings("rawtypes")
		MainResponseDTO preRegResponse = new MainResponseDTO();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO();
		err.setErrorCode(ErrorCodes.PRG_BOOK_RCI_011.name());
		err.setMessage(ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.getMessage());
		List<ExceptionJSONInfoDTO> list = new ArrayList<>();
		list.add(err);
		preRegResponse.setErrors(list);
		preRegResponse.setResponsetime(serviceUtil.getCurrentResponseTime());
		@SuppressWarnings("unchecked")
		ResponseEntity<MainResponseDTO<PreRegistartionStatusDTO>> res = new ResponseEntity<>(preRegResponse,
				HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenReturn(res);
		serviceUtil.callGetStatusRestService("23587986034785");

	}

	@Test(expected = DemographicGetStatusException.class)
	public void callGetStatusRestService1Test() {
		RestClientException ex = new RestClientException(null);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(),
				Mockito.eq(new ParameterizedTypeReference<MainResponseDTO<PreRegistartionStatusDTO>>() {
				}), Mockito.anyMap())).thenThrow(ex);
		serviceUtil.callGetStatusRestService("23587986034785");

	}

}
