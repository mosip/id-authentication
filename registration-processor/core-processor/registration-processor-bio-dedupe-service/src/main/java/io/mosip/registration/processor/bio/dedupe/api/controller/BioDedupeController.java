package io.mosip.registration.processor.bio.dedupe.api.controller;

import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class BioDedupeController.
 *
 * @author M1022006
 */
@RefreshScope
@RestController
@Api(tags = "Biodedupe")
public class BioDedupeController {

	/** The bio dedupe service. */
	@Autowired
	private BioDedupeService bioDedupeService;

	/** Token validator class */
	@Autowired
	private TokenValidator tokenValidator;

	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;

	@Autowired
	private DigitalSignatureUtility digitalSignatureUtility;

	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	/**
	 * Gets the file.
	 *
	 * @param regId
	 *            the reg id
	 * @return the file
	 */

	@GetMapping(path = "/biometricfile/{regId}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiOperation(value = "Get the CBEF XML file  of packet", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "CBEF Xml file is successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the CBEF XML file"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getFile(@PathVariable("regId") String regId,
			@CookieValue(value = "Authorization", required = true) String token) {

		tokenValidator.validate("Authorization=" + token, "biodedupe");
		byte[] file = bioDedupeService.getFile(regId,false);

		if (isEnabled) {
			HttpHeaders headers = new HttpHeaders();
			if(file != null) {
				headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility.getDigitalSignature(new String(file)));
			}
			return ResponseEntity.ok().headers(headers).body(file);
		}

		return ResponseEntity.status(HttpStatus.OK).body(file);

	}
	
	@GetMapping(path = "/biometricfile/abis/{abisRefId}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@ApiOperation(value = "Get the CBEF XML file  of packet using abis ref Id", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "CBEF Xml file is successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the CBEF XML file"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<byte[]> getAbisBiometricFile(@PathVariable("abisRefId") String abisRefId,
			@CookieValue(value = "Authorization", required = true) String token) {

		tokenValidator.validate("Authorization=" + token, "biodedupe");
		byte[] file = bioDedupeService.getFile(abisRefId,true);

		if (isEnabled) {
			HttpHeaders headers = new HttpHeaders();
			if(file != null) {
				headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility.getDigitalSignature(new String(file)));
			}
			return ResponseEntity.ok().headers(headers).body(file);
		}

		return ResponseEntity.status(HttpStatus.OK).body(file);

	}
}