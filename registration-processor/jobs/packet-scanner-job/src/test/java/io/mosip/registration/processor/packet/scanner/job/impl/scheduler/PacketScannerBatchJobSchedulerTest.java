package io.mosip.registration.processor.packet.scanner.job.impl.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.packet.scanner.job.PacketScannerApplication;

@RefreshScope
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketScannerApplication.class)
public class PacketScannerBatchJobSchedulerTest {

	@InjectMocks
	private PacketScannerBatchJobScheduler packetScannerBatchJobScheduler;
	@SpyBean
	private PacketScannerBatchJobScheduler packetScannerBatchJobSchedulerJob;

	@Mock
	JobLauncher jobLauncher;

	@Mock
	private Job packetDecryptorJob;

	@Mock
	private Job landingZoneScannerJob;

	@Mock
	private Job virusScannerJob;

	@Mock
	private Job ftpScannerJob;

	final Appender<ILoggingEvent> mockAppender = mock(Appender.class);

	@Before
	public void setup() {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
	}

	/*@Test
	public void testLandingZoneScannerJobScheduler() {
		Awaitility.await().untilAsserted(
				() -> verify(packetScannerBatchJobSchedulerJob, times(1)).landingZoneScannerJobScheduler());
	}
     */
	/*
	 * @Test public void testVirusScannerJobScheduler() { Awaitility.await()
	 * .untilAsserted(() -> verify(packetScannerBatchJobSchedulerJob,
	 * times(1)).virusScannerJobScheduler()); }
	 * 
	 * @Test public void testFtpJobScheduler() { Awaitility.await().untilAsserted(()
	 * -> verify(packetScannerBatchJobSchedulerJob, times(1)).ftpJobScheduler()); }
	 */

	@Test
	public void testInvalidJobParameters() throws Exception {
		Mockito.doThrow(JobParametersInvalidException.class).when(jobLauncher).run(any(Job.class),
				any(JobParameters.class));
		packetScannerBatchJobScheduler.landingZoneScannerJobScheduler();

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("landingZoneScannerJobScheduler failed to execute");
			}
		}));
	}

	@Test
	public void testJobIsAlreadyRunning() throws Exception {
		Mockito.doThrow(JobExecutionAlreadyRunningException.class).when(jobLauncher).run(any(Job.class),
				any(JobParameters.class));
		packetScannerBatchJobScheduler.landingZoneScannerJobScheduler();

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("landingZoneScannerJobScheduler failed to execute");
			}
		}));
	}

	@Test
	public void testJobRestartFailure() throws Exception {
		Mockito.doThrow(JobRestartException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));

		packetScannerBatchJobScheduler.landingZoneScannerJobScheduler();

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("landingZoneScannerJobScheduler failed to execute");
			}
		}));
	}

	@Test
	public void testJobAlreadyCompletedExecution() throws Exception {

		Mockito.doThrow(JobInstanceAlreadyCompleteException.class).when(jobLauncher).run(any(Job.class),
				any(JobParameters.class));
		packetScannerBatchJobScheduler.landingZoneScannerJobScheduler();

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("landingZoneScannerJobScheduler failed to execute");
			}
		}));
	}

}
