package io.mosip.registration.processor.scanner.virusscanner.config;

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
import io.mosip.registration.processor.scanner.virusscanner.VirusScannerJob;
import io.mosip.registration.processor.scanner.virusscanner.tasklet.VirusScannerTasklet;

/**
 * Configuration class for Packet Scanner Jobs
 * @author M1030448
 *
 */
@Configuration
@EnableBatchProcessing
public class VirusScannerBatchJobConfig implements VirusScannerJob<Job> {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	

	@Autowired
	public VirusScannerTasklet virusScannerTasklet;
	
	

	/**
	 * Step to execute the virusScanStep
	 */
	@Bean
	Step virusScanStep() {
		return stepBuilderFactory.get("virusScanStep").tasklet(virusScannerTasklet).build();
	}

	


	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.packet.scanner.job.PacketScannerJob#virusScannerJob()
	 */
	@Override
	@Bean
	public Job virusScannerJob() {
		return this.jobBuilderFactory.get("virusScannerJob").incrementer(new RunIdIncrementer()).start(virusScanStep())
				.build();
	}
	

}
