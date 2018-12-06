package io.mosip.registration.processor.manual.adjudication.dao;

import java.util.HashMap;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.manual.adjudication.repository.ManualAdjudiacationRepository;



@Component
public class ManualAdjudicationDao {
	@Autowired
	ManualAdjudiacationRepository<ManualVerificationEntity, ManualVerificationPKEntity> manualAdjudiacationRepository;
	
	public ManualVerificationEntity update(ManualVerificationEntity manualAdjudicationEntity) {
		return manualAdjudiacationRepository.save(manualAdjudicationEntity);
	}
	
	public List<ManualVerificationEntity> getFirstApplicantDetails(String status) {
		List<ManualVerificationEntity> manualAdjudicationEntitiesList = manualAdjudiacationRepository
				.getFirstApplicantDetails(status);
		return manualAdjudicationEntitiesList;
		
	}
	public ManualVerificationEntity getByRegId(String regId,String mvUsrId) {
		return manualAdjudiacationRepository.getByRegId(regId,mvUsrId);
	}
	
	public ManualVerificationEntity getAssignedApplicantDetails(String userId, String status) {
		return manualAdjudiacationRepository.getAssignedApplicantDetails(userId,status);
	}
}
