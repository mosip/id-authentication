package io.mosip.kernel.idgenerator.uin.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorConstant;
import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorStatus;

/**
 * Decider to decide the status of the flow whether to generate new uins or not
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerationDecider implements JobExecutionDecider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.job.flow.JobExecutionDecider#decide(org.
	 * springframework.batch.core.JobExecution,
	 * org.springframework.batch.core.StepExecution)
	 */
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		return (Boolean) jobExecution.getExecutionContext().get(UinGeneratorConstant.GENERATE_UIN)
				? new FlowExecutionStatus(UinGeneratorStatus.DO_UIN_GENERATION.toString())
				: new FlowExecutionStatus(UinGeneratorStatus.SKIP_UIN_GENERATION.toString());
	}
}
