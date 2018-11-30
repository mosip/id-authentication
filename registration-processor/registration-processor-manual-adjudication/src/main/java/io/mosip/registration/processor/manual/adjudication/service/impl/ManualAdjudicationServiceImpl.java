package io.mosip.registration.processor.manual.adjudication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualAdjudicationEntity;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
/**
 * 
 * @author M1049617
 *
 */
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService{
	
	ManualAdjudicationEntity manualAdjudicationEntity = new ManualAdjudicationEntity();
	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;
	@Override
	public UserDto assignStatus(UserDto dto) {
		if(dto.getStatus()!=null &&dto.getStatus().equalsIgnoreCase("PENDING"))
		manualAdjudicationEntity.setStatus_code("ASSIGNED");
		manualAdjudicationDao.update(manualAdjudicationEntity);
		return dto;
	}

}
