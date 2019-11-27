/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.controller;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.request.handler.service.LostPacketService;
import io.mosip.registration.processor.request.handler.service.dto.LostPacketRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.LostPacketResponseDto;
import io.mosip.registration.processor.request.handler.service.dto.LostResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.service.exception.RequestHandlerValidationException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Sowmya
 *
 */
@RestController
@Api(tags = "Lost")
public class LostPacketController {
	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;

	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	/** The Constant REG_UINCARD_REPRINT_SERVICE_ID. */
	private static final String REG_UINCARD_REPRINT_SERVICE_ID = "mosip.registration.processor.uincard.reprint.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.packetgenerator.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The Constant REG_UINCARD_REPRINT_SERVICE_ID. */
	private static final String REG_LOST_PACKET_SERVICE_ID = "mosip.registration.processor.lost.id";

	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;

	/** The packet generator service. */
	@Autowired
	private LostPacketService lostPacketService;

	/** The env. */
	@Autowired
	private Environment env;

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Lost UIN or RID Api"),
			@ApiResponse(code = 400, message = "Unable to fetch the detail "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PostMapping(path = "/lost", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getIdValue(@RequestBody(required = true) LostPacketRequestDto lostPacketRequestDto,
			@CookieValue(value = "Authorization", required = true) String token)
			throws RegBaseCheckedException, IOException {
		tokenValidator.validate("Authorization=" + token, "requesthandler");
		try {
			validator.validate(lostPacketRequestDto.getRequesttime(), lostPacketRequestDto.getId(),
					lostPacketRequestDto.getVersion());
			LostResponseDto lostResponseDto = lostPacketService.getIdValue(lostPacketRequestDto.getRequest());
			if (isEnabled) {
				LostPacketResponseDto response = buildLostPacketResponse(lostResponseDto);
				Gson gson = new GsonBuilder().serializeNulls().create();
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility.getDigitalSignature(gson.toJson(response)));
				return ResponseEntity.ok().headers(headers).body(response);
			}
			return ResponseEntity.ok().body(buildLostPacketResponse(lostResponseDto));
		} catch (RequestHandlerValidationException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	public LostPacketResponseDto buildLostPacketResponse(LostResponseDto lostResponseDto) {

		LostPacketResponseDto response = new LostPacketResponseDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_LOST_PACKET_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(lostResponseDto);
		response.setErrors(null);
		return response;
	}
}
