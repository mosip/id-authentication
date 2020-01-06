package io.mosip.kernel.syncdata.service;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.syncdata.dto.SyncJobDefDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface SyncJobDefService {
	
	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime,LocalDateTime currentTimeStamp);
	
}
