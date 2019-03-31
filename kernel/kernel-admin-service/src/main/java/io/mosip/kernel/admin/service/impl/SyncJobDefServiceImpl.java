package io.mosip.kernel.admin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.admin.constant.AdminServiceErrorCode;
import io.mosip.kernel.admin.dto.SyncJobDefDto;
import io.mosip.kernel.admin.dto.response.SyncJobDefResponseDto;
import io.mosip.kernel.admin.entity.SyncJobDef;
import io.mosip.kernel.admin.exception.AdminServiceException;
import io.mosip.kernel.admin.repository.SyncJobDefRepository;
import io.mosip.kernel.admin.service.SyncJobDefService;
import io.mosip.kernel.admin.utils.MapperUtils;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;


/**
 *  Class SyncJobDefServiceImpl.
 */
@Service
public class SyncJobDefServiceImpl implements SyncJobDefService {

	/** instnace of sync job def repository. */
	@Autowired
	SyncJobDefRepository syncJobDefRepository;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.admin.service.SyncJobDefService#getLatestSyncJobDefDetails(java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public SyncJobDefResponseDto getLatestSyncJobDefDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<SyncJobDefDto> syncJobDefDtos = null;
		List<SyncJobDef> syncJobDefs = null;
		SyncJobDefResponseDto syncJobResponseDto = null;
		try {
			syncJobDefs = syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new AdminServiceException(AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
					AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage());
		}
		if(syncJobDefs!=null && !syncJobDefs.isEmpty()) {
		syncJobDefDtos = MapperUtils.mapAll(syncJobDefs, SyncJobDefDto.class);
		syncJobResponseDto = new SyncJobDefResponseDto();
		syncJobResponseDto.setSyncJobDefinitions(syncJobDefDtos);
		}
		return syncJobResponseDto;
	}

}
