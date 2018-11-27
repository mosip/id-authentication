package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.HolidayResponseDto;
import io.mosip.kernel.masterdata.service.HolidayService;

@RestController
@RequestMapping("/holidays")
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
}
