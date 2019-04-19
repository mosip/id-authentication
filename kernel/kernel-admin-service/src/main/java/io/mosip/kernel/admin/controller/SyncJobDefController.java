package io.mosip.kernel.admin.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.admin.dto.response.SyncJobDefResponseDto;
import io.mosip.kernel.admin.service.SyncJobDefService;
import io.mosip.kernel.admin.utils.LocalDateTimeUtil;
import io.mosip.kernel.core.http.ResponseFilter;
import io.swagger.annotations.ApiOperation;

/**
 * SyncJobDefController class.
 */
@RestController
@RequestMapping("/syncjobdef")
public class SyncJobDefController {

	/** instance of job def service. */
	@Autowired
	SyncJobDefService syncJobDefService;

	@Autowired
	LocalDateTimeUtil localDateTimeUtil;

	/**
	 * Gets the sync job def.
	 *
	 * @param lastUpdated
	 *            the last updated
	 * @return list of {@link SyncJobDefResponseDto}
	 */
	@GetMapping
	@ApiOperation(value = "Sync job definition ")
	@ResponseFilter
	public SyncJobDefResponseDto getSyncJobDef(@RequestParam(value="lastupdatedtimestamp",required = false) String lastUpdated) {
		LocalDateTime currentTimeStamp = LocalDateTime.now(ZoneOffset.UTC);
		LocalDateTime lastUpdatedTimeStamp = localDateTimeUtil.getLocalDateTimeFromTimeStamp(currentTimeStamp,
				lastUpdated);
		return syncJobDefService.getLatestSyncJobDefDetails(lastUpdatedTimeStamp, currentTimeStamp);
	}
}
