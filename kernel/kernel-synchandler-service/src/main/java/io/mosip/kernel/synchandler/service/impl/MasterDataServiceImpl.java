package io.mosip.kernel.synchandler.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.synchandler.constant.MasterDataErrorCode;
import io.mosip.kernel.synchandler.dto.ApplicationDto;
import io.mosip.kernel.synchandler.dto.MasterDataResponseDto;
import io.mosip.kernel.synchandler.entity.Application;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.repository.ApplicationRepository;
import io.mosip.kernel.synchandler.service.MasterDataService;

/**
 * masterdata sync handler service impl
 * 
 * @author Abhishek Kumar
 *
 */
@Service
public class MasterDataServiceImpl implements MasterDataService {

	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private ApplicationRepository applicationRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.synchandler.service.MasterDataService#syncData(java.lang.
	 * String, java.time.LocalDate)
	 */
	@Override
	public MasterDataResponseDto syncData(String machineId, LocalDateTime lastUpdated) {
		MasterDataResponseDto response = new MasterDataResponseDto();
		response.setApplications(getApplications(lastUpdated));
		return response;
	}

	private List<ApplicationDto> getApplications(LocalDateTime lastUpdated) {
		List<ApplicationDto> applications = new ArrayList<>();
		List<Application> applicationList;
		try {
			applicationList = applicationRepository.findAllNewUpdateDeletedApplication(lastUpdated);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MasterDataErrorCode.APPLICATION_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		if (!(applicationList.isEmpty())) {
			applicationList.forEach(application -> {
				ApplicationDto applicationDto = new ApplicationDto();
				dataMapper.map(application, applicationDto, true, null, null, true);
				applications.add(applicationDto);
			});
		}
		return applications;
	}

}
