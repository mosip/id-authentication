package io.mosip.registration.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.RegistrationUserRoleDAO;
import io.mosip.registration.entity.RegistrationUserRole;
import io.mosip.registration.entity.RegistrationUserRoleID;
import io.mosip.registration.repositories.RegistrationUserRoleRepository;

@Repository
public class RegistrationUserRoleDAOImpl implements RegistrationUserRoleDAO {
	
	@Autowired
	private RegistrationUserRoleRepository registrationUserRoleRepository;
	
	public List<String> getRoles(String userId){
		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setUsrId(userId);
		List<RegistrationUserRole> registrationUserRoles = registrationUserRoleRepository.findByRegistrationUserRoleID(registrationUserRoleID);
		List<String> roles = new ArrayList<String>();
		for(int role = 0; role < registrationUserRoles.size(); role++) {
			roles.add(registrationUserRoles.get(role).getRegistrationUserRoleID().getRoleCode());
		}
		return roles;
	}
}
