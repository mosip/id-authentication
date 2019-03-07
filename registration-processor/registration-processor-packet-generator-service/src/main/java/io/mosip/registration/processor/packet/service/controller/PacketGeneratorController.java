package io.mosip.registration.processor.packet.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.packet.service.PacketGeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/packet-generator")
@Api(tags = "PacketGenerator")
public class PacketGeneratorController {

	@Autowired
	private PacketGeneratorService packetGeneratorService;

	@GetMapping(path = "/{uin}/{registrationType}/{applicantType}/{reason}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the status of packet", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Get the status of packet "),
			@ApiResponse(code = 400, message = "Unable to fetch the status "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<String> getStatus(@PathVariable("uin") String uin,
			@PathVariable("registrationType") String registrationType,
			@PathVariable("applicantType") String applicantType, @PathVariable("reason") String reason) {
		packetGeneratorService.createPacket(uin, registrationType, applicantType, reason);
		return ResponseEntity.status(HttpStatus.OK).body("");

	}
}
