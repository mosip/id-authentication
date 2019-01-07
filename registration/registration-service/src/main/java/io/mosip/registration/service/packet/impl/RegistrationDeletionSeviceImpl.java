/**
 * 
 */
package io.mosip.registration.service.packet.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.service.packet.RegistrationDeletionService;

/**
 * Registration Deletion Service
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Service
public class RegistrationDeletionSeviceImpl implements RegistrationDeletionService {
	
	@Autowired
	private RegistrationDAO registrationDAO;
	
	/**
	 * Required no days to maintain registrations
	 */
	@Value("${REG_NO_OF_DAYS_LIMIT_TO_DELETE}")
	int noOfDays;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.packet.RegistrationDeletionService#deleteReRegistrationPackets()
	 */
	@Override
	public ResponseDTO deleteReRegistrationPackets() {
		Timestamp reqTime = new Timestamp(System.currentTimeMillis());
		
		//List<Registration> registrations=registrationDAO.getRegistrationsToBeDeleted(getPacketDeletionLastDate(reqTime), RegistrationConstants.) 
		
		return null;
	}
	
	private Timestamp getPacketDeletionLastDate(Timestamp reqTime) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(reqTime);
		cal.add(Calendar.DATE, -noOfDays);

		/** To-Date */
		return (Timestamp) cal.getTime();
	}

}
