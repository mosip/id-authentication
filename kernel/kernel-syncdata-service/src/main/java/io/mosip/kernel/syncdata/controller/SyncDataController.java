package io.mosip.kernel.syncdata.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.dto.ConfigDto;
import io.mosip.kernel.syncdata.dto.PublicKeyResponse;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
import io.mosip.kernel.syncdata.exception.DateParsingException;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import io.mosip.kernel.syncdata.service.SyncMasterDataService;
import io.mosip.kernel.syncdata.service.SyncRolesService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.MapperUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.minidev.json.JSONObject;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Sync Handler Controller
 * 
 * @author Abhishek Kumar
 * @author Srinivasan
 * @author Bal Vikash Sharma
 * @author Megha Tanga
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/v1.0")
public class SyncDataController {
	/**
	 * Service instance {@link SyncMasterDataService}
	 */
	@Autowired
	private SyncMasterDataService masterDataService;

	/**
	 * Service instance {@link SyncConfigDetailsService}
	 */
	@Autowired
	SyncConfigDetailsService syncConfigDetailsService;

	/**
	 * Service instnace {@link SyncRolesService}
	 */
	@Autowired
	SyncRolesService syncRolesService;

	@Autowired
	SyncUserDetailsService syncUserDetailsService;

	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	
	@ApiOperation(value = "API to sync global config details")
	@GetMapping(value = "/configs")
	public JSONObject getConfigDetails() {
		return syncConfigDetailsService.getConfigDetails();
	}
	
	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	@ApiIgnore
	@ApiOperation(value = "API to sync global config details")
	@GetMapping(value = "/globalconfigs")
	public JSONObject getGlobalConfigDetails() {
		return syncConfigDetailsService.getGlobalConfigDetails();
	}

	/**
	 * * This API method would fetch all synced registration center config details
	 * from server
	 * 
	 * @param regId
	 *            registration Id
	 * @return JSONObject
	 */
	@ApiIgnore
	@ApiOperation(value = "Api to get registration center configuration")
	@GetMapping(value = "/registrationcenterconfig/{registrationcenterid}")
	public JSONObject getRegistrationCentreConfig(@PathVariable(value = "registrationcenterid") String regId) {
		return syncConfigDetailsService.getRegistrationCenterConfigDetails(regId);
	}

	@ApiIgnore
	@GetMapping("/configuration/{registrationCenterId}")
	public ConfigDto getConfiguration(@PathVariable("registrationCenterId") String registrationCenterId) {
		return syncConfigDetailsService.getConfiguration(registrationCenterId);
	}

	/**
	 * Api to sync masterdata
	 * 
	 * @param machineId
	 *            id of the machine from the request is received to sync masterdata
	 * @param lastUpdated
	 *            last updated timestamp -optional if last updated timestamp is null
	 *            then fetch all the masterdata
	 * @return {@link MasterDataResponseDto}
	 * @throws InterruptedException
	 *             this API will throw interrupted exception
	 * @throws ExecutionException
	 *             this API will throw Execution exception
	 */
	@ApiOperation(value = "Api to sync the masterdata", response = MasterDataResponseDto.class)
	@GetMapping("/masterdata/{machineId}")
	public MasterDataResponseDto syncMasterData(@PathVariable("machineId") String machineId,
			@RequestParam(value = "lastUpdated", required = false) String lastUpdated)
			throws InterruptedException, ExecutionException {
		LocalDateTime timestamp = null;
		if (lastUpdated != null) {
			try {
				timestamp = MapperUtils.parseToLocalDateTime(lastUpdated);
			} catch (DateTimeParseException e) {
				throw new DateParsingException(MasterDataErrorCode.LAST_UPDATED_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}
		return masterDataService.syncData(machineId, timestamp);
	}

	/**
	 * API will fetch all roles from Auth server
	 * @return RolesResponseDto
	 */
	@GetMapping("/roles")
	public RolesResponseDto getAllRoles() {
		return syncRolesService.getAllRoles();
	}

	/**
	 * API will all the userDetails from LDAP server
	 * 
	 * @param regId
	 * @param lastUpdatedTime
	 * @return UserDetailResponseDto
	 */
	@GetMapping("/userdetails/{regid}")
	public SyncUserDetailDto getUserDetails(@PathVariable("regid") String regId) {
		return syncUserDetailsService.getAllUserDetail(regId);
	}
	
	/**
	 * Request mapping to get Public Key
	 * 
	 * @param applicationId
	 *            Application id of the application requesting publicKey
	 * @param timeStamp
	 *            Timestamp of the request
	 * @param referenceId
	 *            Reference id of the application requesting publicKey
	 * @return {@link PublicKeyResponse} instance
	 */
	@ApiOperation(value = "Get the public key of a particular application",response = PublicKeyResponse.class)
	@GetMapping(value = "/publickey/{applicationId}")
	public ResponseEntity<PublicKeyResponse<String>> getPublicKey(@ApiParam("Id of application")@PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata")	@RequestParam("timeStamp") String timeStamp,
			@ApiParam("Refrence Id as metadata")@RequestParam("referenceId")  Optional<String>referenceId) {

		return new ResponseEntity<>(syncConfigDetailsService.getPublicKey(applicationId, timeStamp, referenceId),
				HttpStatus.OK);
	}
}
