package io.mosip.kernel.syncdata.controller;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.syncdata.constant.MasterDataErrorCode;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.exception.DateParsingException;
import io.mosip.kernel.syncdata.service.MasterDataService;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

/**
 * Sync Handler Controller
 * 
 * @author Abhishek Kumar
 * @author Srinivasan
 * @since 1.0.0
 */
@RestController
@RequestMapping(value="/v1.0")
public class SyncHandlerController {
	@Autowired
	private MasterDataService masterDataService;

	/**
	 * Service instance {@link SyncConfigDetailsService}
	 */
	@Autowired
	SyncConfigDetailsService syncConfigDetailsService;

	/**
	 * This API method would fetch all synced global config details from server
	 * 
	 * @return JSONObject - global config response
	 */
	@GetMapping(value = "/globalconfigs")
	public JSONObject getGlobalConfigDetails() {
		return syncConfigDetailsService.getGlobalConfigDetails();
	}

	/**
	 * * This API method would fetch all synced registration center config details
	 * from server
	 * 
	 * @param regId - registration Id
	 * @return JSONObject
	 */
	@GetMapping(value = "/registrationcenterconfig/{registrationcenterid}")
	public JSONObject getRegistrationCentreConfig(@PathVariable(value = "registrationcenterid") String regId) {
		return syncConfigDetailsService.getRegistrationCenterConfigDetails(regId);
	}

	/**
	 * api to fetch masterdata
	 * 
	 * @param machineId
	 *            machine id - mandatory param
	 * @param lastUpdated
	 *            last updated timestamp -optional if last updated timestamp is null
	 *            then fetch all the masterdata
	 * @return {@link MasterDataResponseDto}
	 * @throws InterruptedException - this API will throw interrupted exception 
	 * @throws ExecutionException - this API will throw Executin exception
	 */
	@GetMapping("/syncmasterdata/{machineId}")
	public MasterDataResponseDto syncMasterData(@PathVariable("machineId") String machineId,
			@RequestParam(value = "lastUpdated", required = false) String lastUpdated)
			throws InterruptedException, ExecutionException {
		LocalDateTime timestamp = null;
		if (lastUpdated != null) {
			try {
				timestamp = LocalDateTime.parse(lastUpdated);
			} catch (Exception e) {
				throw new DateParsingException(MasterDataErrorCode.LAST_UPDATED_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}
		return masterDataService.syncData(machineId, timestamp);
	}

}
