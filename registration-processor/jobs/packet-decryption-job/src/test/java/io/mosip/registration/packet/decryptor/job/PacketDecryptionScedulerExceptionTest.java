package io.mosip.registration.packet.decryptor.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.packet.decryptor.job.schedule.PacketDecryptionScheduler;
@RunWith(SpringRunner.class)
public class PacketDecryptionScedulerExceptionTest {
	@InjectMocks
	private PacketDecryptionScheduler packetdecryptionScheduler;
	
	@Mock
	JobLauncher jobLauncher ;
	
	@Mock
	private Job packetDecryptorJob;
	
	@SuppressWarnings("unchecked")
	@Test
	public void JobParametersInvalidExceptionTest() throws Exception {
		
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		
		Mockito.doThrow(JobParametersInvalidException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));
		packetdecryptionScheduler.packetDecryptorJobScheduler();
		
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("packetDecryptorJobScheduler failed to execute");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void JobExecutionAlreadyRunningExceptionTest() throws Exception {
		
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		
		Mockito.doThrow(JobExecutionAlreadyRunningException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));
		packetdecryptionScheduler.packetDecryptorJobScheduler();
		
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("packetDecryptorJobScheduler failed to execute");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void JobRestartExceptionTest() throws Exception {
		
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		
		Mockito.doThrow(JobRestartException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));
		packetdecryptionScheduler.packetDecryptorJobScheduler();
		
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("packetDecryptorJobScheduler failed to execute");
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void JobInstanceAlreadyCompleteExceptionTest() throws Exception {
		
		
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		
		
		Mockito.doThrow(JobInstanceAlreadyCompleteException.class).when(jobLauncher).run(any(Job.class), any(JobParameters.class));
		packetdecryptionScheduler.packetDecryptorJobScheduler();
		
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((ILoggingEvent) argument).getFormattedMessage()
						.contains("packetDecryptorJobScheduler failed to execute");
			}
		}));
	}
}
