/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.tasklets;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.batchjob.utils.ExpiredStatusUtil;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * This class is a tasklet of batch job to call expired status service in batch service.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class ExpiredStatusTasklet implements Tasklet {

	@Autowired
	private ExpiredStatusUtil expiredUtil;


	@Value("${mosip.batch.token.authmanager.url}")
	String tokenUrl;
	@Value("${mosip.batch.token.request.id}")
	String id;
	@Value("${mosip.batch.token.authmanager.appId}")
	String appId;
	@Value("${mosip.batch.token.authmanager.userName}")
	String userName;
	@Value("${mosip.batch.token.authmanager.password}")
	String password;

	@Value("${version}")
	String version;

	private Logger log = LoggerConfiguration.logConfig(ExpiredStatusTasklet.class);

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		try {

			expiredUtil.expireAppointments();

		} catch (Exception e) {
			log.error("Expired Status ", " Tasklet ", " encountered exception ", e.getMessage());
			contribution.setExitStatus(new ExitStatus(e.getMessage()));
		}

		return RepeatStatus.FINISHED;
	}

}
