package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.mosip.kernel.uingenerator.repository.UinDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UinCountTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(UinCountTasklet.class);

	@Autowired
	private UinDao uinDao;

	@Value("${threshold.uin.count}")
	private long thresholdUINCount;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(UinGeneratorConstants.GENERATE_UIN, countFreeUin() < thresholdUINCount);

		return RepeatStatus.FINISHED;
	}

	/**
	 * @return
	 */
	private long countFreeUin() {
		long freeUIN = uinDao.countFreeUin();
		LOGGER.info("Number of free UINs in database is {}", freeUIN);
		return freeUIN;
	}
}
