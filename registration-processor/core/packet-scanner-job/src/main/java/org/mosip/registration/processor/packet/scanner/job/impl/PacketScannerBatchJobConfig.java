package org.mosip.registration.processor.packet.scanner.job.impl;

import javax.sql.DataSource;

import org.mosip.registration.processor.packet.scanner.job.PacketScannerJob;
import org.mosip.registration.processor.packet.scanner.job.impl.tasklet.LandingZoneScannerTasklet;
import org.mosip.registration.processor.packet.scanner.job.impl.tasklet.VirusScannerTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Packet Scanner Jobs
 * @author M1030448
 *
 */
@Configuration
@EnableBatchProcessing
public class PacketScannerBatchJobConfig implements PacketScannerJob<Job> {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Autowired
	public LandingZoneScannerTasklet landingZoneScannerTasklet;

	@Autowired
	public VirusScannerTasklet virusScannerTasklet;

	/**
	 * Step to execute the virusScanStep
	 */
	@Bean
	Step virusScanStep() {
		return stepBuilderFactory.get("virusScanStep").tasklet(virusScannerTasklet).build();
	}

	/**
	 * Step to execute the landingZoneStep
	 */
	@Bean
	Step landingZoneStep() {
		return stepBuilderFactory.get("landingZoneStep").tasklet(landingZoneScannerTasklet).build();
	}

	/* (non-Javadoc)
	 * @see org.mosip.registration.processor.packet.scanner.job.PacketScannerJob#landingZoneScannerJob()
	 */
	@Bean
	public Job landingZoneScannerJob() {
		return this.jobBuilderFactory.get("landingZoneScannerJob").incrementer(new RunIdIncrementer()).start(landingZoneStep())
				.build();
	}

	/* (non-Javadoc)
	 * @see org.mosip.registration.processor.packet.scanner.job.PacketScannerJob#virusScannerJob()
	 */
	@Override
	@Bean
	public Job virusScannerJob() {
		return this.jobBuilderFactory.get("virusScannerJob").incrementer(new RunIdIncrementer()).start(virusScanStep())
				.build();
	}

}
