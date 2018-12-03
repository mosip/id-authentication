package io.mosip.registration.processor.manual.adjudication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.ApplicantDetailsDto;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
/**
 * 
 * @author M1049617
 *
 */
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService{
	
	ManualVerificationEntity manualAdjudicationEntity = new ManualVerificationEntity();
	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;
	@Override
	public ApplicantDetailsDto assignStatus(UserDto dto) {
		ApplicantDetailsDto adto=new ApplicantDetailsDto();
		
		if(dto.getStatus()!=null &&dto.getStatus().equalsIgnoreCase("PENDING")) {
			
		String regid=manualAdjudicationDao.getFirstApplicantDetails().get(0).getRegId();
		manualAdjudicationEntity.setStatusCode(dto.getStatus());
		manualAdjudicationEntity.setMvUserId(dto.getUserId());
		manualAdjudicationEntity.setRegId(regid);
		manualAdjudicationDao.update(manualAdjudicationEntity);
		}
		
		return adto;
	}
	
	

}
