package io.mosip.registration.dao.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamDAO;
import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.repositories.GlobalParamRepository;

/**
 * The implementation class of {@link GlobalParamDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class GlobalParamDAOImpl implements GlobalParamDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationAppLoginDAOImpl.class);

	/** The globalParam repository. */
	@Autowired
	private GlobalParamRepository globalParamRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.GlobalParamDAO#getGlobalParams()
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.debug("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching list of global params");

		List<GlobalParamName> globalParams = globalParamRepository.findByIsActiveTrue();
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParams.forEach(param -> globalParamMap.put(param.getName(), param.getVal()));

		LOGGER.debug("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "List of global params fetched successfully");

		return globalParamMap;
	}
}
