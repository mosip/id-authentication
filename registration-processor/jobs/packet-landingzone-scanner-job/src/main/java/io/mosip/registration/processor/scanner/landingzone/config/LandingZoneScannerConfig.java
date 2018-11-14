package io.mosip.registration.processor.scanner.landingzone.config;

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


import io.mosip.registration.processor.scanner.landingzone.LandingzoneScannerJob;
import io.mosip.registration.processor.scanner.landingzone.tasklet.LandingZoneScannerTasklet;

/**
 * Configuration class for Packet Scanner Jobs
 * @author M1030448
 *
 */
@Configuration
@EnableBatchProcessing
public class LandingZoneScannerConfig implements LandingzoneScannerJob<Job>  {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Autowired
	public LandingZoneScannerTasklet landingZoneScannerTasklet;

	

	/**
	 * Step to execute the landingZoneStep
	 */
	@Bean
	Step landingZoneStep() {
		return stepBuilderFactory.get("landingZoneStep").tasklet(landingZoneScannerTasklet).build();
	}
	
	
	


	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.packet.scanner.job.PacketScannerJob#landingZoneScannerJob()
	 */
	@Bean
	public Job landingZoneScannerJob() {
		return this.jobBuilderFactory.get("landingZoneScannerJob").incrementer(new RunIdIncrementer()).start(landingZoneStep())
				.build();
	}

	

}
