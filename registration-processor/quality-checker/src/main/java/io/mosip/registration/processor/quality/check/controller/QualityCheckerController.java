package io.mosip.registration.processor.quality.check.controller;
	
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.code.QualityCheckerStatusCode;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class QualityCheckerController.
 */
@RestController
@RequestMapping("/v0.1/registration-processor/quality-checker")
@Api(tags = "Quality Checker")
public class QualityCheckerController {

	/** The quality check manger. */
	@Autowired
	private QualityCheckManager<String, QCUserDto> qualityCheckManger;


	/**
	 * Update QC user status controller.
	 *
	 * @param qcUserDtos the qc user dtos
	 * @return the response entity
	 */
	@PostMapping(path = "/decisionStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the QCUser entity", response = QualityCheckerStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "QC User Entity decision status successfully updated") })
	public ResponseEntity<List<QCUserDto>> updateQCUserStatusController(@RequestBody(required = true) List<QCUserDto> qcUserDtos) {
		List<QCUserDto> dtos = qualityCheckManger.updateQCUserStatus(qcUserDtos);
		return ResponseEntity.status(HttpStatus.OK).body(dtos);
	}
}