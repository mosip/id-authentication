package io.mosip.registration.processor.request.handler.service.controller;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.request.handler.service.PacketGeneratorService;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.request.handler.service.dto.PacketGeneratorResponseDto;
import io.mosip.registration.processor.request.handler.service.dto.ResidentUpdateDto;
import io.mosip.registration.processor.request.handler.service.dto.ResidentUpdateRequestDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.swagger.annotations.Api;

@RestController
@Api(tags = "Resident update service")
public class ResidentServicesController {
	@Autowired
	TokenValidator tokenValidator;
	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;
	@Autowired
	@Qualifier("residentUpdateService")
	private PacketGeneratorService<ResidentUpdateDto> residentUpdateServiceImpl;
	@Autowired
	private RequestHandlerRequestValidator validator;
	@Autowired
	private Environment env;
	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;
	private static final String RESPONSE_SIGNATURE = "Response-Signature";
	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.packetgenerator.version";

	@PostMapping("/resUpdate")
	public ResponseEntity<Object> updateResidentUINData(
			@RequestBody(required = true) ResidentUpdateRequestDto residentUpdateRequestDto,
			@CookieValue(value = "Authorization", required = true) String token)
			throws RegBaseCheckedException, IOException {
		tokenValidator.validate("Authorization=" + token, "residentUpdateService");
		PacketGeneratorResDto packetGeneratorResDto;

		validator.validate(residentUpdateRequestDto.getRequesttime(), residentUpdateRequestDto.getId(),
				residentUpdateRequestDto.getVersion());
		packetGeneratorResDto = residentUpdateServiceImpl.createPacket(residentUpdateRequestDto.getRequest());
		if (isEnabled) {
			PacketGeneratorResponseDto response = buildPacketGeneratorResponse(packetGeneratorResDto);
			Gson gson = new GsonBuilder().serializeNulls().create();
			HttpHeaders headers = new HttpHeaders();
			headers.add(RESPONSE_SIGNATURE, digitalSignatureUtility.getDigitalSignature(gson.toJson(response)));
			return ResponseEntity.ok().headers(headers).body(response);
		}
		return ResponseEntity.ok().body(buildPacketGeneratorResponse(packetGeneratorResDto));

	}

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

}
