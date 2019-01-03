package io.mosip.preregistration.datasync.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.MainRequestDTO;
import io.mosip.preregistration.datasync.dto.MainResponseDTO;
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
@RequestMapping("/v0.1/pre-registration/data-sync/")
@Api(tags = "Data-Sync")
@CrossOrigin("*")
public class DataSyncController {

	@Autowired
	private DataSyncService dataSyncService;

	/**
	 * @param DataSyncDTO
	 * @return responseDto
	 */
	@PostMapping(path = "/reteriveAllPreRegIds", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All PreRegistrationIds fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch PreRegistrationIds ") })
	@ApiOperation(value = "Fetch all PreRegistrationIds")
	public ResponseEntity<MainResponseDTO<PreRegistrationIdsDTO>> retrieveAllPreRegids(
			@RequestBody(required = true) MainRequestDTO<DataSyncRequestDTO> dataSyncDto) {
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.retrieveAllPreRegIds(dataSyncDto));
	}

	/**
	 * @param preId
	 * @return zip file to download
	 */
	@GetMapping(path = "/datasync", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Data Sync records fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the records") })
	public ResponseEntity<MainResponseDTO<PreRegArchiveDTO>> retrievePreRegistrations(
			 @RequestParam(required = true,  value = "preId") String preId) {
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.getPreRegistrationData(preId));
	}

	/**
	 * @param consumedData
	 * @return response object
	 */
	@PostMapping(path = "/reverseDataSync", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Store consumed Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Consumed Pre-Registrations saved"),
			@ApiResponse(code = 400, message = "Unable to save the records") })
	public ResponseEntity<MainResponseDTO<ReverseDatasyncReponseDTO>> storeConsumedPreRegistrationsIds(
			@NotNull @RequestBody(required = true) MainRequestDTO<ReverseDataSyncRequestDTO> consumedData) {
		return ResponseEntity.status(HttpStatus.OK).body(dataSyncService.storeConsumedPreRegistrations(consumedData));
	}

}
