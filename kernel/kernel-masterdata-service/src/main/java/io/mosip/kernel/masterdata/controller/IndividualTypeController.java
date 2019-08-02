package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.IndividualTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.service.IndividualTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This controller class provides crud operation on individual type.
 * 
 * @author Bal Vikash Sharma
 * @author Sidhant Agarwal
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

	/**
	 * This controller method provides with all individual types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the individual types.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/all")
	@ApiOperation(value = "Retrieve all the individual types with additional metadata", notes = "Retrieve all the individual types with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of individual types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving individual types") })
	public ResponseWrapper<PageDto<IndividualTypeExtnDto>> getIndividualTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<IndividualTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(individualTypeService.getIndividualTypes(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * API to search Individual Types.
	 * 
	 * @param request
	 *            the request DTO {@link SearchDto} wrapped in
	 *            {@link RequestWrapper}.
	 * @return the response i.e. multiple entities based on the search values
	 *         required.
	 */
	@ResponseFilter
	@PostMapping("/search")
	public ResponseWrapper<PageResponseDto<IndividualTypeExtnDto>> searchIndividuals(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<IndividualTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(individualTypeService.searchIndividuals(request.getRequest()));
		return responseWrapper;
	}

	/**
	 * API that returns the values required for the column filter columns.
	 * 
	 * @param request
	 *            the request DTO {@link FilterResponseDto} wrapper in
	 *            {@link RequestWrapper}.
	 * @return the response i.e. the list of values for the specific filter column
	 *         name and type.
	 */
	@ResponseFilter
	@PostMapping("/filtervalues")
	public ResponseWrapper<FilterResponseDto> individualsFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> requestWrapper) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(individualTypeService.individualsFilterValues(requestWrapper.getRequest()));
		return responseWrapper;
	}
}
