package io.mosip.kernel.syncdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.syncdata.constant.AdminServiceErrorCode;
import io.mosip.kernel.syncdata.dto.SyncJobDefDto;
import io.mosip.kernel.syncdata.entity.SyncJobDef;
import io.mosip.kernel.syncdata.exception.AdminServiceException;
import io.mosip.kernel.syncdata.service.SyncJobDefService;
import io.mosip.kernel.syncdata.syncjob.repository.SyncJobDefRepository;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class contains the business logic for CRUD opertaion.
 *
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class SyncJobDefServiceImpl implements SyncJobDefService {

	@Autowired
	private SyncJobDefRepository syncJobDefRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncJobDefService#getSyncJobDefDetails(java.
	 * time.LocalDateTime, java.time.LocalDateTime)
	 */
	@Override
	public List<SyncJobDefDto> getSyncJobDefDetails(LocalDateTime lastUpdatedTime, LocalDateTime currentTimeStamp) {

		List<SyncJobDefDto> syncJobDefDtos = null;
		List<SyncJobDef> syncJobDefs = null;
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
		}
		return syncJobDefDtos;
	}

}
