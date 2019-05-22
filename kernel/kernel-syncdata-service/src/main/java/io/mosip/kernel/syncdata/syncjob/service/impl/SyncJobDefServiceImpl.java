package io.mosip.kernel.syncdata.syncjob.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.syncdata.syncjob.constant.AdminServiceErrorCode;
import io.mosip.kernel.syncdata.syncjob.dto.SyncJobDefDto;
import io.mosip.kernel.syncdata.syncjob.entity.SyncJobDef;
import io.mosip.kernel.syncdata.syncjob.exception.AdminServiceException;
import io.mosip.kernel.syncdata.syncjob.repository.SyncJobDefRepository;
import io.mosip.kernel.syncdata.syncjob.response.SyncJobDefResponseDto;
import io.mosip.kernel.syncdata.syncjob.service.SyncJobDefService;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * Class SyncJobDefServiceImpl.
 */
@Service
public class SyncJobDefServiceImpl implements SyncJobDefService {

	/** instnace of sync job def repository. */
	@Autowired
	SyncJobDefRepository syncJobDefRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.admin.service.SyncJobDefService#getLatestSyncJobDefDetails(
	 * java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public SyncJobDefResponseDto getLatestSyncJobDefDetails(LocalDateTime lastUpdatedTime,
			LocalDateTime currentTimeStamp) {
		List<SyncJobDefDto> syncJobDefDtos = null;
		List<SyncJobDef> syncJobDefs = null;
		SyncJobDefResponseDto syncJobResponseDto = null;
		if (lastUpdatedTime == null) {
			lastUpdatedTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
		}
		try {
			syncJobDefs = syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(lastUpdatedTime,
					currentTimeStamp);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new AdminServiceException(AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorCode(),
					AdminServiceErrorCode.SYNC_JOB_DEF_FETCH_EXCEPTION.getErrorMessage());
		}
		if (syncJobDefs != null && !syncJobDefs.isEmpty()) {
			syncJobDefDtos = MapperUtils.mapAll(syncJobDefs, SyncJobDefDto.class);
			syncJobResponseDto = new SyncJobDefResponseDto();
			syncJobResponseDto.setSyncJobDefinitions(syncJobDefDtos);
		}
		return syncJobResponseDto;
	}

}
