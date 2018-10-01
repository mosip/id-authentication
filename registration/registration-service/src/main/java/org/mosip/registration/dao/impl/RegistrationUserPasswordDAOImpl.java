package org.mosip.registration.dao.impl;

import java.util.List;

import org.mosip.registration.dao.RegistrationUserPasswordDAO;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationUserPasswordDAOImpl  implements RegistrationUserPasswordDAO {
	
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
	
	public String getPassword(String userId, String hashPassword) {
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		registrationUserPasswordID.setUsrId(userId);
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPassword = registrationUserPasswordRepository.findByRegistrationUserPasswordID(registrationUserPasswordID);
		String password = "";
		if(registrationUserPassword.size() > 0) {
			password = registrationUserPassword.get(0).getRegistrationUserPasswordID().getPwd();
		}
		return password;
	}

}
