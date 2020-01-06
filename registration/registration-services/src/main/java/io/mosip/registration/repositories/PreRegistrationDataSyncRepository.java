package io.mosip.registration.repositories;

import java.util.Date;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.PreRegistrationList;

/**
 * Pre registration repository to get/save/update and verify pre-reg
 * 
 * @author YASWANTH S
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */
public interface PreRegistrationDataSyncRepository extends BaseRepository<PreRegistrationList, String> {

	public PreRegistrationList findByPreRegId(String preRegId);

	/**
	 * Fetch the Pre_Registration Records based on the Appointment Date
	 * 
	 * @param startDate
	 *            - start date
	 * @param isDeleted
	 *            - deleted flag
	 * @return List - list of pre reg objects
	 */
	public List<PreRegistrationList> findByAppointmentDateBeforeAndIsDeleted(Date startDate, Boolean isDeleted);
	
	/**
	 * Fetch the LastUpdatedPreRegTime of the latest
	 * {@link PreRegistrationList}
	 * 
	 * @return the {@link PreRegistrationList}
	 */
	public PreRegistrationList findTopByOrderByLastUpdatedPreRegTimeStampDesc();

}
