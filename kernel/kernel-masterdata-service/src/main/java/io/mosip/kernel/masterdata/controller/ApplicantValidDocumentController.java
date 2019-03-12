package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.ApplicantValidDocumentDto;
import io.mosip.kernel.masterdata.service.ApplicantValidDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/applicanttype")
@Api(tags = { "ApplicantValidDocument" })
public class ApplicantValidDocumentController {

	@Autowired
	private ApplicantValidDocumentService applicantValidDocumentService;

	@GetMapping(value = "/{applicantId}/languages")
	@ApiOperation(value = "get value from Caretory for the given id", notes = "get value from Category for the given id", response = ApplicantValidDocumentDto.class)
	public ResponseEntity<ApplicantValidDocumentDto> getApplicantValidDocument(@PathVariable String applicantId,
			@RequestParam("languages") List<String> languages) {
		return new ResponseEntity<>(applicantValidDocumentService.getDocumentCategoryAndTypes(applicantId, languages),
				HttpStatus.OK);
	}

}
