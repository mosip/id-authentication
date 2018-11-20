package io.mosip.registration.processor.packet.decryptor.job.config;

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

import io.mosip.registration.processor.packet.decryptor.job.PacketDecryptorJob;
import io.mosip.registration.processor.packet.decryptor.job.tasklet.PacketDecryptorTasklet;
/**
 * Spring batch configuration for packet decryption job.
 *
 * @author Jyoti Prakash Nayak
 */
@Configuration
@EnableBatchProcessing
public class PacketDecryptorBatchConfig implements PacketDecryptorJob<Job> {
	
	/** The job builder factory. */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/** The data source. */
	@Autowired
	public DataSource dataSource;
	
	/** The packet decryptor tasklet. */
	@Autowired
	public PacketDecryptorTasklet packetDecryptorTasklet;
	
	/**
	 * Step to execute the packetDecrypterTasklet.
	 *
	 * @return the step
	 */
	@Bean
	Step packetDecryptorStep() {
		return stepBuilderFactory.get("packetDecryptorStep").tasklet(packetDecryptorTasklet).build();
	}

	
	
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.packet.decryptor.job.PacketDecryptorJob#packetDecryptorJob()
	 */
	@Override
	@Bean
	public Job packetDecryptorJob() {
		return this.jobBuilderFactory.get("packetDecryptorJob").incrementer(new RunIdIncrementer()).start(packetDecryptorStep())
				.build();
	}

}
