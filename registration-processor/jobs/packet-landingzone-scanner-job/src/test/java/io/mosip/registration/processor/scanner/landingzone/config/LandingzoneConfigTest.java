package io.mosip.registration.processor.scanner.landingzone.config;

import static org.junit.Assert.assertEquals;


import org.springframework.batch.core.repository.JobRestartException;
import org.junit.Test;
import org.junit.runner.RunWith;

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

import io.mosip.registration.processor.scanner.landingzone.PacketLandingzoneScannerJobApplication;
import io.mosip.registration.processor.scanner.landingzone.tasklet.LandingZoneScannerTasklet;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketLandingzoneScannerJobApplication.class)
@ContextConfiguration(classes = { LandingZoneScannerConfig.class })
public class LandingzoneConfigTest {

	@Autowired
	private JobLauncher jobLauncher;	
	
	@Autowired
	private Job landingZoneScannerJob;
	
	@MockBean
	public LandingZoneScannerTasklet landingZoneScannerTasklet; 
	
	@Test
	public void packetDecryptorJobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(landingZoneScannerJob, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
