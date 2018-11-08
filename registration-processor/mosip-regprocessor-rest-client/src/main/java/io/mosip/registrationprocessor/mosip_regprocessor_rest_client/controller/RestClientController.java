package io.mosip.registrationprocessor.mosip_regprocessor_rest_client.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/rest-client")
@Api(tags = "Rest-Client")
public class RestClientController {

	/*@Autowired
	private PacketReceiverService<MultipartFile, Boolean> packetHandlerService;*/
	
	@GetMapping(path = "/", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "", response = RegistrationStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Officer and Supervisor validated"),
	@ApiResponse(code = 400, message = "Packet already present in landing zone") })
	public ResponseEntity<RegistrationStatusCode> packet(
			@RequestParam(value = "file", required = true) MultipartFile file) {
		//if
		/*if (packetHandlerService.storePacket(file)) {
			return ResponseEntity.ok().body(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE);
		} else {
			return ResponseEntity.badRequest().body(RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED);
		}*/
		return null;
	}
	
	
	
}
