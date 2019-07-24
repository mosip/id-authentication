package io.mosip.registration.processor.request.handler.service.controller;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
import io.mosip.registration.processor.request.handler.service.PacketGeneratorService;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RequestHandlerValidationException;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class PacketGeneratorController.
 * 
 * @author Sowmya
 */
@RefreshScope
@RestController
@Api(tags = "Packet Generator")
public class PacketGeneratorController {

	/** The packet generator service. */
	@Autowired
	private PacketGeneratorService packetGeneratorService;

	/** The env. */
	@Autowired
	private Environment env;

	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;

	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.packetgenerator.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;

	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;

	/**
	 * Gets the status.
	 *
	 * @param packerGeneratorRequestDto
	 *            the packer generator request dto
	 * @param errors
	 *            the errors
	 * @return the status
	 * @throws RegBaseCheckedException
	 * @throws IOException
	 */
	@PostMapping(path = "/registrationpacket", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the status of packet", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get the status of packet "),
			@ApiResponse(code = 400, message = "Unable to fetch the status "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<Object> getStatus(
			@RequestBody(required = true) PacketGeneratorRequestDto packerGeneratorRequestDto,
			@CookieValue(value = "Authorization", required = true) String token)
			throws RegBaseCheckedException, IOException {
		tokenValidator.validate("Authorization=" + token, "packetgenerator");

		try {
			validator.validate(packerGeneratorRequestDto.getRequesttime(), packerGeneratorRequestDto.getId(),
					packerGeneratorRequestDto.getVersion());
			PacketGeneratorResDto packerGeneratorResDto;
			packerGeneratorResDto = packetGeneratorService.createPacket(packerGeneratorRequestDto.getRequest());

			if (isEnabled) {
				HttpHeaders headers = new HttpHeaders();
				headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility
						.getDigitalSignature(buildSignaturePacketGeneratorResponse(packerGeneratorResDto)));
				return ResponseEntity.ok().headers(headers).body(buildPacketGeneratorResponse(packerGeneratorResDto));
			}
			return ResponseEntity.ok().body(buildPacketGeneratorResponse(packerGeneratorResDto));
		} catch (RequestHandlerValidationException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_RGS_DATA_VALIDATION_FAILED, e);

		}
	}

	/**
	 * Builds the packet generator response.
	 *
	 * @param packerGeneratorResDto
	 *            the packer generator res dto
	 * @return the string
	 */
	public PacketGeneratorResponseDto buildPacketGeneratorResponse(PacketGeneratorResDto packerGeneratorResDto) {

		PacketGeneratorResponseDto response = new PacketGeneratorResponseDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_PACKET_GENERATOR_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(packerGeneratorResDto);
		response.setErrors(null);
		return response;
	}
	
	public String buildSignaturePacketGeneratorResponse(PacketGeneratorResDto packerGeneratorResDto) {

		PacketGeneratorResponseDto response = new PacketGeneratorResponseDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_PACKET_GENERATOR_SERVICE_ID));
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(packerGeneratorResDto);
		response.setErrors(null);
		Gson gson = new GsonBuilder().create();
		return gson.toJson(response);
	}
}