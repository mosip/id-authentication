package io.mosip.registrationprocessor.eis.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registrationprocessor.eis.entity.MessageRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

/**
 * External Controller
 *
 */
@RestController
@RequestMapping("/registration-processor")
@Api(tags = "external-integration-service")
public class ExternalController {
	/**
	 * dummy method to process incoming requests
	 * @param messageRequestDTO
	 * @return boolean
	 */
	@PostMapping(path = "/external-integration-service/v1.0", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "external request Processed successfully") })
	public ResponseEntity<Boolean> eisController(@RequestBody(required = true) MessageRequestDTO messageRequestDTO)  {
			
		ResponseEntity<Boolean> temp=ResponseEntity.ok().body(Boolean.FALSE);
			 messageRequestDTO.getRequest();
			if(messageRequestDTO.getRequest() !=null) {
				temp=ResponseEntity.ok().body(Boolean.TRUE);
			}
			
		return temp;
		
	}
}
