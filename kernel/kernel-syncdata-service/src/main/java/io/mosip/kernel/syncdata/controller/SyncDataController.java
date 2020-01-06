package io.mosip.kernel.syncdata.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.syncdata.dto.ConfigDto;
import io.mosip.kernel.syncdata.dto.PublicKeyResponse;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.UploadPublicKeyRequestDto;
import io.mosip.kernel.syncdata.dto.UploadPublicKeyResponseDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import io.mosip.kernel.syncdata.service.SyncMasterDataService;
import io.mosip.kernel.syncdata.service.SyncRolesService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.LocalDateTimeUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.minidev.json.JSONObject;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Sync Handler Controller
 * 
 * 
 * @author Srinivasan
 * @author Abhishek Kumar
 * @author Bal Vikash Sharma
 * @author Megha Tanga
 * @author Urvil Joshi
 * @since 1.0.0
 */
@RestController
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

	@Autowired
	LocalDateTimeUtil localDateTimeUtil;

	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@ApiOperation(value = "API to sync global config details")
	@GetMapping(value = "/configs")
	public ResponseWrapper<ConfigDto> getConfigDetails() {
		String currentTimeStamp = DateUtils.getUTCCurrentDateTimeString();
		ConfigDto syncConfigResponse = syncConfigDetailsService.getConfigDetails();
		syncConfigResponse.setLastSyncTime(currentTimeStamp);
		ResponseWrapper<ConfigDto> response = new ResponseWrapper<>();
		response.setResponse(syncConfigResponse);
		return response;
	}

	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@ApiIgnore
	@ApiOperation(value = "API to sync global config details")
	@GetMapping(value = "/globalconfigs")
	public ResponseWrapper<JSONObject> getGlobalConfigDetails() {
		ResponseWrapper<JSONObject> response = new ResponseWrapper<>();
		response.setResponse(syncConfigDetailsService.getGlobalConfigDetails());
		return response;
	}

	/**
	 * * This API method would fetch all synced registration center config details
	 * from server
	 * 
	 * @param regId registration Id
	 * @return JSONObject
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@ApiIgnore
	@ApiOperation(value = "Api to get registration center configuration")
	@GetMapping(value = "/registrationcenterconfig/{registrationcenterid}")
	public ResponseWrapper<JSONObject> getRegistrationCentreConfig(
			@PathVariable(value = "registrationcenterid") String regId) {
		ResponseWrapper<JSONObject> response = new ResponseWrapper<>();
		response.setResponse(syncConfigDetailsService.getRegistrationCenterConfigDetails(regId));
		return response;
	}

	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@ApiIgnore
	@GetMapping("/configuration/{registrationCenterId}")
	public ResponseWrapper<ConfigDto> getConfiguration(
			@PathVariable("registrationCenterId") String registrationCenterId) {
		ResponseWrapper<ConfigDto> response = new ResponseWrapper<>();
		response.setResponse(syncConfigDetailsService.getConfiguration(registrationCenterId));
		return response;
	}

	/**
	 * 
	 * @param macId        - MAC address of the machine
	 * @param serialNumber - Serial number of the machine
	 * @param lastUpdated  - last updated time stamp
	 * @return {@link MasterDataResponseDto}
	 * @throws InterruptedException - this method will throw interrupted Exception
	 * @throws ExecutionException   - this method will throw exeution exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/masterdata")
	public ResponseWrapper<MasterDataResponseDto> syncMasterData(
			@RequestParam(value = "macaddress", required = false) String macId,
			@RequestParam(value = "serialnumber", required = false) String serialNumber,
			@RequestParam(value = "lastupdated", required = false) String lastUpdated,
			@RequestParam(value="keyindex",required=false)String keyIndex)
			throws InterruptedException, ExecutionException {

		LocalDateTime currentTimeStamp = LocalDateTime.now(ZoneOffset.UTC);
		LocalDateTime timestamp = localDateTimeUtil.getLocalDateTimeFromTimeStamp(currentTimeStamp, lastUpdated);
		String regCenterId = null;
		MasterDataResponseDto masterDataResponseDto = masterDataService.syncData(regCenterId, macId, serialNumber,
				timestamp, currentTimeStamp,keyIndex);

		masterDataResponseDto.setLastSyncTime(DateUtils.formatToISOString(currentTimeStamp));

		ResponseWrapper<MasterDataResponseDto> response = new ResponseWrapper<>();
		response.setResponse(masterDataResponseDto);
		return response;
	}

	/**
	 * 
	 * @param macId        - MAC address of the machine
	 * @param serialNumber - Serial number of the machine
	 * @param regCenterId  - reg Center Id
	 * @param lastUpdated  - last updated time stamp
	 * @return {@link MasterDataResponseDto}
	 * @throws InterruptedException - this method will throw interrupted Exception
	 * @throws ExecutionException   - this method will throw exeution exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/masterdata/{regcenterId}")
	public ResponseWrapper<MasterDataResponseDto> syncMasterDataWithRegCenterId(
			@PathVariable("regcenterId") String regCenterId,
			@RequestParam(value = "macaddress", required = false) String macId,
			@RequestParam(value = "serialnumber", required = false) String serialNumber,
			@RequestParam(value = "lastupdated", required = false) String lastUpdated,
			@RequestParam(value="keyindex",required=false)String keyIndex)
			throws InterruptedException, ExecutionException {

		LocalDateTime currentTimeStamp = LocalDateTime.now(ZoneOffset.UTC);
		LocalDateTime timestamp = localDateTimeUtil.getLocalDateTimeFromTimeStamp(currentTimeStamp, lastUpdated);
		MasterDataResponseDto masterDataResponseDto = masterDataService.syncData(regCenterId, macId, serialNumber,
				timestamp, currentTimeStamp,keyIndex);

		masterDataResponseDto.setLastSyncTime(DateUtils.formatToISOString(currentTimeStamp));

		ResponseWrapper<MasterDataResponseDto> response = new ResponseWrapper<>();
		response.setResponse(masterDataResponseDto);
		return response;
	}

	/**
	 * API will fetch all roles from Auth server
	 * 
	 * @return RolesResponseDto
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/roles")
	public ResponseWrapper<RolesResponseDto> getAllRoles() {
		String currentTimeStamp = DateUtils.getUTCCurrentDateTimeString();
		RolesResponseDto rolesResponseDto = syncRolesService.getAllRoles();
		rolesResponseDto.setLastSyncTime(currentTimeStamp);
		ResponseWrapper<RolesResponseDto> response = new ResponseWrapper<>();
		response.setResponse(rolesResponseDto);
		return response;
	}

	/**
	 * API will all the userDetails from LDAP server
	 * 
	 * @param regId - registration center Id
	 * 
	 * @return UserDetailResponseDto - user detail response
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping("/userdetails/{regid}")
	public ResponseWrapper<SyncUserDetailDto> getUserDetails(@PathVariable("regid") String regId) {
		String currentTimeStamp = DateUtils.getUTCCurrentDateTimeString();
		SyncUserDetailDto syncUserDetailDto = syncUserDetailsService.getAllUserDetail(regId);
		syncUserDetailDto.setLastSyncTime(currentTimeStamp);
		ResponseWrapper<SyncUserDetailDto> response = new ResponseWrapper<>();
		response.setResponse(syncUserDetailDto);
		return response;
	}

	/**
	 * Request mapping to get Public Key
	 * 
	 * @param applicationId Application id of the application requesting publicKey
	 * @param timeStamp     Timestamp of the request
	 * @param referenceId   Reference id of the application requesting publicKey
	 * @return {@link PublicKeyResponse} instance
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@GetMapping(value = "/publickey/{applicationId}")
	public ResponseWrapper<PublicKeyResponse<String>> getPublicKey(
			@ApiParam("Id of application") @PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata") @RequestParam(value="timeStamp",required=false) String timeStamp,
			@ApiParam("Refrence Id as metadata") @RequestParam(value="referenceId",required=false) String referenceId) {

		String currentTimeStamp = DateUtils.getUTCCurrentDateTimeString();
		PublicKeyResponse<String> publicKeyResponse = syncConfigDetailsService.getPublicKey(applicationId, timeStamp,
				referenceId);
		publicKeyResponse.setLastSyncTime(currentTimeStamp);

		ResponseWrapper<PublicKeyResponse<String>> response = new ResponseWrapper<>();
		response.setResponse(publicKeyResponse);
		return response;
	}
	
	@PreAuthorize("hasAnyRole('REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','REGISTRATION_ADMIN')")
	@ResponseFilter
	@PostMapping(value = "/tpm/publickey", produces = "application/json")
	public ResponseWrapper<UploadPublicKeyResponseDto> uploadpublickey(@ApiParam("public key in BASE64 encoded")@RequestBody @Valid RequestWrapper<UploadPublicKeyRequestDto> uploadPublicKeyRequestDto) {
		ResponseWrapper<UploadPublicKeyResponseDto> response = new ResponseWrapper<>();
		response.setResponse(masterDataService.uploadpublickey(uploadPublicKeyRequestDto.getRequest()));
		return response;
	}

}
