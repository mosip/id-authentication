package io.mosip.registration.processor.scanner.landingzone.tasklet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class LandingZoneScannerTaskletTest {

	@InjectMocks
	private LandingZoneScannerTasklet landingZoneToVirusScanTasklet;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> filemanager;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@MockBean
	private StepContribution stepContribution;

	@MockBean
	private ChunkContext chunkContext;

	private InternalRegistrationStatusDto dto1;

	private InternalRegistrationStatusDto dto2;

	private List<InternalRegistrationStatusDto> list;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		dto1 = new InternalRegistrationStatusDto();
		dto1.setRegistrationId("1001");
		dto1.setStatusComment("landingZone");
		dto1.setRetryCount(0);
		dto1.setCreateDateTime(null);
		dto1.setUpdateDateTime(null);

		dto2 = new InternalRegistrationStatusDto();
		dto2.setRegistrationId("1002");
		dto2.setStatusComment("landingZone");
		dto2.setRetryCount(0);
		dto2.setCreateDateTime(null);
		dto2.setUpdateDateTime(null);

		list = new ArrayList<InternalRegistrationStatusDto>();
		AuditResponseDto auditResponseDto=new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder("test case description",EventId.RPR_401.toString(),EventName.ADD.toString(),EventType.BUSINESS.toString(), "1234testcase");
		
		/*AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		AuditRequestDto auditRequest1 = new AuditRequestDto();

		Field f = CoreAuditRequestBuilder.class.getDeclaredField("auditRequestBuilder");
		f.setAccessible(true);
		f.set(coreAuditRequestBuilder, auditRequestBuilder);
		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		f1.setAccessible(true);
		f1.set(auditRequestBuilder, auditRequest1);*/

	}

	@Test
	public void landingZoneToVirusScanTaskletSuccessTest() throws Exception {
		list.add(dto1);
		list.add(dto2);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).thenReturn(true);
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class));

		Mockito.doNothing().when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		RepeatStatus status = landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void noFilesToBeMovedTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);

		RepeatStatus status = landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		Assert.assertEquals(RepeatStatus.FINISHED, status);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("There are currently no files to be moved");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void registrationStatusServiceFindingEntitiesfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);

		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.findbyfilesByThreshold(any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Enrolment Status table is not accessible");
			}
		}));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void registrationStatusServiceUpdateEnrolmentfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).thenReturn(true);
		Mockito.doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class));
		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.updateRegistrationStatus(any(InternalRegistrationStatusDto.class));
		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Enrolment Status table is not accessible");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void CopyfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);

		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doThrow(IOException.class).when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);
		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void CleanUpfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).thenReturn(true);
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager)
				.cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class), any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void CheckifExistsfailureTest() throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
		when(mockAppender.getName()).thenReturn("MOCK");
		root.addAppender(mockAppender);
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager)
				.checkIfFileExists(any(DirectoryPathDto.class), any(String.class));

		landingZoneToVirusScanTasklet.execute(stepContribution, chunkContext);

		verify(mockAppender).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
			@Override
			public boolean matches(final ILoggingEvent argument) {
				return ((LoggingEvent) argument).getFormattedMessage()
						.contains("The Virus Scan Path set by the System is not accessible");
			}
		}));
	}
}
