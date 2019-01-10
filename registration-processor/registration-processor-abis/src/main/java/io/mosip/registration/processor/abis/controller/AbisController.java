package io.mosip.registration.processor.abis.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.abis.dto.IdentityRequestDto;
import io.mosip.registration.processor.abis.dto.IdentityResponceDto;
import io.mosip.registration.processor.abis.service.impl.AbisServiceImpl;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/abis")
@Api(tags = "Abis")
public class AbisController {

	@Autowired
	private AbisServiceImpl abisServiceImpl;
	
	@PostMapping(path = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "insert biometric data of an Individual", response = AbisInsertResponceDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Biometric data inserted successfully"),
			@ApiResponse(code = 400, message = "Uable to insert biometric data") })
	public ResponseEntity<AbisInsertResponceDto> insert(@RequestBody(required = true) AbisInsertRequestDto abisInsertRequestDto) {
		
		AbisInsertResponceDto abisInsertResponceDto = null;
		
		try {
			abisInsertResponceDto = abisServiceImpl.insert(abisInsertRequestDto);
			
		} catch (ApisResourceAccessException | ClassNotFoundException | IOException | ParserConfigurationException
				| SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(abisInsertRequestDto.getId().equals("insert")) {
			return ResponseEntity.status(HttpStatus.OK).body(abisInsertResponceDto);
		}
		abisInsertResponceDto.setFailureReason(2);
		abisInsertResponceDto.setReturnValue(2);
		 return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(abisInsertResponceDto);
		
	}
	
	
	@PostMapping(path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "identify duplicate biometric data of an Individual", response = IdentityResponceDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "unique biometric data"),
			@ApiResponse(code = 400, message = "duplicate biometric data") })
	public ResponseEntity<IdentityResponceDto> identity(@RequestBody(required = true) IdentityRequestDto identityRequestDto) {
		IdentityResponceDto identityResponceDto = null;
		try {
			identityResponceDto = abisServiceImpl.performDedupe(identityRequestDto);
		} catch (ApisResourceAccessException | ClassNotFoundException | IOException | ParserConfigurationException
				| SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//IdentityResponceDto identityResponceDto = abisServiceImpl.deDupeCheck(identityRequestDto);

		if (identityRequestDto.getId().equals("identify")) {
			return ResponseEntity.status(HttpStatus.OK).body(identityResponceDto);
		}
		identityResponceDto.setCandidateList(null);
		identityResponceDto.setReturnValue(2);
		identityResponceDto.setFailureReason(1);
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(identityResponceDto);
	}
}
