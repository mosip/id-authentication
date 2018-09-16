package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.mosip.kernel.uingenerator.constants.UinGeneratorStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
public class UinGenerationDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		return (Boolean) jobExecution.getExecutionContext().get(UinGeneratorConstants.GENERATE_UIN)
				? new FlowExecutionStatus(UinGeneratorStatus.DO_UIN_GENERATION.toString())
				: new FlowExecutionStatus(UinGeneratorStatus.SKIP_UIN_GENERATION.toString());
	}
}
