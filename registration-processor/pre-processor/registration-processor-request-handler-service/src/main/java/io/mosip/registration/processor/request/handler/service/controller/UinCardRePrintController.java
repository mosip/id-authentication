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
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResponseDto;
import io.mosip.registration.processor.request.handler.service.dto.UinCardRePrintRequestDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.service.exception.RequestHandlerValidationException;
import io.mosip.registration.processor.request.handler.service.impl.UinCardRePrintServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Ranjitha
 *
 */
@RestController
@Api(tags = "Uin Card RePrint")
public class UinCardRePrintController {
	
	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;
	
	@Autowired
	private UinCardRePrintServiceImpl uinCardRePrintServiceImpl;
	
	private static final String RESPONSE_SIGNATURE = "Response-Signature";
	
	/** The Constant REG_UINCARD_REPRINT_SERVICE_ID. */
	private static final String REG_UINCARD_REPRINT_SERVICE_ID = "mosip.registration.processor.uincard.reprint.id";
	
	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.packetgenerator.version";
	
	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	
	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	@ApiOperation(value = "Uin Card Re-Print Api", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Uin Card Re-Print Api"),
			@ApiResponse(code = 400, message = "Unable to fetch the status "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PostMapping(path = "/reprint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getStatus(
			@RequestBody(required = true) UinCardRePrintRequestDto uinCardRePrintRequestDto,
			@CookieValue(value = "Authorization", required = true) String token)
					throws RegBaseCheckedException, IOException {
		tokenValidator.validate("Authorization=" + token, "requesthandler");
		try {	
			PacketGeneratorResDto packetGeneratorResDto = new PacketGeneratorResDto();
			packetGeneratorResDto = uinCardRePrintServiceImpl.createPacket(uinCardRePrintRequestDto);
			
			if (isEnabled) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility
						.getDigitalSignature(buildSignaturePacketGeneratorResponse(packetGeneratorResDto)));
				return ResponseEntity.ok().headers(headers).body(buildPacketGeneratorResponse(packetGeneratorResDto));
			}
			
			return ResponseEntity.ok().body(buildPacketGeneratorResponse(packetGeneratorResDto));	
		}catch(RequestHandlerValidationException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);
		}		
	}
	
	public PacketGeneratorResponseDto buildPacketGeneratorResponse(PacketGeneratorResDto packetGeneratorResDto) {

		PacketGeneratorResponseDto response = new PacketGeneratorResponseDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_UINCARD_REPRINT_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(packetGeneratorResDto);
		response.setErrors(null);
		return response;
	}
	
	public String buildSignaturePacketGeneratorResponse(PacketGeneratorResDto packetGeneratorResDto) {

		PacketGeneratorResponseDto response = new PacketGeneratorResponseDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_UINCARD_REPRINT_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(packetGeneratorResDto);
		response.setErrors(null);
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(response);
	}
}
