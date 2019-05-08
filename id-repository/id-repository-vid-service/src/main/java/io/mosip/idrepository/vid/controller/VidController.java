package io.mosip.idrepository.vid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.core.util.DataValidationUtil;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.logger.spi.Logger;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author Prem Kumar
 *
 */
@RestController
public class VidController {

	private static final String RETRIEVE_UIN_BY_VID = "retrieveUinByVid";

	private static final String UPDATE_VID_STATUS = "updateVidStatus";

	private static final String VID_CONTROLLER = "VidController";

	@Autowired
	private VidService<VidRequestDTO, VidResponseDTO> vidService;

	@Autowired
	private VidRequestValidator vidRequestValidator;

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(VidController.class);

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(vidRequestValidator);
	}

	/**
	 * This method will accepts vid as parameter, if vid is valid it will return
	 * respective uin.
	 * 
	 * @param vid
	 * @return uin the uin
	 * @throws IdRepoAppException
	 */
	@GetMapping(path = "/vid/{VID}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VidResponseDTO> retrieveUinByVid(@PathVariable("VID") String vid) throws IdRepoAppException {
		try {
			IdRepoLogger.setVid(vid);
			vidRequestValidator.validateId(vid);
			return new ResponseEntity<>(vidService.retrieveUinByVid(vid), HttpStatus.OK);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "vid"));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

	/**
	 * This Method accepts VidRequest body as parameter and vid from url then it
	 * will update the status if it is an valid vid.
	 * 
	 * @param vid
	 * @param request
	 * @param errors
	 * @return VidResponseDTO 
	 * @throws IdRepoAppException
	 */
	@PatchMapping(path = "/vid/{VID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VidResponseDTO> updateVidStatus(@PathVariable("VID") String vid,
			@Validated @RequestBody VidRequestDTO request, @ApiIgnore Errors errors) throws IdRepoAppException {
		try {
			vidRequestValidator.validateId(vid);
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(vidService.updateVid(vid, request), HttpStatus.OK);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "vid"));
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}
}
