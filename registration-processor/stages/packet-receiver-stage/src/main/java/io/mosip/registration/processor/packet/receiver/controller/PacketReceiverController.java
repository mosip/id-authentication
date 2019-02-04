package io.mosip.registration.processor.packet.receiver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.mosip.registration.processor.core.auth.dto.RegistrationProcessorSuccessResponse;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
	
/**
 * The Class PacketReceiverController.
 */
@RestController
@RequestMapping("/v0.1/registration-processor/packet-receiver")
@Api(tags = "Packet Handler")
public class PacketReceiverController {

	/** The packet handler service. */
	@Autowired
	private PacketReceiverService<MultipartFile, Boolean> packetHandlerService;
	
	@Autowired
	private RegistrationProcessorSuccessResponse registrationProcessorSuccessResponse;

	/**
	 * Packet.
	 *
	 * @param file
	 *            the file
	 * @return the response entity
	 */
	@PostMapping(path = "/registrationpackets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Upload a packet to landing zone", response = RegistrationStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Packet successfully uploaded to landing zone"),
			@ApiResponse(code = 400, message = "Packet already present in landing zone") })
	public ResponseEntity<RegistrationProcessorSuccessResponse> packet(
			@RequestParam(value = "file", required = true) MultipartFile file) {

		if (packetHandlerService.storePacket(file)) {
			registrationProcessorSuccessResponse.setStatus(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
			return ResponseEntity.ok().body(registrationProcessorSuccessResponse);
		} else {
			registrationProcessorSuccessResponse.setStatus(RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED.toString());
			return ResponseEntity.badRequest().body(registrationProcessorSuccessResponse);
		}
	}

}
