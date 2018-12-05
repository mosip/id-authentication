package io.mosip.kernel.synchandler.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.synchandler.constant.MasterDataErrorCode;
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.service.MasterDataService;

/**
 * Sync Handler Controller
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@RestController
public class SyncHandlerController {
	@Autowired
	private MasterDataService masterDataService;

	@GetMapping("/syncmasterdata/{machineId}")
	public MasterDataResponseDto syncMasterData(@PathVariable("machineId") String machineId,
			@RequestParam(value = "lastUpdated", required = false) String lastUpdated) {
		long start = System.currentTimeMillis();
		System.out.println("Going to call service method.");
		LocalDateTime timestamp = null;
		if (lastUpdated != null) {
			try {
				timestamp = LocalDateTime.parse(lastUpdated);
			} catch (Exception e) {
				throw new MasterDataServiceException(MasterDataErrorCode.LAST_UPDATED_PARSE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}

		System.out.println("service Method execution completed.");
		long elapsedTime = System.currentTimeMillis() - start;
		System.out.println("Service Method execution time: " + elapsedTime + " milliseconds.");
		return masterDataService.syncData(machineId, timestamp);
	}

}
