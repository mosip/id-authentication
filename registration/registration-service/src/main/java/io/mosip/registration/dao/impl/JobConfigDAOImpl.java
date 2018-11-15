package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.JobConfigDAO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.repositories.JobConfigRepository;

/**
 * implementation class of {@link JobConfigDAO}
 * @author Dinesh Ashokan
 *
 */
@Repository
public class JobConfigDAOImpl implements JobConfigDAO{
	
	@Autowired
	private JobConfigRepository jobConfigRepository;
	
	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(JobConfigDAOImpl.class);

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.JobConfigDAO#getJob()
	 */
	@Override
	public List<SyncJob> getAll() {
		try {
			return jobConfigRepository.findAll();
		} catch (RuntimeException runtimeException) {
			System.out.println("***********ERROR****************");
			runtimeException.printStackTrace();
			return null;
		}
	}


	@Override
	public List<SyncJob> getActiveJobs() {
		try {
			return jobConfigRepository.findByIsActiveTrue();
		} catch (RuntimeException runtimeException) {
			System.out.println("***********ERROR****************");
			runtimeException.printStackTrace();
			return null;
		}
	}

}
