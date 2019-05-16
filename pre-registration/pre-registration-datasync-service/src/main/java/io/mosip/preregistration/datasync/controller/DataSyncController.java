package io.mosip.preregistration.datasync.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.ResponseFilter;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.service.DataSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Data Sync Controller
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@RestController
@RequestMapping("/")
@Api(tags = "Data-Sync")
@CrossOrigin("*")
public class DataSyncController {

	@Autowired
	private DataSyncService dataSyncService;

	private Logger log = LoggerConfiguration.logConfig(DataSyncController.class);

	/**
	 * @param DataSyncDTO
	 * @return responseDto
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','REGISTRATION_ ADMIN')")
	@ResponseFilter
	@PostMapping(path = "/sync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All PreRegistrationIds fetched successfully") })
	@ApiOperation(value = "Fetch all PreRegistrationIds")
	public ResponseEntity<MainResponseDTO<PreRegistrationIdsDTO>> retrieveAllPreRegids(
			@RequestBody(required = true) MainRequestDTO<DataSyncRequestDTO> dataSyncDto) {
		log.info("sessionId", "idType", "id",
				"In Datasync controller for retreiving all the pre-registrations for object  " + dataSyncDto);
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.retrieveAllPreRegIds(dataSyncDto));
	}

	/**
	 * @param preId
	 * @return zip file to download
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','REGISTRATION_ ADMIN')")
	@ResponseFilter
	@GetMapping(path = "/sync/{preRegistrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Data Sync records fetched") })
	public ResponseEntity<MainResponseDTO<PreRegArchiveDTO>> retrievePreRegistrations(
			@PathVariable(required = true, value = "preRegistrationId") String preRegistrationId) {
		log.info("sessionId", "idType", "id",
				"In Datasync controller for retreiving pre-registration data with preRegId " + preRegistrationId);
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.getPreRegistrationData(preRegistrationId));
	}

	/**
	 * @param consumedData
	 * @return response object
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','REGISTRATION_ ADMIN','REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@PostMapping(path = "/sync/consumedPreRegIds", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Store consumed Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Consumed Pre-Registrations saved") })
	public ResponseEntity<MainResponseDTO<ReverseDatasyncReponseDTO>> storeConsumedPreRegistrationsIds(
			@NotNull @RequestBody(required = true) MainRequestDTO<ReverseDataSyncRequestDTO> consumedData) {
		log.info("sessionId", "idType", "id",
				"In Datasync controller for storing the consumed preregistration with object" + consumedData);
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.storeConsumedPreRegistrations(consumedData));
	}

}
