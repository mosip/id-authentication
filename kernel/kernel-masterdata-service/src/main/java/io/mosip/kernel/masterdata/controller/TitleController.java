package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TitleExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.TitleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for fetching titles from master data
 * 
 * @author Sidhant Agarwal
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "Title" })
public class TitleController {

	@Autowired
	private TitleService titleService;

	/**
	 * Method to return list of all titles
	 * 
	 * @return list of all titles present in master DB
	 */
	@PreAuthorize("hasAnyRole('ID_AUTHENTICATION','ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping(value = "/title")
	public ResponseWrapper<TitleResponseDto> getAllTitles() {
		ResponseWrapper<TitleResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.getAllTitles());
		return responseWrapper;
	}

	/**
	 * Method to return list of titles for a particular language code
	 * 
	 * @param langCode
	 *            input to fetch all titles belonging to the particular language
	 *            code
	 * @return list of all titles for the particular language code
	 */
	@ResponseFilter
	@GetMapping(value = "/title/{langcode}")
	public ResponseWrapper<TitleResponseDto> getTitlesBylangCode(@PathVariable("langcode") String langCode) {

		ResponseWrapper<TitleResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.getByLanguageCode(langCode));
		return responseWrapper;
	}

	/**
	 * Method to add a new row of title data
	 * 
	 * @param title
	 *            input from user
	 * @return primary key of entered row
	 */
	@ResponseFilter
	@PostMapping("/title")
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	public ResponseWrapper<CodeAndLanguageCodeID> saveTitle(@Valid @RequestBody RequestWrapper<TitleDto> title) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.saveTitle(title.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller class to update title data
	 * 
	 * @param titles
	 *            input DTO for updated row
	 * @return composite primary key of updated row
	 */
	@ResponseFilter
	@PutMapping("/title")
	@ApiOperation(value = "Service to update title", notes = "Update title and return composite id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When title successfully updated"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No title found"),
			@ApiResponse(code = 500, message = "While updating title any error occured") })
	public ResponseWrapper<CodeAndLanguageCodeID> updateTitle(
			@ApiParam("Title DTO to update") @Valid @RequestBody RequestWrapper<TitleDto> titles) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.updateTitle(titles.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller class to delete title data
	 * 
	 * @param code
	 *            input from user
	 * @return composite key of deleted row of data
	 */
	@ResponseFilter
	@DeleteMapping("/title/{code}")
	@ApiOperation(value = "Service to delete title", notes = "Delete title and return composite id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When title successfully deleted"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No title found"),
			@ApiResponse(code = 500, message = "While deleting title any error occured") })
	public ResponseWrapper<CodeResponseDto> deleteTitle(@PathVariable("code") String code) {

		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.deleteTitle(code));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all titles.
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
	 * @return the response i.e. pages containing the titles.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/title/all")
	@ApiOperation(value = "Retrieve all the title with additional metadata", notes = "Retrieve all the title with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of title"),
			@ApiResponse(code = 500, message = "Error occured while retrieving title") })
	public ResponseWrapper<PageDto<TitleExtnDto>> getTitles(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<TitleExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.getTitles(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * Search titles.
	 *
	 * @param request
	 *            the request
	 * @return response wrapper
	 */
	
	@ResponseFilter
	@PostMapping("title/search")
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	@ApiOperation(value = "Search title details")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of titles"),
			@ApiResponse(code = 500, message = "Error occured while searching title") })
	public ResponseWrapper<PageResponseDto<TitleExtnDto>> searchTitles(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<TitleExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.searchTitles(request.getRequest()));
		return responseWrapper;
	}
	
	/**
	 * Filter templates.
	 *
	 * @param request the request
	 * @return {@link FilterResponseDto}
	 */
	@ResponseFilter
	@PostMapping("title/filtervalues")
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	@ApiOperation(value = "filter title details")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of title"),
			@ApiResponse(code = 500, message = "Error occured while retrieving templates") })
	public ResponseWrapper<FilterResponseDto> filterTemplates(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(titleService.filterTitles(request.getRequest()));
		return responseWrapper;
	}
}
