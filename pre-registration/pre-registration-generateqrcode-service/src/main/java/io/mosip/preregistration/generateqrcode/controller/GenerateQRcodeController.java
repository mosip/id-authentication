package io.mosip.preregistration.generateqrcode.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.DataValidationUtil;
import io.mosip.preregistration.core.util.RequestValidator;
import io.mosip.preregistration.generateqrcode.dto.QRCodeResponseDTO;
import io.mosip.preregistration.generateqrcode.service.GenerateQRcodeService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This class provides  API's to generate the QR code operations on
 * pre-registration.
 * @author Sanober Noor
 *@since 1.0.0
 */
@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class GenerateQRcodeController {

	private Logger log = LoggerConfiguration.logConfig(GenerateQRcodeController.class);
	
	@Autowired
	private RequestValidator requestValidator;
	
	/** The Constant for GET UPDATED DATE TIME application. */
	private static final String QRCODE = "generate";
	
	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(requestValidator);
	}
	
	@Autowired
	private GenerateQRcodeService service;
	
	/**
	 * @param Json Stirng data
	 * @return the response entity
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@PostMapping(path="/generate" ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MainResponseDTO<QRCodeResponseDTO>> generateQRCode(@Validated @RequestBody MainRequestDTO<String> data,@ApiIgnore Errors errors) {
		log.info("sessionId", "idType", "id",
				"In generateQRCode controller for generateQRCode generation with request " + data);
		requestValidator.validateId(QRCODE, data.getId(), errors);
		DataValidationUtil.validate(errors,QRCODE);
		return  new ResponseEntity<>(service.generateQRCode(data),HttpStatus.OK);
		
	}
}
