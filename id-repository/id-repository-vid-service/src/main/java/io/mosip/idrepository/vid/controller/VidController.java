package io.mosip.idrepository.vid.controller;

import javax.sql.DataSource;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.VidRequestDTO;
import io.mosip.idrepository.core.dto.VidResponseDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.core.util.DataValidationUtil;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.logger.spi.Logger;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class Vid Controller.
 *
 * @author Manoj SP
 * @author Prem Kumar
 */
@RestController
public class VidController {
	
	/** The data source. */
	@Autowired
	DataSource dataSource;
	
	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The Constant CREATE. */
	private static final String CREATE = "create";

	/**  The Constant RETRIEVE_UIN_BY_VID. */
	private static final String RETRIEVE_UIN_BY_VID = "retrieveUinByVid";

	/**  The Constant UPDATE_VID_STATUS. */
	private static final String UPDATE_VID_STATUS = "updateVidStatus";

	/**  The Constant VID_CONTROLLER. */
	private static final String VID_CONTROLLER = "VidController";
	
	/**  The Vid Service. */
	@Autowired
	private VidService<VidRequestDTO, ResponseWrapper<VidResponseDTO>> vidService;
	
	/**  The Vid Request Validator. */
	@Autowired
	private VidRequestValidator validator;

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(VidController.class);

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}
	
	/**
	 * Creates the vid.
	 *
	 * @param request the request
	 * @param errors the errors
	 * @return the response entity
	 * @throws IdRepoAppException the id repo app exception
	 */
	@PostMapping(path = "/vid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<VidResponseDTO>> createVid(
			@Validated @RequestBody RequestWrapper<VidRequestDTO> request, @ApiIgnore Errors errors)
			throws IdRepoAppException {
		try {
			
			IdRepoLogger.setUin(request.getRequest().getUin());
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(vidService.createVid(request.getRequest()), HttpStatus.OK);
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), VID_CONTROLLER, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

	/**
	 * This method will accepts vid as parameter, if vid is valid it will return
	 * respective uin.
	 *
	 * @param vid the vid
	 * @return uin the uin
	 * @throws IdRepoAppException the id repo app exception
	 */
	@GetMapping(path = "/vid/{VID}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<VidResponseDTO>> retrieveUinByVid(@PathVariable("VID") String vid)
			throws IdRepoAppException {
		try {
			IdRepoLogger.setVid(vid);
			validator.validateVid(vid);
			return new ResponseEntity<>(vidService.retrieveUinByVid(vid), HttpStatus.OK);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdRepoLogger.getVid(), VID_CONTROLLER, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid"));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getVid(), VID_CONTROLLER, RETRIEVE_UIN_BY_VID, e.getMessage());
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

	/**
	 * This Method accepts VidRequest body as parameter and vid from url then it
	 * will update the status if it is an valid vid.
	 *
	 * @param vid the vid
	 * @param request the request
	 * @param errors the errors
	 * @return VidResponseDTO
	 * @throws IdRepoAppException the id repo app exception
	 */
	@PatchMapping(path = "/vid/{VID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<VidResponseDTO>> updateVidStatus(@PathVariable("VID") String vid,
			@Validated @RequestBody RequestWrapper<VidRequestDTO> request, @ApiIgnore Errors errors)
			throws IdRepoAppException {
		try {
			IdRepoLogger.setVid(vid);
			validator.validateVid(vid);
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(vidService.updateVid(vid, request.getRequest()), HttpStatus.OK);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdRepoLogger.getVid(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid"));
		} catch (IdRepoDataValidationException e) {
			mosipLogger.error(IdRepoLogger.getVid(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getVid(), VID_CONTROLLER, UPDATE_VID_STATUS, e.getMessage());
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}
}
