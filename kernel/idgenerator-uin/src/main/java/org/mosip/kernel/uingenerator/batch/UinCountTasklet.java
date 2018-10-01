package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.constant.UinGeneratorConstants;
import org.mosip.kernel.uingenerator.repository.UinRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Tasklet that contains function to count number of uins in database
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinCountTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(UinCountTasklet.class);

	/**
	 * Field for {@link #uinDao}
	 */
	@Autowired
	private UinRepository uinDao;

	/**
	 * Long field for uin threshold cocunt
	 */
	@Value("${threshold.uin.count}")
	private long thresholdUINCount;

	/**
	 * Based on number of free uins in db, checks whether to generate new uins or
	 * not
	 * 
	 * @param contribution
	 *            Represents a contribution to a StepExecution, buffering changes
	 *            until they can be applied at a chunk boundary.
	 * @param chunkContext
	 *            Context object for data stored for the duration of a chunk
	 * @return The status
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.put(UinGeneratorConstants.GENERATE_UIN, countFreeUin() < thresholdUINCount);

		return RepeatStatus.FINISHED;
	}

	/**
	 * Count number of free uins in database
	 * 
	 * @return the count of free uins
	 */
	private long countFreeUin() {
		long freeUIN = uinDao.countFreeUin();
		LOGGER.info("Number of free UINs in database is {}", freeUIN);
		return freeUIN;
	}
}
