package org.mosip.demo.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.Valid;

import org.mosip.demo.dto.OtpGeneratorRequestDto;
import org.mosip.demo.dto.OtpGeneratorResponseDto;
import org.mosip.demo.dto.OtpValidatorResponseDto;
import org.mosip.demo.dto.PersonDto;
import org.mosip.demo.entity.Enrollment;
import org.mosip.demo.entity.Person;
import org.mosip.demo.service.SchedulingService;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidDataException;
import org.mosip.kernel.core.mosipsecurity.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonGenerationException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Scheduling controller with apis to get all enrollment centers and add new
 * registraion
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
@CrossOrigin
public class SchedulingController {
	/**
	 * Scheduling Service field with functions related to enrollment scheduling
	 */
	@Autowired
	SchedulingService service;

	/**
	 * Function to get all enrollment centers
	 * 
	 * @return List of enrollment centers
	 */
	@GetMapping(value = "/enrolmentcenters")
	public ResponseEntity<List<Enrollment>> getEnrollmentCenters() {

		return new ResponseEntity<>(service.getEnrolmentCenters(), HttpStatus.OK);
	}

	@GetMapping(value = "/dataaccessdemo")
	public ResponseEntity<Boolean> dataaccessdemo() {

		return new ResponseEntity<>(service.daoDemo(), HttpStatus.OK);
	}


	@GetMapping(value = "/securitydemo")
	public ResponseEntity<Boolean> securitydemo()
			throws MosipInvalidDataException, MosipInvalidKeyException, NoSuchAlgorithmException, IOException {

		return new ResponseEntity<>(service.securitydemo(), HttpStatus.OK);
	}

	@GetMapping(value = "/jsondemo")
	public ResponseEntity<Boolean> jsonDemo()
			throws MosipJsonGenerationException, MosipJsonMappingException, MosipIOException, MosipJsonParseException {

		return new ResponseEntity<>(service.jsonDemo(), HttpStatus.OK);
	}

	@GetMapping(value = "/zipdemo")
	public ResponseEntity<Boolean> zipDemo() throws MosipIOException {

		return new ResponseEntity<>(service.zipDemo(), HttpStatus.OK);
	}

	@PostMapping(value = "/getOtp")
	public ResponseEntity<OtpGeneratorResponseDto> getOtp(@Valid @RequestBody OtpGeneratorRequestDto otpDto) {
		return new ResponseEntity<>(service.getOtp(otpDto), HttpStatus.CREATED);
	}

	@GetMapping(value = "/validateOtp")
	public ResponseEntity<OtpValidatorResponseDto> validateOtp(@RequestParam String key, @RequestParam String otp)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		return new ResponseEntity<>(service.validateOtp(key, otp), HttpStatus.OK);
	}

	/**
	 * Function to add new registration
	 * 
	 * @param personDto
	 * @return created person
	 */
	@PostMapping(value = "/registrations")
	public ResponseEntity<Person> addRegistration(@RequestBody PersonDto personDto) {

		return new ResponseEntity<>(service.addSchedule(personDto), HttpStatus.CREATED);
	}

}
