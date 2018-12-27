package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.HolidayIDDto;
import io.mosip.kernel.masterdata.dto.HolidayIdDeleteDto;
import io.mosip.kernel.masterdata.dto.HolidayUpdateDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
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
@RequestMapping("/v1.0/holidays")
public class HolidayController {

	@Autowired
	private HolidayService holidayService;

	/**
	 * This method returns all holidays present in master db
	 * 
	 * @return list of all holidays
	 */
	@GetMapping
	public HolidayResponseDto getAllHolidays() {
		return holidayService.getAllHolidays();
	}

	/**
	 * This method returns list of holidays for a particular holiday id
	 * 
	 * @param holidayId
	 *            input parameter holiday id
	 * @return list of holidays for a particular holiday id
	 */
	@GetMapping("/{holidayid}")
	public HolidayResponseDto getAllHolidayById(@PathVariable("holidayid") int holidayId) {
		return holidayService.getHolidayById(holidayId);
	}

	/**
	 * This method returns a list of holidays containing a particular language code
	 * and holiday id
	 * 
	 * @param holidayId
	 *            input parameter holiday id
	 * @param langCode
	 *            input parameter language code
	 * @return {@link HolidayResponseDto}
	 */
	@GetMapping("/{holidayid}/{langcode}")
	public HolidayResponseDto getAllHolidayByIdAndLangCode(@PathVariable("holidayid") int holidayId,
			@PathVariable("langcode") String langCode) {
		return holidayService.getHolidayByIdAndLanguageCode(holidayId, langCode);
	}

	/**
	 * This method creates a new row of holiday data
	 * 
	 * @param holiday
	 *            input values to add a new row of data
	 * @return primary key of inserted Holiday data
	 */
	@PostMapping
	public ResponseEntity<HolidayIDDto> saveHoliday(@Valid @RequestBody RequestDto<HolidayDto> holiday) {
		return new ResponseEntity<>(holidayService.saveHoliday(holiday), HttpStatus.CREATED);

	}

	/**
	 * Method to update a holiday
	 * 
	 * @param holiday
	 *            input values to update the data
	 * @return id of updated Holiday data
	 */
	@PutMapping
	@ApiOperation(value = "to update a holiday", response = HolidayIDDto.class)
	public HolidayIDDto updateHoliday(@Valid @RequestBody RequestDto<HolidayUpdateDto> holiday) {
		return holidayService.updateHoliday(holiday);
	}

	/**
	 * Method to delete holidays
	 * 
	 * @param request
	 *            input values to delete 
	 * @return id of the deleted Holiday data
	 */
	@DeleteMapping
	@ApiOperation(value = "to delete a holiday", response = HolidayIdDeleteDto.class)
	public HolidayIdDeleteDto deleteHoliday(@Valid @RequestBody RequestDto<HolidayIdDeleteDto> request) {
		return holidayService.deleteHoliday(request);
	}

}
