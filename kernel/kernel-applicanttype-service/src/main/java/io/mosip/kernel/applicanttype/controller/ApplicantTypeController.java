package io.mosip.kernel.applicanttype.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.applicanttype.dto.response.ResponseDTO;
import io.mosip.kernel.applicanttype.service.ApplicantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@CrossOrigin
@RestController
@Api(value = "This service provide operations on applicant type", tags = { "ApplicantType" })
public class ApplicantTypeController {

	@Autowired
	private ApplicantTypeService applicantTypeService;

	@PostMapping(value = "/getApplicantType")
	@ApiOperation(value = "Get applicant type for provided queries", notes = "Get applicant type for matching queries", response = String.class)
	public ResponseEntity<ResponseDTO> getApplicantType(@Valid @RequestBody RequestDTO dto) {
		return new ResponseEntity<>(applicantTypeService.getApplicantType(dto), HttpStatus.OK);
	}
}
