package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.dao.JobConfigDAO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.exception.RegBaseUncheckedException;
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
	private static MosipLogger LOGGER;

	
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
