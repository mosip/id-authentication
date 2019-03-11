package io.mosip.registration.processor.packet.service.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.packet.service.PacketGeneratorService;
import io.mosip.registration.processor.packet.service.dto.PackerGeneratorRequestDto;
import io.mosip.registration.processor.packet.service.dto.PackerGeneratorResDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/packet-generator")
@Api(tags = "PacketGenerator")
public class PacketGeneratorController {

	@Autowired
	private PacketGeneratorService packetGeneratorService;

	@Autowired
	private Environment env;

	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	@PostMapping(path = "/generatePacketAndUpload", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the status of packet", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get the status of packet "),
			@ApiResponse(code = 400, message = "Unable to fetch the status "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<Object> getStatus(
			@Validated @RequestBody(required = true) PackerGeneratorRequestDto packerGeneratorRequestDto,
			@ApiIgnore Errors errors) {
		PackerGeneratorResDto packerGeneratorResDto = packetGeneratorService
				.createPacket(packerGeneratorRequestDto.getRequest());
		return ResponseEntity.ok().body(buildPacketGeneratorResponse(packerGeneratorResDto));

	}

	public String buildPacketGeneratorResponse(PackerGeneratorResDto packerGeneratorResDto) {

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
