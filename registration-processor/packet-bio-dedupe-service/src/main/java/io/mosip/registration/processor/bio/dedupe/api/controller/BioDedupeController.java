/**
 * 
 */
package io.mosip.registration.processor.bio.dedupe.api.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.bio.dedupe.exception.BioDedupeAppException;
import io.mosip.registration.processor.bio.dedupe.exception.BioDedupeValidationException;
import io.mosip.registration.processor.bio.dedupe.request.validator.BioDedupeRequestValidator;
import io.mosip.registration.processor.bio.dedupe.request.validator.BioDedupeValidationUtil;
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeRequestDTO;
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeResponseDTO;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class BioDedupeController.
 *
 * @author M1022006
 */
@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/bio-dedupe")
@Api(tags = "Biodedupe")
public class BioDedupeController {

	/** The bio dedupe service. */
	@Autowired
	private BioDedupeService bioDedupeService;

	/** The validator. */
	@Autowired
	private BioDedupeRequestValidator validator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	private static final String BIO_DEDUPE_SERVICE_ID = "mosip.packet.bio.dedupe";
	private static final String BIO_DEDUPE_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


	/**
	 * Gets the file.
	 *
	 * @param regId
	 *            the reg id
	 * @return the file
	 * @throws BioDedupeAppException 
	 */
	@PostMapping(path = "/regId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the CBEF XML file  of packet", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "CBEF Xml file is successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the CBEF XML file"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<BioDedupeResponseDTO> getFile(@Validated @RequestBody(required = true) BioDedupeRequestDTO bioDedupeRequestDTO,@ApiIgnore Errors errors) throws BioDedupeAppException {
		try {
			BioDedupeValidationUtil.validate(errors);
			byte[] file = bioDedupeService.getFile(bioDedupeRequestDTO.getRequest().getRegId());
			String byteAsString=new String(file);
			return ResponseEntity.status(HttpStatus.OK).body(buildBioDedupeResponse(byteAsString));
		}catch(BioDedupeValidationException e) {
			throw new BioDedupeAppException(PlatformErrorMessages.RPR_BDD_DATA_VALIDATION_FAILED, e);
		}
	}


	public BioDedupeResponseDTO buildBioDedupeResponse(String byteAsString) {

		BioDedupeResponseDTO response = new BioDedupeResponseDTO();
		if (Objects.isNull(response.getId())) {
			response.setId(BIO_DEDUPE_SERVICE_ID);
		}
		response.setError(null);
		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(BIO_DEDUPE_APPLICATION_VERSION);
		response.setFile(byteAsString);
		response.setError(null);
		return response;
	}
}
