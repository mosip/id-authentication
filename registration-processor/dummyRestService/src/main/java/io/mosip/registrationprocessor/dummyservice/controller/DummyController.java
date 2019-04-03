package io.mosip.registrationprocessor.dummyservice.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registrationprocessor.dummyservice.entity.MessageRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Dummy controller
 *
 */
@RestController
@RequestMapping("/registration-processor")
@Api(tags = "Registration Dummy Service")
public class DummyController {
	/**
	 * dummy method to process incoming requests
	 * @param messageRequestDTO
	 * @return boolean
	 */
	@PostMapping(path = "/dummy/v1.0", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "DummyRequest Processed successfully") })
	public ResponseEntity<Boolean> dummyController(@RequestBody(required = true) MessageRequestDTO messageRequestDTO)  {
			
		ResponseEntity<Boolean> temp=ResponseEntity.ok().body(Boolean.FALSE);
			 messageRequestDTO.getRequest();
			if(messageRequestDTO.getRequest() !=null) {
				temp=ResponseEntity.ok().body(Boolean.TRUE);
			}
			
		return temp;
		
	}
}
