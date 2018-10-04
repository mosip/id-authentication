package io.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.RegistrationAppLoginDAO;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.repositories.RegistrationAppLoginRepository;


/**
 * The implementation class of {@link RegistrationAppLoginDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationAppLoginDAOImpl implements RegistrationAppLoginDAO{

	/** The registrationAppLogin repository. */
	@Autowired
	private RegistrationAppLoginRepository registrationAppLoginRepository;
		
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public Map<String,Object> getModesOfLogin(){
		
		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq();
		Map<String,Object> loginModes = new LinkedHashMap<String,Object>();
		for(int mode = 0; mode < loginList.size(); mode++) {
			loginModes.put(""+loginList.get(mode).getMethodSeq(), loginList.get(mode).getRegistrationAppLoginMethodID().getLoginMethod());
		}
		loginModes.put("sequence", 1);
		return loginModes;
	}
}
