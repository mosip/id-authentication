package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
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
@RequestMapping(value = "/individualtypes")
@Api(tags = { "IndividualType" })
public class IndividualTypeController {

	@Autowired
	private IndividualTypeService individualTypeService;

	/**
	 * @return the all active individual type.
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping
	@ApiOperation(value = "get value from Caretory for the given id", notes = "get value from Category for the given id")
	public ResponseWrapper<IndividualTypeResponseDto> getAllIndividualTypes() {
		ResponseWrapper<IndividualTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(individualTypeService.getAllIndividualTypes());
		return responseWrapper;
	}
}
