package org.mosip.registration.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.mosip.registration.dao.RegistrationUserRoleDAO;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.mosip.registration.repositories.RegistrationUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationUserRoleDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationUserRoleDAOImpl implements RegistrationUserRoleDAO {
	
	/** The registrationUserRole repository. */
	@Autowired
	private RegistrationUserRoleRepository registrationUserRoleRepository;
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationUserRoleDAO#getRoles(java.lang.String)
	 */
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
