package io.mosip.registration.processor.packet.storage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/packet-info-storage-service")
@Api(tags = "Reg Packet Info")
public class PacketInfoController {

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@GetMapping(path = "/getexceptiondata", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the exception entity", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Assigned packets fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch the Exception Data") })
	public ResponseEntity<List<ApplicantInfoDto>> getPacketsforQCUser(
			@RequestParam(value = "qcuserId", required = true) String qcuserId) {
		List<ApplicantInfoDto> packets = packetInfoManager.getPacketsforQCUser(qcuserId);
		return ResponseEntity.status(HttpStatus.OK).body(packets);
	}

}