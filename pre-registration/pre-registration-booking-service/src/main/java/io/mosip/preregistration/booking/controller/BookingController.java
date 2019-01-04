package io.mosip.preregistration.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.MainListRequestDTO;
import io.mosip.preregistration.booking.dto.MainListResponseDTO;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.MainResponseDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.booking.service.BookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Booking Controller
 * 
 * @author M1037717
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/booking/")
@Api(tags = "Booking")
@CrossOrigin("*")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	/**
	 * 
	 * @return
	 */
	@GetMapping(path = "/masterSynchronization", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Sync master Data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Master Data Sync is successful"),
			@ApiResponse(code = 400, message = "Unable to fetch the records") })
	public ResponseEntity<MainResponseDTO<String>> saveAvailability() {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.addAvailability());
	}

	/**
	 * @param regID
	 * @return ResponseDto<AvailabilityDto>
	 */
	@GetMapping(path = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch availability Data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Availablity details fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch the records") })
	public ResponseEntity<MainResponseDTO<AvailabilityDto>> getAvailability(
			@RequestParam(value = "RegCenterId") String regID) {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAvailability(regID));
	}

	/**
	 * @param bookingDTO
	 * @return response entity
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PostMapping(path = "/book", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Booking Appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment Booked Successfully"),
			@ApiResponse(code = 400, message = "Unable to Book the appointment") })
	public ResponseEntity<MainResponseDTO<List<BookingStatusDTO>>> bookAppoinment(
			@RequestBody(required = true) MainListRequestDTO<BookingRequestDTO> bookingDTO){
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.bookAppointment(bookingDTO));
	}
	
	

	/**
	 * @param bookingDTO
	 * @return response entity
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@GetMapping(path = "/appointmentDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fecth Appointment details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment Booked Successfully"),
			@ApiResponse(code = 400, message = "Unable to Book the appointment") })
	public ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> appointmentDetails(
			@RequestParam(value = "preRegID") String preRegID) {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAppointmentDetails(preRegID));

	}
	
	/**
	 * @param bookingDTO
	 * @return response entity
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PutMapping(path = "/book", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Cancel an booked appointment")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Appointment canceled successfully"),
			@ApiResponse(code = 400, message = "Unable to cancel the appointment") })
	public ResponseEntity<MainResponseDTO<CancelBookingResponseDTO>> cancelBook(
			@RequestBody MainRequestDTO<CancelBookingDTO> requestDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.cancelAppointment(requestDTO));
	}
	
	/**
	 * @param bookingDTO
	 * @return response entity
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	@PostMapping(path = "/bookedPreIdsByRegId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Reterive all pre-registration ids by registration center id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Reterived all pre-registration ids successfully"),
			@ApiResponse(code = 400, message = "Unable to reterive pre-registration ids") })
	public ResponseEntity<MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> getPreIdsByRegCenterId(
			@RequestBody MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.getPreIdsByRegCenterId(requestDTO));
	}

}
