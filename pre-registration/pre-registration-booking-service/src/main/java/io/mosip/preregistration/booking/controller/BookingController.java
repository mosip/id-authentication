/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatus;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.MultiBookingRequest;
import io.mosip.preregistration.booking.service.BookingService;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides different API's to perform operations on Booking
 * Application
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/")
@Api(tags = "Booking")
@CrossOrigin("*")
public class BookingController {

	/** Autowired reference for {@link #bookingService}. */
	@Autowired
	private BookingService bookingService;

	private Logger log = LoggerConfiguration.logConfig(BookingController.class);

	/**
	 * Get API to save availability.
	 * 
	 * @return MainResponseDto .
	 */
	@PreAuthorize("hasAnyRole('PRE_REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR')")
	@GetMapping(path = "/appointment/availability/sync", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Sync master Data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Master Data Sync is successful") })
	public ResponseEntity<MainResponseDTO<String>> saveAvailability() {
		log.info("sessionId", "idType", "id",
				"In saveAvailability method of Booking controller for synching master data to get availability ");
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.addAvailability());
	}

	/**
	 * Get API to get availability details.
	 * 
	 * @param registration_center_id
	 * @return MainResponseDTO
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@GetMapping(path = "/appointment/availability/{registrationCenterId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch availability Data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Availablity details fetched successfully") })
	public ResponseEntity<MainResponseDTO<AvailabilityDto>> getAvailability(
			@PathVariable("registrationCenterId") String registrationCenterId) {
		log.info("sessionId", "idType", "id",
				"In getAvailability method of Booking controller to fetch the availability for regID: "
						+ registrationCenterId);
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAvailability(registrationCenterId));
	}

	/**
	 * Post API to book the appointment.
	 * 
	 * @param MainListRequestDTO
	 * @return MainResponseDTO
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@PostMapping(path = "/appointment/{preRegistrationId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Booking Appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment Booked Successfully") })
	public ResponseEntity<MainResponseDTO<BookingStatusDTO>> bookAppoinment(
			@PathVariable("preRegistrationId") String preRegistrationId,
			@RequestBody(required = true) MainRequestDTO<BookingRequestDTO> bookingDTO) {
		log.info("sessionId", "idType", "id",
				"In bookAppoinment method of Booking controller to book an appointment for object: " + bookingDTO);
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.bookAppointment(bookingDTO, preRegistrationId));
	}
	
	/**
	 * Post API to book the appointment.
	 * 
	 * @param MainListRequestDTO
	 * @return MainResponseDTO
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@PostMapping(path = "/appointment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Booking Appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment Booked Successfully") })
	public ResponseEntity<MainResponseDTO<BookingStatus>> bookMultiAppoinment(
			@RequestBody(required = true) MainRequestDTO<MultiBookingRequest> bookingRequest) {
		log.info("sessionId", "idType", "id",
				"In bookAppoinment method of Booking controller to book an appointment for object: " + bookingRequest);
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.bookMultiAppointment(bookingRequest));
	}

	/**
	 * Get API to get the booked appointment details.
	 * 
	 * @param MainListRequestDTO
	 * @return MainResponseDTO
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */

	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','REGISTRATION_ ADMIN','PRE_REGISTRATION_ADMIN')")
	@GetMapping(path = "/appointment/{preRegistrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch Appointment details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment details fetched Successfully") })
	public ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> getAppointments(
			@PathVariable("preRegistrationId") String preRegistrationId) {
		log.info("sessionId", "idType", "id",
				"In appointmentDetails method of Booking controller to fetch appointment details for preRegID: "
						+ preRegistrationId);
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAppointmentDetails(preRegistrationId));

	}

	/**
	 * Put API to cancel the appointment.
	 * 
	 * @param MainListRequestDTO
	 * @return MainResponseDTO
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@PutMapping(path = "/appointment/{preRegistrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Cancel an booked appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment canceled successfully") })
	public ResponseEntity<MainResponseDTO<CancelBookingResponseDTO>> cancelBook(
			@PathVariable("preRegistrationId") String preRegistrationId) {
		log.info("sessionId", "idType", "id",
				"In cancelBook method of Booking controller to cancel the appointment for object: " + preRegistrationId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(bookingService.cancelAppointment(preRegistrationId));
	}
	
	/**
	 * Put API to cancel the appointment for Pre-registration batch job.
	 * 
	 * @param MainListRequestDTO
	 * @return MainResponseDTO
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PreAuthorize("hasAnyRole('PRE_REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR')")
	@PutMapping(path = "/batch/appointment/{preRegistrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Cancel an booked appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment canceled successfully") })
	public ResponseEntity<MainResponseDTO<CancelBookingResponseDTO>> cancelAppointmentBatch(
			@PathVariable("preRegistrationId") String preRegistrationId) {
		log.info("sessionId", "idType", "id",
				"In cancelAppointmentBatch method of Booking controller to cancel the appointment for object: " + preRegistrationId+" triggered by batch job");
		return ResponseEntity.status(HttpStatus.OK)
				.body(bookingService.cancelAppointmentBatch(preRegistrationId));
	}

	/**
	 * Delete API to delete the Individual booking associated with the PreId.
	 *
	 * @param preId
	 *            the pre id
	 * @return the deletion status of booking for a pre-id
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@DeleteMapping(path = "/appointment", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Discard Booking")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of Booking is successfully") })
	public ResponseEntity<MainResponseDTO<DeleteBookingDTO>> discardIndividual(
			@RequestParam(value = "preRegistrationId") String preId) {
		log.info("sessionId", "idType", "id", "In Booking controller for deletion of booking with preId " + preId);

		return ResponseEntity.status(HttpStatus.OK).body(bookingService.deleteBooking(preId));
	}

	/**
	 * Get API to fetch all the booked pre-ids within from-date and to-date range.
	 *
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @return the booked pre-ids for date range
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','REGISTRATION_ ADMIN')")
	@GetMapping(path = "/appointment/preRegistrationId/{registrationCenterId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion ids By Booked Date Time And Registration center id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Booked data successfully retrieved") })
	public ResponseEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> getBookedDataByDate(
			@RequestParam(value = "from_date", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String fromDate,
			@RequestParam(value = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd") String toDate,
			@PathVariable("registrationCenterId") String regCenterId) {
		log.info("sessionId", "idType", "id",
				"In booking controller for fetching all booked preids " + fromDate + " to " + toDate);
		return ResponseEntity.status(HttpStatus.OK)
				.body(bookingService.getBookedPreRegistrationByDate(fromDate, toDate, regCenterId));
	}

}
