package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.repository.UinDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UinCountTasklet implements Tasklet {
	private static final Logger log = LoggerFactory.getLogger(UinCountTasklet.class);

	@Autowired
	private UinDao uinDao;

	@Value("${threshold.uin.count}")
	private long thresholdUINCount;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		long freeUIN = countfreeUIN();
		log.info("Number of free UINs in database is {}", freeUIN);
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobContext = jobExecution.getExecutionContext();
		if (freeUIN < thresholdUINCount) {
			jobContext.put("doUINGeneration", true);
		} else {
			jobContext.put("doUINGeneration", false);
		}
		return RepeatStatus.FINISHED;
	}

	public int countfreeUIN() {
		return uinDao.countFreeUin(false);
	}

}
