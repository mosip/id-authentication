package io.mosip.registration.processor.packet.decryptor.job.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Scheduler class for Packet Decryption job.
 *
 * @author Jyoti Prakash Nayak
 */
@Component
@EnableScheduling
public class PacketDecryptionScheduler {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecryptionScheduler.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {} - {}";
	
	/** The job launcher. */
	@Autowired
	private JobLauncher jobLauncher;
	
	/** The packet decryptor job. */
	@Autowired
	private Job packetDecryptorJob;

	/**
	 * packetDecryptorJobScheduler runs the packetDecryptorJobJob as per given cron schedule.
	 */
	@Scheduled(cron = "${decryption.cron.job.schedule}")
	public void packetDecryptorJobScheduler() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(packetDecryptorJob, jobParameters);
			
			LOGGER.info(LOGDISPLAY,"Job's status ", jobExecution.getId(),jobExecution.getStatus());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error(LOGDISPLAY,"packetDecryptorJobScheduler failed to execute", e);
		}
	}
}
