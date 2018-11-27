package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.SyncJobConfigDAO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.repositories.JobConfigRepository;

/**
 * implementation class of {@link SyncJobConfigDAO}
 * 
 * @author Dinesh Ashokan
 *
 */
@Repository
public class JobConfigDAOImpl implements SyncJobConfigDAO {

	@Autowired
	private JobConfigRepository jobConfigRepository;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(JobConfigDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.JobConfigDAO#getJob()
	 */
	@Override
	public List<SyncJobDef> getAll() {
		return jobConfigRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.SyncJobConfigDAO#getActiveJobs()
	 */
	@Override
	public List<SyncJobDef> getActiveJobs() {
		return jobConfigRepository.findByIsActiveTrue();
	}

}
