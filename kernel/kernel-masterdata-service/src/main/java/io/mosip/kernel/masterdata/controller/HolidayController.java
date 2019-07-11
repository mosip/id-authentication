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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayIDDto;
import io.mosip.kernel.masterdata.dto.HolidayIdDeleteDto;
import io.mosip.kernel.masterdata.dto.HolidayUpdateDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.service.HolidayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller class for Holiday table
 * 
 * @author Sidhant Agarwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "Holiday" })
@RequestMapping("/holidays")
public class HolidayController {

	@Autowired
	private HolidayService holidayService;

	/**
	 * This method returns all holidays present in master db
	 * 
	 * @return list of all holidays
	 */
	@ResponseFilter
	@GetMapping
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	public ResponseWrapper<HolidayResponseDto> getAllHolidays() {
		ResponseWrapper<HolidayResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.getAllHolidays());
		return responseWrapper;
	}

	/**
	 * This method returns list of holidays for a particular holiday id
	 * 
	 * @param holidayId input parameter holiday id
	 * @return list of holidays for a particular holiday id
	 */
	@ResponseFilter
	@GetMapping("/{holidayid}")
	public ResponseWrapper<HolidayResponseDto> getAllHolidayById(@PathVariable("holidayid") int holidayId) {

		ResponseWrapper<HolidayResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.getHolidayById(holidayId));
		return responseWrapper;
	}

	/**
	 * This method returns a list of holidays containing a particular language code
	 * and holiday id
	 * 
	 * @param holidayId input parameter holiday id
	 * @param langCode  input parameter language code
	 * @return {@link HolidayResponseDto}
	 */
	@ResponseFilter
	@GetMapping("/{holidayid}/{langcode}")
	public ResponseWrapper<HolidayResponseDto> getAllHolidayByIdAndLangCode(@PathVariable("holidayid") int holidayId,
			@PathVariable("langcode") String langCode) {
		ResponseWrapper<HolidayResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.getHolidayByIdAndLanguageCode(holidayId, langCode));
		return responseWrapper;
	}

	/**
	 * This method creates a new row of holiday data
	 * 
	 * @param holiday input values to add a new row of data
	 * @return primary key of inserted Holiday data
	 */
	@ResponseFilter
	@PostMapping
	public ResponseWrapper<HolidayIDDto> saveHoliday(@Valid @RequestBody RequestWrapper<HolidayDto> holiday) {
		ResponseWrapper<HolidayIDDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.saveHoliday(holiday.getRequest()));
		return responseWrapper;
	}

	/**
	 * Method to update a holiday
	 * 
	 * @param holiday input values to update the data
	 * @return id of updated Holiday data
	 */
	@ResponseFilter
	@PutMapping
	@ApiOperation(value = "to update a holiday", response = HolidayIDDto.class)
	public ResponseWrapper<HolidayIDDto> updateHoliday(@Valid @RequestBody RequestWrapper<HolidayUpdateDto> holiday) {
		ResponseWrapper<HolidayIDDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.updateHoliday(holiday.getRequest()));
		return responseWrapper;
	}

	/**
	 * Method to delete holidays
	 * 
	 * @param request input values to delete
	 * @return id of the deleted Holiday data
	 */
	@ResponseFilter
	@DeleteMapping
	@ApiOperation(value = "to delete a holiday", response = HolidayIdDeleteDto.class)
	public ResponseWrapper<HolidayIdDeleteDto> deleteHoliday(
			@Valid @RequestBody RequestWrapper<HolidayIdDeleteDto> request) {
		ResponseWrapper<HolidayIdDeleteDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(holidayService.deleteHoliday(request));
		return responseWrapper;
	}

}
