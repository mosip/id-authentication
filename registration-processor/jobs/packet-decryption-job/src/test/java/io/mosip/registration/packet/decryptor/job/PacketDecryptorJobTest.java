package io.mosip.registration.packet.decryptor.job;

import static org.junit.Assert.assertEquals;


import org.springframework.batch.core.repository.JobRestartException;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.mosip.registration.processor.packet.decryptor.job.PacketDecryptorJobApplication;
import io.mosip.registration.processor.packet.decryptor.job.config.PacketDecryptorBatchConfig;
import io.mosip.registration.processor.packet.decryptor.job.tasklet.PacketDecryptorTasklet;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketDecryptorJobApplication.class)
@ContextConfiguration(classes = { PacketDecryptorBatchConfig.class })
public class PacketDecryptorJobTest {

	@Autowired
	private JobLauncher jobLauncher;	
	
	@Autowired
	private Job packetDecryptorJob;	
	
	@MockBean
	public PacketDecryptorTasklet packetDecryptorTasklet;
	
	@Test
	public void packetDecryptorJobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(packetDecryptorJob, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
