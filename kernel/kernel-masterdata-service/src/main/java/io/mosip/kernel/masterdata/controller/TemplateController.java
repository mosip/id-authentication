package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import io.mosip.kernel.masterdata.service.TemplateService;

/**
 * Controller APIs to get Template details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/templates")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	/**
	 * Method to fetch all Template details
	 * 
	 * @return All {@link TemplateDto}
	 */
	@GetMapping
	public List<TemplateDto> getAllTemplate() {
		return templateService.getAllTemplate();
	}

	/**
	 * API to fetch all Template details based on language code
	 * 
	 * @return All TemplateDto of specific language
	 */
	@GetMapping("/{languageCode}")
	public List<TemplateDto> getAllTemplateByLanguageCode(@PathVariable("languageCode") String languageCode) {
		return templateService.getAllTemplateByLanguageCode(languageCode);
	}

	/**
	 * API to fetch a Template details using templateTypeCode and language code
	 * 
	 * @return Template Details
	 */
	@GetMapping("/{languageCode}/{templateTypeCode}")
	public List<TemplateDto> getAllTemplateByLanguageCodeAndTemplateTypeCode(
			@PathVariable("languageCode") String languageCode,
			@PathVariable("templateTypeCode") String templateTypeCode) {
		return templateService.getAllTemplateByLanguageCodeAndTemplateTypeCode(languageCode, templateTypeCode);
	}
}
