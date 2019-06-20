package io.mosip.registration.dao.impl;

import java.sql.Timestamp;
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
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;
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
	private static final Logger LOGGER = AppConfig.getLogger(GlobalParamDAOImpl.class);

	/** The globalParam repository. */
	@Autowired
	private GlobalParamRepository globalParamRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.GlobalParamDAO#getGlobalParams()
	 */
	public Map<String, Object> getGlobalParams() {

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching list of global params");

		List<GlobalParamName> globalParams = globalParamRepository.findByIsActiveTrueAndValIsNotNull();
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParams.forEach(param -> globalParamMap.put(param.getName(), param.getVal() != null ? param.getVal().trim() : param.getVal()));

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "List of global params fetched successfully");

		return globalParamMap;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.GlobalParamDAO#saveAll(java.util.List)
	 */
	@Override
	public void saveAll(List<GlobalParam> globalParamList) {

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Save Global Params started");

		/** Parase List to Iterable */
		Iterable<GlobalParam> globalParamIterableList = globalParamList;

		/** Save all Global Params */
		globalParamRepository.saveAll(globalParamIterableList);

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Save Global Params ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.GlobalParamDAO#get(java.lang.String)
	 */
	@Override
	public GlobalParam get(GlobalParamId globalParamId) {
		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Global Param");
		return globalParamRepository.findById(GlobalParam.class, globalParamId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.GlobalParamDAO#getAll(java.util.List)
	 */
	@Override
	public List<GlobalParam> getAll(List<String> names) {

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get All Global Params");

		return globalParamRepository.findByNameIn(names);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.GlobalParamDAO#getAllEntries()
	 */
	@Override
	public List<GlobalParam> getAllEntries() {
		return globalParamRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.GlobalParamDAO#updateSoftwareUpdateStatus(java.lang.String)
	 */
	@Override
	public GlobalParam updateSoftwareUpdateStatus(boolean isUpdateAvailable,Timestamp timestamp) {

		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating the SoftwareUpdate flag started.");


		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode(RegistrationConstants.ENGLISH_LANG_CODE);

		GlobalParam globalParam = get(globalParamId);

		if (!globalParam.getVal().equalsIgnoreCase(RegistrationConstants.ENABLE)) {

			if (isUpdateAvailable) {
				globalParam.setVal(RegistrationConstants.ENABLE);
			} else {
				globalParam.setVal(RegistrationConstants.DISABLE);
			}
			globalParam.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
			globalParam.setUpdDtimes(timestamp);

		}
		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating the SoftwareUpdate flag ended.");
		return globalParamRepository.update(globalParam);
	}

	@Override
	public GlobalParam update(GlobalParam globalParam) {
		LOGGER.info("REGISTRATION - GLOBALPARAMS - GLOBAL_PARAM_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating global param");
		
		return globalParamRepository.update(globalParam);
		
	}
}
