package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.ExceptionalHolidayResponseDto;
import io.mosip.kernel.masterdata.service.ExceptionalHolidayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Kishan Rathore
 *
 */
@RestController
@Api(tags = { "Exceptional Holidays" })
public class ExceptionalHolidayController {

	@Autowired
	private ExceptionalHolidayService service;

	/**
	 * 
	 * Function to fetch exceptional holidays detail based on given Registration
	 * center ID and Language code.
	 * 
	 * @param ExceptionalHolidayResponseDto
	 * @return ExceptionalHolidayResponseDto exceptional holidays based on given Registration center ID
	 *         and Language code {@link ExceptionalHolidayResponseDto}
	 */
	@ResponseFilter
	@GetMapping(value = "/exceptionalholidays/{registrationCenterId}/{languagecode}")
	@ApiOperation(value = "Retrieve all Exceptional Holidays for given Registration center ID and Languge Code", notes = "Retrieve all Week Days for given Registration center ID and Languge Code")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Exceptional Holidays from database for the given Registration center ID Languge Code"),
			@ApiResponse(code = 404, message = "When Exceptional Holidays found for the given Registration center ID and Languge Code"),
			@ApiResponse(code = 500, message = "While retrieving Exceptional Holidays any error occured") })
	public ResponseWrapper<ExceptionalHolidayResponseDto> getExceptionalHolidays(
			@PathVariable("registrationCenterId") String regCenterId, @PathVariable("languagecode") String langCode) {

		ResponseWrapper<ExceptionalHolidayResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(service.getAllExceptionalHolidays(regCenterId, langCode));
		return responseWrapper;
	}

}
