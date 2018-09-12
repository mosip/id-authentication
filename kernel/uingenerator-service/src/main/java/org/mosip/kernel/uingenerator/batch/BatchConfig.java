package org.mosip.kernel.uingenerator.batch;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class BatchConfig extends DefaultBatchConfigurer {

	private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

	@Override
	public void setDataSource(DataSource dataSource) {
		// override to do not set datasource even if a datasource exist.
		// initialize will use a Map based JobRepository (instead of database)
	}

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UinCountTasklet uinCountTasklet;

	@Autowired
	private UinGenProcessor uinGenProcessor;

	@Bean
	public Job job() {
		JobBuilder jobBuilder = jobBuilderFactory.get("job").incrementer(new RunIdIncrementer());
		UinGenDecider uinGenDecider = new UinGenDecider();
		Flow uinGenFlow = new FlowBuilder<Flow>("uin_gen_flow").start(uinGenDecider).on("DO_UIN_GENERATION").to(step2())
				.from(uinGenDecider).on("SKIP_UIN_GENERATION").end("COMPLETED").build();
		FlowJobBuilder builder = jobBuilder.flow(step1()).next(uinGenFlow).end();
		return builder.build();
	}

	private Step step1() {
		return stepBuilderFactory.get("step1").tasklet(uinCountTasklet).build();
	}

	private Step step2() {
		return stepBuilderFactory.get("step2").chunk(1).reader(reader()).processor(uinGenProcessor).writer(writer())
				.build();
	}

	@Bean
	@StepScope
	public UinGenReader reader() {
		return new UinGenReader();
	}

	@Bean
	@Transactional
	public UinGenWriter writer() {
		UinGenWriter hibernateItemWriter = new UinGenWriter(entityManager);
		hibernateItemWriter.setSessionFactory(getSession().getSessionFactory());
		return hibernateItemWriter;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	@Scheduled(cron = "${uin.generation.cron}")
	public void myScheduler() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();

		try {

			JobExecution jobExecution = jobLauncher.run(job(), jobParameters);
			log.info("Job's status {}", jobExecution.getStatus());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			log.error("error {}", e);
		}
	}

}
