package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.TitleResponseDto;
import io.mosip.kernel.masterdata.service.TitleService;

@RestController
public class TitleController {

	@Autowired
	private TitleService titleService;

	@GetMapping(value = "/title")
	public TitleResponseDto getAllTitles() {
		return titleService.getAllTitles();
	}

	@GetMapping(value = "/title/{languageCode}")
	public TitleResponseDto getTitlesByLanguageCode(@PathVariable String languageCode) {
		return titleService.getByLanguageCode(languageCode);
	}

}
