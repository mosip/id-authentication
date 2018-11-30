package io.mosip.registration.processor.manual.adjudication.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/manual-adjudication")
@Api(tags = "Manual Adjudication")
public class ManualAdjudicationControllerStart {
	@Autowired
	private ManualAdjudicationService manualAdjudicationService;
	@PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "status successfully updated") })
	public ResponseEntity<UserDto> updateStatusController(@RequestBody(required = true) UserDto userDto) {
		
		UserDto t = manualAdjudicationService.assignStatus(userDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(t);
	
}
}
