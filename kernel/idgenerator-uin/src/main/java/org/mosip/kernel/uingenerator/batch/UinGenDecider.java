package org.mosip.kernel.uingenerator.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;

public class UinGenDecider implements JobExecutionDecider {
	private static final Logger log = LoggerFactory.getLogger(UinGenDecider.class);

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		ExecutionContext jobContext = jobExecution.getExecutionContext();

		boolean uinGen = (Boolean) jobContext.get("doUINGeneration");
		if (uinGen) {
			log.info("Will run UIN Generation");
			return new FlowExecutionStatus("DO_UIN_GENERATION");
		}

		log.info("Skip UIN Generation");
		return new FlowExecutionStatus("SKIP_UIN_GENERATION");
	}
}
