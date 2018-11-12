package io.mosip.registration.processor.scanner.ftp.config;

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

import io.mosip.registration.processor.scanner.ftp.PacketScannerJob;
import io.mosip.registration.processor.scanner.ftp.tasklet.FTPScannerTasklet;



/**
 * Configuration class for Packet Scanner Jobs
 * @author M1030448
 *
 */
@Configuration
@EnableBatchProcessing
public class FTPScannerConfig implements PacketScannerJob<Job> {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	
	
	@Autowired
	public FTPScannerTasklet ftpScannerTasklet;

	
	/**
	 * Step to execute the ftpzone
	 */
	@Bean
	Step ftpZoneStep() {
		return stepBuilderFactory.get("ftpZoneStep").tasklet(ftpScannerTasklet).build();
	}


	
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.packet.scanner.job.PacketScannerJob#ftpScannerJob()
	 */
	@Override
	@Bean
	public Job ftpScannerJob() {
		return this.jobBuilderFactory.get("ftpScannerJob").incrementer(new RunIdIncrementer()).start((ftpZoneStep()))
				.build();
	}

}
