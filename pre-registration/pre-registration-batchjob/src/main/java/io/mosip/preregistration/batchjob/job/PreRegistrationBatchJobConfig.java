/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.preregistration.batchjob.tasklets.AvailabilitySyncTasklet;
import io.mosip.preregistration.batchjob.tasklets.ConsumedStatusTasklet;
import io.mosip.preregistration.batchjob.tasklets.ExpiredStatusTasklet;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Configuration
@EnableBatchProcessing
public class PreRegistrationBatchJobConfig {
	
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ConsumedStatusTasklet consumedStatusTasklet;
	
	@Autowired
	private AvailabilitySyncTasklet availabilitySyncTasklet;
	
	@Autowired
	private ExpiredStatusTasklet expiredStatusTasklet;
	
	
	@Bean
	public Step consumedStatusStep() {
		return stepBuilderFactory.get("consumedStatusStep").tasklet(consumedStatusTasklet).build();
	}
	
	@Bean
	public Step availabilitySyncStep() {
		return stepBuilderFactory.get("availabilitySyncStep").tasklet(availabilitySyncTasklet).build();
	}
	
	@Bean
	public Step expiredStatusStep() {
		return stepBuilderFactory.get("expiredStatusStep").tasklet(expiredStatusTasklet).build();
	}
	
	@Bean
	public Job availabilitySyncJob(){
		return this.jobBuilderFactory.get("availabilitySyncJob")
				   .incrementer(new RunIdIncrementer())
				   .start(availabilitySyncStep())
				   .build();
	}
	@Bean
	public Job consumedStatusJob() {
		return this.jobBuilderFactory.get("consumedStatusJob")
				   .incrementer(new RunIdIncrementer())
				   .start(consumedStatusStep())
				   .build();
	}
	
	@Bean
	public Job expiredStatusJob() {
		return this.jobBuilderFactory.get("expiredStatusJob")
				   .incrementer(new RunIdIncrementer())
				   .start(expiredStatusStep())
				   .build();
	}
}
