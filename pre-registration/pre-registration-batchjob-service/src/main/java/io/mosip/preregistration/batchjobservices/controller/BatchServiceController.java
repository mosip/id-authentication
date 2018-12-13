package io.mosip.preregistration.batchjobservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.batchjobservices.dto.ResponseDto;
import io.mosip.preregistration.batchjobservices.service.ArchivingConsumedStatusService;
import io.mosip.preregistration.batchjobservices.service.BatchJobService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/pre-registration/batch")
@Api(tags = "Services for batch job")
@CrossOrigin("*")
public class BatchServiceController {
	
	@Autowired
	private BatchJobService batchJobService;
	
	@Autowired
	private ArchivingConsumedStatusService archivingConsumedStatusService;
	
	@GetMapping(path="/updateConsumedStatus",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDto<String>> demographicStatusUpdate(){
		
		ResponseDto<String> response=batchJobService.demographicConsumedStatus();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(path="/archivingConsumedPreId",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDto<String>>  archivingConsumedStatus(){
		
		ResponseDto<String> response=archivingConsumedStatusService.archivingConsumed();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
