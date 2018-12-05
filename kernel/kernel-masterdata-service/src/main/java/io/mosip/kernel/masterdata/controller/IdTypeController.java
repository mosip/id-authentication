package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.IdTypeService;

/**
 * This controller class provides id types master data operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
public class IdTypeController {
	/**
	 * Autowire reference to IdService.
	 */
	@Autowired
	IdTypeService idService;

	/**
	 * This method returns the list of id types present for a specific language
	 * code.
	 * 
	 * @param langCode
	 *            the language code against which id types are to be fetched.
	 * @return the list of id types.
	 */
	@GetMapping("/v1.0/idtypes/{langcode}")
	public IdTypeResponseDto getIdTypesByLanguageCode(@Valid @PathVariable("langcode") String langCode) {
		return idService.getIdTypesByLanguageCode(langCode);
	}

	/**
	 * This method creates id types.
	 * 
	 * @param idTypeRequestDto
	 *            the request of idtype to be added.
	 * @return the response.
	 */
	@PostMapping("/v1.0/idtypes")
	public ResponseEntity<CodeAndLanguageCodeID> createIdType(
			@Valid @RequestBody RequestDto<IdTypeDto> idTypeRequestDto) {
		return new ResponseEntity<>(idService.createIdType(idTypeRequestDto), HttpStatus.CREATED);
	}
}
