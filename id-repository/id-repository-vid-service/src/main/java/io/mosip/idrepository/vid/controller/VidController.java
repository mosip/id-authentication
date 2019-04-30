package io.mosip.idrepository.vid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.core.util.DataValidationUtil;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import springfox.documentation.annotations.ApiIgnore;

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
	private VidValidator<String> vidValidator;
	
	@Autowired
	private VidRequestValidator vidRequestValidator;

	@GetMapping(path = "/vid/{VID}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VidResponseDTO> retrieveUinByVid(@PathVariable String vid, @ApiIgnore Errors errors)
			throws IdRepoAppException {
		try {
			vidValidator.validateId(vid);
			vidRequestValidator.validateId(vid,errors);
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(vidService.retrieveUinByVid(vid), HttpStatus.OK);
		} catch (InvalidIDException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid"));
		} catch (IdRepoDataValidationException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

}
