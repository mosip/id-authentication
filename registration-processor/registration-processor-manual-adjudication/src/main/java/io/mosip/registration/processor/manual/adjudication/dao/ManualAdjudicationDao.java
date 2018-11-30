package io.mosip.registration.processor.manual.adjudication.dao;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.processor.manual.adjudication.entity.ManualAdjudicationEntity;
import io.mosip.registration.processor.manual.adjudication.repository.ManualAdjudiacationRepository;


public class ManualAdjudicationDao {
	@Autowired
	ManualAdjudiacationRepository<ManualAdjudicationEntity, String> manualAdjudiacationRepository;

	public ManualAdjudicationEntity update(ManualAdjudicationEntity manualAdjudicationEntity) {

		return manualAdjudiacationRepository.save(manualAdjudicationEntity);
	}
}
