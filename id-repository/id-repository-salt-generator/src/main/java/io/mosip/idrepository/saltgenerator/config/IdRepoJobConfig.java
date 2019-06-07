package io.mosip.idrepository.saltgenerator.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.mosip.idrepository.saltgenerator.entity.SaltEntity;

/**
 * The Class IdRepoJobConfig - provides configuration for Salt generator Job.
 *
 * @author Manoj SP
 */
@Configuration
@DependsOn("idRepoSaltGeneratorConfig")
public class IdRepoJobConfig {

	/** The job builder factory. */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	/** The listener. */
	@Autowired
	private JobExecutionListener listener;
	
	/** The reader. */
	@Autowired
	private ItemReader<SaltEntity> reader;
	
	/** The writer. */
	@Autowired
	private ItemWriter<SaltEntity> writer;
	
	/**
	 * Job.
	 *
	 * @param step the step
	 * @return the job
	 */
	@Bean
	public Job job(Step step) {
		return jobBuilderFactory
				.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step)
				.end()
				.build();
	}
	
	
	/**
	 * Step.
	 *
	 * @return the step
	 */
	@Bean
	public Step step() {
		return stepBuilderFactory
				.get("step")
				.<SaltEntity, SaltEntity> chunk(10)
				.reader(reader)
				.writer(writer)
				.build();
	}
}
