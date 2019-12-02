package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.MachineTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * This controller class to save Machine type details.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "MachineType" })
public class MachineTypeController {

	/**
	 * Reference to MachineType Service.
	 */
	@Autowired
	private MachineTypeService machinetypeService;

	/**
	 * Post API to insert a new row of Machine Type data
	 * 
	 * @param machineType
	 *            input Machine Type DTO from user
	 * 
	 * @return ResponseEntity Machine Type Code and Language Code which is
	 *         successfully inserted
	 * 
	 */
	@ResponseFilter
	@PostMapping("/machinetypes")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	@ApiOperation(value = "Service to save Machine Type", notes = "Saves MachineType and return  code and Languge Code")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Machine Type successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Machine Type any error occured") })
	public ResponseWrapper<CodeAndLanguageCodeID> createMachineType(
			@Valid @RequestBody RequestWrapper<MachineTypeDto> machineType) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machinetypeService.createMachineType(machineType.getRequest()));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all machine types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * @return the response i.e. pages containing the machine types.
	 */
    @PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/machinetypes/all")
	@ApiOperation(value = "Retrieve all the machine types with additional metadata", notes = "Retrieve all the machine types with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of machine types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving machine types") })
	public ResponseWrapper<PageDto<MachineTypeExtnDto>> getAllMachineTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<MachineTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(machinetypeService.getAllMachineTypes(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * Api to search Machine Types.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link MachineTypeExtnDto}.
	 */
	@ResponseFilter
	@PostMapping("/machinetypes/search")
	@ApiOperation(value = "Api to search Machine Types")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	public ResponseWrapper<PageResponseDto<MachineTypeExtnDto>> searchMachineType(
			@ApiParam(value = "Request DTO to search Machine Types") @RequestBody @Valid RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<MachineTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machinetypeService.searchMachineType(request.getRequest()));
		return responseWrapper;
	}

	/**
	 * Api to filter Machine Types based on column and type provided.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	@ResponseFilter
	@PostMapping("/machinetypes/filtervalues")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	public ResponseWrapper<FilterResponseDto> machineTypesFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machinetypeService.machineTypesFilterValues(request.getRequest()));
		return responseWrapper;
	}
}
