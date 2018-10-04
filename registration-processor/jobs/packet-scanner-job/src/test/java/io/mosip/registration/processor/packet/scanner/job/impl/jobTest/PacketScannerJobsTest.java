/*package io.mosip.registration.processor.packet.scanner.job.impl.jobTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import io.mosip.registration.processor.packet.scanner.job.PacketScannerApplication;
import io.mosip.registration.processor.packet.scanner.job.impl.PacketScannerBatchJobConfig;
import io.mosip.registration.processor.packet.scanner.job.impl.tasklet.LandingZoneScannerTasklet;
import io.mosip.registration.processor.packet.scanner.job.impl.tasklet.VirusScannerTasklet;
import org.springframework.batch.core.BatchStatus;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketScannerApplication.class)
@ContextConfiguration(classes = { PacketScannerBatchJobConfig.class })
public class PacketScannerJobsTest {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job landingZoneScannerJob;

	@Autowired
	private Job virusScannerJob;

	@MockBean
	public LandingZoneScannerTasklet landingZoneScannerTasklet;

	@MockBean
	public VirusScannerTasklet virusScannerTasklet;

	@Test
	public void landingZoneScannerJobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(landingZoneScannerJob, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@Test
	public void virusScannerJobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(virusScannerJob, jobParameters);
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
*/