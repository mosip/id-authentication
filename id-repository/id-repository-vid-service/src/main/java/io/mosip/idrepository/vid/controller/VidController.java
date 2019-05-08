package io.mosip.idrepository.vid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;

/**
 * 
 * @author Prem Kumar
 *
 */
@RestController
public class VidController {

	@Autowired
	private VidService<Object, VidResponseDTO> vidService;

	@Autowired
	private VidRequestValidator vidRequestValidator;

	@GetMapping(path = "/vid/{VID}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VidResponseDTO> retrieveUinByVid(@PathVariable("VID") String vid) throws IdRepoAppException {
		try {
			vidRequestValidator.validateId(vid);
			return new ResponseEntity<>(vidService.retrieveUinByVid(vid), HttpStatus.OK);
		} catch (InvalidIDException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid"));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

}
