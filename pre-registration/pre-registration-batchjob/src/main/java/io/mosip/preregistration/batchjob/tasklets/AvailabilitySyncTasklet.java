
/* 
 * Copyright
 * 
 */package io.mosip.preregistration.batchjob.tasklets;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjob.utils.AuthTokenUtil;
import io.mosip.preregistration.batchjob.utils.AvailabilityUtil;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * This class is a tasklet of batch job to call master data sync API in batch service.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class AvailabilitySyncTasklet implements Tasklet {

	@Autowired
	private AvailabilityUtil availabilityUtil;
	
	@Autowired
	private AuthTokenUtil tokenUtil;

	private Logger log = LoggerConfiguration.logConfig(AvailabilitySyncTasklet.class);

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		try {
			HttpHeaders headers=tokenUtil.getTokenHeader();
			
			availabilityUtil.addAvailability(headers);
			

		} catch (Exception e) {
			log.error("Sync master ", " Tasklet ", " encountered exception ", e.getMessage());
			throw e;
		}

		return RepeatStatus.FINISHED;
	}

}
