package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.HolidayResponseDto;
import io.mosip.kernel.masterdata.entity.id.HolidayID;
import io.mosip.kernel.masterdata.service.HolidayService;

@RestController
@RequestMapping("/v1.0/holidays")
public class HolidayController {

	@Autowired
	private HolidayService holidayService;

	@GetMapping
	public HolidayResponseDto getAllHolidays() {
		return holidayService.getAllHolidays();
	}

	@GetMapping("/{holidayid}")
	public HolidayResponseDto getAllHolidayById(@PathVariable("holidayid") int holidayId) {
		return holidayService.getHolidayById(holidayId);
	}

	@GetMapping("/{holidayid}/{langcode}")
	public HolidayResponseDto getAllHolidayByIdAndLangCode(@PathVariable("holidayid") int holidayId,
			@PathVariable("langcode") String langCode) {
		return holidayService.getHolidayByIdAndLanguageCode(holidayId, langCode);
	}
	@PostMapping
	public ResponseEntity<HolidayID> saveHoliday(
			@Valid @RequestBody RequestDto<HolidayDto> holiday) {
		return new ResponseEntity<>(holidayService.saveHoliday(holiday), HttpStatus.CREATED);

	}
}
