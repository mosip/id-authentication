/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.batchjobservices.service.ExpiredStatusService;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.swagger.annotations.Api;

/**
 * This is a controller class of batch job service.
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/")
@Api(tags = "Services for batch job")
@CrossOrigin("*")
public class BatchServiceController {
	
	@Autowired
	private ConsumedStatusService consumedStatusService;
	
	@Autowired
	private ExpiredStatusService expiredStatusService;
	
	@PreAuthorize("hasAnyRole('PRE_REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR')")
	@PutMapping(path="/consumedStatus",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MainResponseDTO<String>> consumedAppointments(){
		
		MainResponseDTO<String> response=consumedStatusService.demographicConsumedStatus();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PreAuthorize("hasAnyRole('PRE_REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR')")
	@PutMapping(path="/expiredStatus",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MainResponseDTO<String>> expiredAppointments(){
		
		MainResponseDTO<String> response=expiredStatusService.expireAppointments();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
