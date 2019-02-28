package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.service.IndividualTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * This controller class provides crud operation on individual type.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping(value = "/v1.0/individualtypes")
@Api(tags = { "IndividualType" })
public class IndividualTypeController {

	@Autowired
	private IndividualTypeService individualTypeService;

	@GetMapping
	@ApiOperation(value = "get value from Caretory for the given id", notes = "get value from Category for the given id", response = IndividualTypeResponseDto.class)
	public ResponseEntity<IndividualTypeResponseDto> getAllIndividualTypes() {
		return new ResponseEntity<>(individualTypeService.getAllIndividualTypes(), HttpStatus.OK);
	}
}
