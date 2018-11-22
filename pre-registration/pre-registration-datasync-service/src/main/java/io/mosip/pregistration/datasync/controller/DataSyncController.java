package io.mosip.pregistration.datasync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.pregistration.datasync.dto.DataSyncDTO;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ResponseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.service.DataSyncService;
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
@Api(tags = "Data Sync")
@CrossOrigin("*")
public class DataSyncController {

	@Autowired
	private DataSyncService dataSyncService;

	/**
	 * @param preIds
	 * @return responseDto
	 * @throws Exception
	 */
	
	@PostMapping(path = "/datasync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All PreRegistrationIds fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch PreRegistrationIds ") })
	@ApiOperation(value = "Fetch all PreRegistrationIds")
	public ResponseEntity<ResponseDTO<ResponseDataSyncDTO>> retrieveAllPreRegids(@RequestBody(required = true) DataSyncDTO dataSyncDto) {
		
		ResponseDTO<ResponseDataSyncDTO> responseDto = dataSyncService.retrieveAllPreRegid(dataSyncDto.getDataSyncRequestDto());
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
	
	/**
	 * @param preId
	 * @return zip file to download
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/datasync", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Retrieve Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Data Sync records fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the records") })
	public ResponseEntity<byte[]> retrievePreRegistrations(@RequestParam(value = "preId") String preId)
			throws Exception {

		byte[] bytes = null;
		String filename = "";
		ResponseDTO responseDto = dataSyncService.getPreRegistration(preId);
		if (responseDto != null && responseDto.getResponse().size() > 0 && responseDto.getResponse().get(0) != null) {

			bytes = (byte[]) responseDto.getResponse().get(0);
			filename = responseDto.getResponse().get(1).toString();
		}

		System.out.println("filename in controller: " + filename);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/octet-stream");
		responseHeaders.add("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");

		return new ResponseEntity<byte[]>(bytes, responseHeaders, HttpStatus.OK);
	}

	/**
	 * @param consumedData
	 * @return response object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping(path = "/reverseDataSync", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Store consumed Pre-Registrations")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Consumed Pre-Registrations saved"),
			@ApiResponse(code = 400, message = "Unable to save the records") })
	public ResponseEntity<ResponseDTO<ReverseDataSyncDTO>> storeConsumedPreRegistrationsIds(
			@RequestBody(required = true) ReverseDataSyncDTO consumedData)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseDTO<ReverseDataSyncDTO> responseDto = dataSyncService.storeConsumedPreRegistrations(consumedData);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);

	}
	
	

}
