package io.mosip.preregistration.generateqrcode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.generateqrcode.dto.QRCodeResponseDTO;
import io.mosip.preregistration.generateqrcode.service.GenerateQRcodeService;

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
	private GenerateQRcodeService service;
	
	/**
	 * @param Json Stirng data
	 * @return the response entity
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@PostMapping(path="/generate")
	public ResponseEntity<MainResponseDTO<QRCodeResponseDTO>> generateQRCode(@RequestBody String data) {
		log.info("sessionId", "idType", "id",
				"In generateQRCode controller for generateQRCode generation with request " + data);
		return  new ResponseEntity<>(service.generateQRCode(data),HttpStatus.OK);
		
	}
}
