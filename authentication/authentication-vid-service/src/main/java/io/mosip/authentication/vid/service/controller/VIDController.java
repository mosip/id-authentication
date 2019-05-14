package io.mosip.authentication.vid.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.vid.service.integration.VIDManager;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class VIDController,it is an REST Api to generate the VID.
 * 
 * @author Arun Bose S
 */
@RestController
public class VIDController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(VIDController.class);

	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;

	/** The Static Pin Facade */
	@Autowired
	private VIDManager vidManager;

	/**
	 * Generate VID.
	 *
	 * @param uin the uin
	 * @return the VID response DTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@GetMapping(path = "/{uin}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "VID Generation Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "VID generated successfully") })
	public VIDResponseDTO generateVID(@PathVariable String uin) throws IdAuthenticationAppException {
		VIDResponseDTO vidResponse = null;
		try {
			uinValidator.validateId(uin);
			vidResponse = vidManager.getVIDByUIN(uin);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateVID",
					e.getErrorTexts() == null || e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getMessage(), e);
		} catch (InvalidIDException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateVID",
					e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_UIN);
		}
		return vidResponse;

	}

}
