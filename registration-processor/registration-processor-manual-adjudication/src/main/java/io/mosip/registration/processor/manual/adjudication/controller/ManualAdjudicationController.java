package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/registration-processor/manual-adjudication")
@Api(tags = "Manual Adjudication")
public class ManualAdjudicationController {
	@Autowired
	private ManualAdjudicationService manualAdjudicationService;
	
	//Boolean t=manualAdjudicationService.updateStatus();
		

}
