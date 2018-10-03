package org.mosip.registration.dao.impl;

import java.util.List;

import org.mosip.registration.dao.RegistrationUserPasswordDAO;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationUserPasswordDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserPasswordDAOImpl  implements RegistrationUserPasswordDAO {
	
	/** The registrationUserPassword repository. */
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
		
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationUserPasswordDAO#getPassword(java.lang.String,java.lang.String)
	 */
	public String getPassword(String userId, String hashPassword) {
		
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		registrationUserPasswordID.setUsrId(userId);
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPassword = registrationUserPasswordRepository.findByRegistrationUserPasswordID(registrationUserPasswordID);
		String userData = "";
		if(!registrationUserPassword.isEmpty()) {
			userData = registrationUserPassword.get(0).getRegistrationUserPasswordID().getPwd();
		}
		
		return userData;
	}

}
