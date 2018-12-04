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
	/** The Constant AND. */
	public static final String AND = "AND";

	/** The Constant EMPTY_STRING. */
	public static final String EMPTY_STRING = " ";

	/** The Constant SELECT_DISTINCT. */
	public static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	/** The Constant FROM. */
	public static final String FROM = " FROM  ";

	/** The Constant WHERE. */
	public static final String WHERE = " WHERE ";
	
	/** The Constant WHERE. */
	public static final String ORDER_BY = " ORDER BY ";

	
	public ManualVerificationEntity update(ManualVerificationEntity manualAdjudicationEntity) {
		return manualAdjudiacationRepository.save(manualAdjudicationEntity);
	}
	
	public List<ManualVerificationEntity> getFirstApplicantDetails(){
		Map<String, Object> params = new HashMap<>();
		String className = ManualVerificationEntity.class.getSimpleName();
		String alias = ManualVerificationEntity.class.getName().toLowerCase().substring(0, 1);
		
		String queryStr = SELECT_DISTINCT + alias + FROM + className + WHERE + alias +".cr_dtimes in "
				+ "(" + "select min(" +alias + ".cr_dtimes)" +FROM +className +")" +AND +alias+ ".status_code =:statusCode" ;
		params.put("statusCode", "PENDING");
		List<ManualVerificationEntity> manualAdjudicationEntitiesList = manualAdjudiacationRepository
				.createQuerySelect(queryStr, params);
		return manualAdjudicationEntitiesList;
		
	}
}
