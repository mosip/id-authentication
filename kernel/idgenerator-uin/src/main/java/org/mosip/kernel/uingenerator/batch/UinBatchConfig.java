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

@Configuration
@PropertySource("classpath:uin.properties")
@EnableBatchProcessing
@EnableScheduling
public class UinBatchConfig extends DefaultBatchConfigurer {

	@Override
	public void setDataSource(DataSource dataSource) {
		// override to initialize a Map based JobRepository
	}

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private UinCountTasklet uinCountTasklet;

	@Autowired
	private UinGenerationDecider uinGenDecider;

	@Autowired
	private UinGenerationProcessor uinGenProcessor;

	@Autowired
	private UinGenerationWriter uinGenWriter;

	@Bean
	@StepScope
	public UinGenerationReader reader() {
		return new UinGenerationReader();
	}

	@Bean
	public Job job() {
		JobBuilder jobBuilder = jobBuilderFactory.get(UinGeneratorConstants.UIN_GENERATOR_JOB)
				.incrementer(new RunIdIncrementer());
		Flow uinGenFlow = new FlowBuilder<Flow>(UinGeneratorConstants.UIN_GENERATOR_FLOW).start(uinGenDecider)
				.on(UinGeneratorStatus.DO_UIN_GENERATION.toString()).to(uinGeneratorStep()).from(uinGenDecider)
				.on(UinGeneratorStatus.SKIP_UIN_GENERATION.toString()).end(UinGeneratorStatus.COMPLETED.toString())
				.build();
		FlowJobBuilder builder = jobBuilder.flow(uinCounterStep()).next(uinGenFlow).end();
		return builder.build();
	}

	@Scheduled(cron = "${uin.generation.cron}")
	public void myScheduler() {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(UinGeneratorConstants.TIME, System.currentTimeMillis()).toJobParameters();
		if (isJobNotRunning(job())) {
			try {
				jobLauncher.run(job(), jobParameters);
			} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
					| JobParametersInvalidException e) {
				throw new UinGenerationJobException(UinGeneratorErrorCodes.UIN_GENERATION_JOB_EXCEPTION.getErrorCode(),
						UinGeneratorErrorCodes.UIN_GENERATION_JOB_EXCEPTION.getErrorMessage());
			}
		}
	}

	private Step uinCounterStep() {
		return stepBuilderFactory.get(UinGeneratorConstants.UIN_COUNTER_STEP).tasklet(uinCountTasklet).build();
	}

	private Step uinGeneratorStep() {
		return stepBuilderFactory.get(UinGeneratorConstants.UIN_GENERATOR_STEP).<String, List<UinBean>>chunk(1)
				.reader(reader()).processor(uinGenProcessor).writer(uinGenWriter).build();
	}

	private boolean isJobNotRunning(Job job) {
		Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
		return jobExecutions.isEmpty();
	}
}
