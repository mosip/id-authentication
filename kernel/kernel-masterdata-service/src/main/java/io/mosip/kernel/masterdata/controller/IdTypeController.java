package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.IdTypeRequestDto;
import io.mosip.kernel.masterdata.dto.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
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
	 * Autowired reference to IdService.
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
	@GetMapping("/idtypes/{langCode}")
	public IdTypeResponseDto getIdTypeDetailsBylangCode(@PathVariable("langCode") String langCode) {
		return idService.getIdTypeByLanguageCode(langCode);
	}

	/**
	 * This method adds a list of id types.
	 * 
	 * @param idTypeRequestDto
	 *            the request of list of id types to be added.
	 * @return the list of added id types as response.
	 */
	@PostMapping("/idtypes")
	public ResponseEntity<PostResponseDto> addIdType(@RequestBody IdTypeRequestDto idTypeRequestDto) {
		return new ResponseEntity<>(idService.addIdType(idTypeRequestDto), HttpStatus.CREATED);
	}
}
