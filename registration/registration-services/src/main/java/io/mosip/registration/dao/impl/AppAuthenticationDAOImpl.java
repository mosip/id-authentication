package io.mosip.registration.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AppAuthenticationDAO;
import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.dao.AppRolePriorityDetails;
import io.mosip.registration.repositories.AppAuthenticationRepository;
import io.mosip.registration.repositories.AppRolePriorityRepository;

/**
 * The implementation class of {@link AppAuthenticationDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class AppAuthenticationDAOImpl implements AppAuthenticationDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AppAuthenticationDAOImpl.class);

	/** The AppAuthentication repository. */
	@Autowired
	private AppAuthenticationRepository appAuthenticationRepository;
	
	/** The AppRolePriority repository. */
	@Autowired
	private AppRolePriorityRepository appRolePriorityRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationAppLoginDAO#getModesOfLogin()
	 */
	public List<String> getModesOfLogin(String authType, Set<String> roleList) {

		LOGGER.info("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching list of login modes");
		
		Set<String> role = new HashSet<>();
		
		if(roleList.size() == RegistrationConstants.PARAM_ONE) {
			role.add(roleList.iterator().next());
		} else if(roleList.size() > RegistrationConstants.PARAM_ONE){
			List<AppRolePriorityDetails> appRolePriorityDetails = appRolePriorityRepository.findByAppRolePriorityIdProcessIdAndAppRolePriorityIdRoleCodeInOrderByPriority(authType, roleList);
			role.add(!appRolePriorityDetails.isEmpty() ? String.valueOf(appRolePriorityDetails.get(RegistrationConstants.PARAM_ZERO).getAppRolePriorityId().getRoleCode()) : "");
		}
		
		if(!authType.equalsIgnoreCase(ProcessNames.LOGIN.getType())) {
			role.add("*");
		}

		List<AppAuthenticationDetails> loginList = appAuthenticationRepository
				.findByIsActiveTrueAndAppAuthenticationMethodIdProcessIdAndAppAuthenticationMethodIdRoleCodeInOrderByMethodSequence(authType, role);
		
		LOGGER.info("REGISTRATION - LOGINMODES - REGISTRATION_APP_LOGIN_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "List of login modes fetched successfully");
		
		return loginList.stream().map(loginMethod -> loginMethod.getAppAuthenticationMethodId().getAuthMethodCode()).collect(Collectors.toList());
	}
}
