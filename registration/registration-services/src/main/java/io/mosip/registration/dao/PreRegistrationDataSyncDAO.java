package io.mosip.registration.dao;

import java.util.Date;
import java.util.List;

import io.mosip.registration.entity.PreRegistrationList;

/**
 * pre registartion DAO
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncDAO {

	/**
	 * To get pre registration
	 * @param preRegId is a id 
	 * @return pre registartion entity
	 */
	public PreRegistrationList get(String preRegId);
	
	
	/**
	 * To save new Pre registration
	 * @param preRegistration is a entity
	 * @return saved pre registartion
	 */
	public PreRegistrationList save(PreRegistrationList preRegistration);


	
	/**
	 * Fetch the Pre-Reg Records that needs to be deleted
	 * @param startDate
	 * @return
	 */
	public List<PreRegistrationList> fetchRecordsToBeDeleted(Date startDate);
	
	/**
	 * Update the Deleted Pre-Reg Records in the table
	 * @param preReg
	 * @return
	 */
	public PreRegistrationList update(PreRegistrationList preReg);
}
