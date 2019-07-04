package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import io.mosip.registration.entity.PreRegistrationList;

/**
 * This class is used to fetch the specific pre registration by passing pre registration id as parameter,
 * To save the new pre registration record to {@link PreRegistrationList} table. To fetch the list
 * of pre registration that needs to be deleted by passing start date as parameter from 
 * {@link PreRegistrationList} table. To update the pre registration record in the {@link PreRegistrationList} table.
 * To delete all the specifically given list of pre registration records and to fetch all the pre registration 
 * present in the {@link PreRegistrationList} table.  
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncDAO {

	/**
	 * This method is used to get the pre registration from  {@link PreRegistrationList} table by 
	 * passing pre registration ID as parameter.
	 * 
	 * @param preRegId
	 *            pre registration id
	 * @return PreRegistrationList pre registration entity
	 */
	public PreRegistrationList get(String preRegId);

	/**
	 * This method is udsed to save new Pre registration to the {@link PreRegistrationList} table.
	 * 
	 * @param preRegistration
	 *            pre reg entity object
	 * @return PreRegistrationList saved pre registartion
	 */
	public PreRegistrationList save(PreRegistrationList preRegistration);

	/**
	 * This method is used to fetch the list of Pre-Registration Records that needs to be deleted from  {@link PreRegistrationList} table.
	 * 
	 * @param startDate
	 *            - start date to fetch pre registration data
	 * @return List - List of pre registrations to be deleted
	 */
	public List<PreRegistrationList> fetchRecordsToBeDeleted(Date startDate);

	/**
	 * This method id used to update the Pre-Registration Records in the {@link PreRegistrationList} table.
	 * 
	 * @param preReg
	 *            - pre registration entity object
	 * @return PreRegistrationList - pre registration entity
	 */
	public PreRegistrationList update(PreRegistrationList preReg);

	/**
	 * This method is used to delete the list of Pre-Registartions from  {@link PreRegistrationList} table.
	 * 
	 * @param preRegistrationList
	 *            pre registration list
	 */
	public void deleteAll(List<PreRegistrationList> preRegistrationList);

	/**
	 * This method is used to fetch all the pre registration packets available in the {@link PreRegistrationList} table.
	 * 
	 * @return List - pre registration list
	 */
	List<PreRegistrationList> getAllPreRegPackets();
	
	/**
	 * Last pre registration packet downloaded date time.
	 *
	 * @return the timestamp
	 */
	public Timestamp getLastPreRegPacketDownloadedTime();
}
