package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.TitleResponseDto;
import io.mosip.kernel.masterdata.service.TitleService;

/**
 * Controller class for fetching titles from master data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
public class TitleController {

	@Autowired
	private TitleService titleService;

	/**
	 * Method to return list of all titles
	 * 
	 * @return list of all titles present in master DB
	 */
	@GetMapping(value = "/title")
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
	@GetMapping(value = "/title/{langcode}")
	public TitleResponseDto getTitlesBylangCode(@PathVariable String langCode) {
		return titleService.getByLanguageCode(langCode);
	}

}
