package io.mosip.registration.processor.abis.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/abis")
@Api(tags = "Abis")
public class AbisController {

	@PostMapping(path = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "insert biometric data of an Individual", response = AbisInsertResponceDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Biometric data inserted successfully"),
			@ApiResponse(code = 400, message = "Uable to insert biometric data") })
	public ResponseEntity<AbisInsertResponceDto> insert(@RequestBody(required = true) AbisInsertRequestDto abisInsertRequestDto) {
		
		return null;
		
	}
	
}
