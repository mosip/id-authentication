package io.mosip.registration.dao;

import java.util.Date;
import java.util.List;

import io.mosip.registration.entity.PreRegistrationList;

/**
 * pre registartion DAO
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncDAO {

	/**
	 * To get pre registration
	 * 
	 * @param preRegId
	 *            pre registration id
	 * @return PreRegistrationList pre registartion entity
	 */
	public PreRegistrationList get(String preRegId);

	/**
	 * To save new Pre registration
	 * 
	 * @param preRegistration
	 *            pre reg entity object
	 * @return PreRegistrationList saved pre registartion
	 */
	public PreRegistrationList save(PreRegistrationList preRegistration);

	/**
	 * Fetch the Pre-Reg Records that needs to be deleted
	 * 
	 * @param startDate
	 *            - start date to fetch pre reg data
	 * @return List - pre reg list to be deleted
	 */
	public List<PreRegistrationList> fetchRecordsToBeDeleted(Date startDate);

	/**
	 * Update the Deleted Pre-Reg Records in the table
	 * 
	 * @param preReg
	 *            - pre reg entity object
	 * @return PreRegistrationList - pre reg entity
	 */
	public PreRegistrationList update(PreRegistrationList preReg);

	/**
	 * Delete list of Pre-Registartions
	 * 
	 * @param preRegistrationList
	 *            pre registation list
	 */
	public void deleteAll(List<PreRegistrationList> preRegistrationList);

	/**
	 * Fetches all the pre registration packets available in the db
	 * 
	 * @return List - pre reg list
	 */
	List<PreRegistrationList> getAllPreRegPackets();
}
