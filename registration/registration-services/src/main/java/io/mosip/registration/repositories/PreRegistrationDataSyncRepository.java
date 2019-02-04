package io.mosip.registration.repositories;

import java.util.Date;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.PreRegistrationList;

/**
 * Pre registration repository to get/save/update and verify pre-reg
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface PreRegistrationDataSyncRepository extends BaseRepository<PreRegistrationList, String> {
	
	public PreRegistrationList findByPreRegId(String preRegId);
	
	
	/**
	 * Fetch the Pre_Registration Records based on the Appointment Date
	 * @param startDate
	 * @param isDeleted
	 * @return
	 */
	public List<PreRegistrationList> findByAppointmentDateBeforeAndIsDeleted(Date startDate, Boolean isDeleted);
	
}
