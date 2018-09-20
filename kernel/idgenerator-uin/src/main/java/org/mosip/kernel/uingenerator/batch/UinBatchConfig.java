package org.mosip.kernel.uingenerator.batch;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.mosip.kernel.uingenerator.constants.UinGeneratorErrorCodes;
import org.mosip.kernel.uingenerator.constants.UinGeneratorStatus;
import org.mosip.kernel.uingenerator.exception.UinGenerationJobException;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Enable Uin generator batch job and Spring's scheduled task execution
 * capability. Provides configuration for setting up the job.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
@PropertySource("classpath:uin.properties")
@EnableBatchProcessing
@EnableScheduling
public class UinBatchConfig extends DefaultBatchConfigurer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.configuration.annotation.
	 * DefaultBatchConfigurer#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		// override to initialize a Map based JobRepository
	}

	/**
	 * Field for a JobBuilder which sets the JobRepository automatically
	 */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/**
	 * Field for a StepBuilder which sets the JobRepository and
	 * PlatformTransactionManager automatically.
	 */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/**
	 * Entry point for browsing executions of running or historical jobs and steps.
	 */
	@Autowired
	private JobExplorer jobExplorer;

	/**
	 * Field for a interface for controlling jobs
	 */
	@Autowired
	private JobLauncher jobLauncher;

	/**
	 * Tasklet having functionality to count number of free uins in the database
	 */
	@Autowired
	private UinCountTasklet uinCountTasklet;

	/**
	 * Uin Generation Decider is used to decide whether to generate new uins or not
	 */
	@Autowired
	private UinGenerationDecider uinGenDecider;

	/**
	 * Uin Generation Processor is used to generate list of uins
	 */
	@Autowired
	private UinGenerationProcessor uinGenProcessor;

	/**
	 * Uin Generation Writer is used to persist uins to database
	 */
	@Autowired
	private UinGenerationWriter uinGenWriter;

	/**
	 * Uin Generation Reader is used to initialize the process for uin generation
	 * job
	 * 
	 * @return The Uin Generation Reader instance
	 */
	@Bean
	@StepScope
	public UinGenerationReader reader() {
		return new UinGenerationReader();
	}

	/**
	 * Builds the uin generator job with sequence of steps
	 * 
	 * @return The job instance
	 */
	@Bean
	public Job uinGeneratorJob() {
		JobBuilder jobBuilder = jobBuilderFactory.get(UinGeneratorConstants.UIN_GENERATOR_JOB)
				.incrementer(new RunIdIncrementer());
		Flow uinGenFlow = new FlowBuilder<Flow>(UinGeneratorConstants.UIN_GENERATOR_FLOW).start(uinGenDecider)
				.on(UinGeneratorStatus.DO_UIN_GENERATION.toString()).to(uinGeneratorStep()).from(uinGenDecider)
				.on(UinGeneratorStatus.SKIP_UIN_GENERATION.toString()).end(UinGeneratorStatus.COMPLETED.toString())
				.build();
		FlowJobBuilder builder = jobBuilder.flow(uinCounterStep()).next(uinGenFlow).end();
		return builder.build();
	}

	/**
	 * The Uin generator job scheduler
	 */
	@Scheduled(cron = "${uin.generation.cron}")
	public void uinGeneratorScheduler() {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(UinGeneratorConstants.TIME, System.currentTimeMillis()).toJobParameters();
		if (isJobNotRunning(uinGeneratorJob())) {
			try {
				jobLauncher.run(uinGeneratorJob(), jobParameters);
			} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
					| JobParametersInvalidException e) {
				throw new UinGenerationJobException(UinGeneratorErrorCodes.UIN_GENERATION_JOB_EXCEPTION.getErrorCode(),
						UinGeneratorErrorCodes.UIN_GENERATION_JOB_EXCEPTION.getErrorMessage());
			}
		}
	}

	/**
	 * Step to get number of free uins in database
	 * 
	 * @return The Uin counter step
	 */
	private Step uinCounterStep() {
		return stepBuilderFactory.get(UinGeneratorConstants.UIN_COUNTER_STEP).tasklet(uinCountTasklet).build();
	}

	/**
	 * Step to generate and save uins in database
	 * 
	 * @return The uin generator step
	 */
	private Step uinGeneratorStep() {
		return stepBuilderFactory.get(UinGeneratorConstants.UIN_GENERATOR_STEP).<String, List<UinBean>>chunk(1)
				.reader(reader()).processor(uinGenProcessor).writer(uinGenWriter).build();
	}

	/**
	 * Check whether a job is already running
	 * 
	 * @param job
	 *            The uin generator job
	 * @return true if job is not running
	 */
	private boolean isJobNotRunning(Job job) {
		Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
		return jobExecutions.isEmpty();
	}
}
