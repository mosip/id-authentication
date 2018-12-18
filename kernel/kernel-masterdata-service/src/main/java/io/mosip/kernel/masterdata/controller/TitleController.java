package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
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
	@GetMapping(value = "/v1.0/title")
	public TitleResponseDto getAllTitles() {
		return titleService.getAllTitles();
	}

	/**
	 * Method to return list of titles for a particular language code
	 * 
	 * @param langCode
	 *            input to fetch all titles belonging to the particular language
	 *            code
	 * @return list of all titles for the particular language code
	 */
	@GetMapping(value = "/v1.0/title/{langcode}")
	public TitleResponseDto getTitlesBylangCode(@PathVariable("langcode") String langCode) {
		return titleService.getByLanguageCode(langCode);
	}

	/**
	 * Method to add a new row of title data
	 * 
	 * @param title
	 *            input from user
	 * @return primary key of entered row
	 */
	@PostMapping("/v1.0/title")
	public ResponseEntity<CodeAndLanguageCodeID> saveTitle(@Valid @RequestBody RequestDto<TitleDto> title) {
		return new ResponseEntity<>(titleService.saveTitle(title), HttpStatus.CREATED);

	}
	
	
	@PutMapping("/v1.0/title")
	@ApiOperation(value = "Service to update title", notes = "Update title and return composite id", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When title successfully updated", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No title found"),
			@ApiResponse(code = 500, message = "While updating title any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> updateTitle(
			@ApiParam("Title DTO to update") @Valid @RequestBody RequestDto<TitleDto> titles) {
		return new ResponseEntity<>(titleService.updateTitle(titles), HttpStatus.OK);
	}

}
