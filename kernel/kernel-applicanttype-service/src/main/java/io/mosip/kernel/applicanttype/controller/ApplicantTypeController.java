package io.mosip.kernel.applicanttype.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.applicanttype.dto.request.RequestDTO;
import io.mosip.kernel.applicanttype.dto.response.ResponseDTO;
import io.mosip.kernel.applicanttype.service.ApplicantTypeService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;

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

	@PreAuthorize("hasRole('INDIVIDUAL')")
	@ResponseFilter
	@PostMapping(value = "/getApplicantType")
	public ResponseWrapper<ResponseDTO> getApplicantType(@Valid @RequestBody RequestWrapper<RequestDTO> dto) {
		ResponseWrapper<ResponseDTO> response = new ResponseWrapper<>();
		response.setResponse(applicantTypeService.getApplicantType(dto.getRequest()));
		return response;
	}
}