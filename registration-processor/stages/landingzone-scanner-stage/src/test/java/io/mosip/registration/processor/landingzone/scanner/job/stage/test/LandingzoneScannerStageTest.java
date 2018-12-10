package io.mosip.registration.processor.landingzone.scanner.job.stage.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.landingzone.scanner.job.stage.LandingzoneScannerStage;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(MockitoJUnitRunner.class)
public class LandingzoneScannerStageTest {

	@InjectMocks
	private LandingzoneScannerStage landingzoneScannerStage;

	@Mock
	private FileManager<DirectoryPathDto, InputStream> filemanager;
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private InternalRegistrationStatusDto dto1;

	private InternalRegistrationStatusDto dto2;

	private List<InternalRegistrationStatusDto> list;
	
	private Logger fooLogger;
	
	private ListAppender<ILoggingEvent> listAppender;
	
	@Before
	public void setup() {
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
		
        fooLogger = (Logger) LoggerFactory.getLogger(LandingzoneScannerStage.class);
        listAppender = new ListAppender<>();
	}
	
	@Test
	public void landingZoneToVirusScanTaskletSuccessTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		list.add(dto1);
		list.add(dto2);
		when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).thenReturn(true);
		doNothing().when(filemanager).cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class),
				any(String.class));

		doNothing().when(registrationStatusService).updateRegistrationStatus(any(InternalRegistrationStatusDto.class));

		MessageDTO msg = new MessageDTO();
		landingzoneScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "1001 - moved successfully to virus scan."),
						Tuple.tuple( Level.INFO, "1002 - moved successfully to virus scan.")); 
		
	}
	
	@Test
	public void noFilesToBeMovedTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);

		MessageDTO msg = new MessageDTO();
		landingzoneScannerStage.process(msg);
		
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.INFO, "There are currently no files to be moved")); 
	}
	
	@Test
	public void registrationStatusServiceFindingEntitiesfailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);

        doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.findbyfilesByThreshold(any(String.class));

  		MessageDTO msg = new MessageDTO();
  		landingzoneScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Enrolment Status table is not accessible - {}")); 

	}
	
	@Test
	public void registrationStatusServiceUpdateEnrolmentfailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
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
		
		MessageDTO msg = new MessageDTO();
		landingzoneScannerStage.process(msg);

		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Enrolment Status table is not accessible - {}")); 
	}
	
	@Test
	public void CopyfailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		list.add(dto1);

		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));

		MessageDTO msg = new MessageDTO();
		landingzoneScannerStage.process(msg);
		
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "The Virus Scan Path set by the System is not accessible - {}")); 
	
	}
	
	@Test
	public void CleanUpfailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.when(filemanager.checkIfFileExists(any(DirectoryPathDto.class), any(String.class))).thenReturn(true);
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager)
				.cleanUpFile(any(DirectoryPathDto.class), any(DirectoryPathDto.class), any(String.class));

			MessageDTO msg = new MessageDTO();
			landingzoneScannerStage.process(msg);

			Assertions.assertThat(listAppender.list)
	        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
			.containsExactly(Tuple.tuple( Level.ERROR, "The Virus Scan Path set by the System is not accessible - {}")); 
	}
	
	@Test
	public void CheckifExistsfailureTest() throws Exception {
		
        listAppender.start();
        fooLogger.addAppender(listAppender);
        
		list.add(dto1);
		Mockito.when(registrationStatusService
				.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString()))
				.thenReturn(list);
		Mockito.doNothing().when(filemanager).copy(any(String.class), any(DirectoryPathDto.class),
				any(DirectoryPathDto.class));
		Mockito.doThrow(FileNotFoundInDestinationException.class).when(filemanager)
				.checkIfFileExists(any(DirectoryPathDto.class), any(String.class));

			MessageDTO msg = new MessageDTO();
			landingzoneScannerStage.process(msg);
			
			Assertions.assertThat(listAppender.list)
	        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
			.containsExactly(Tuple.tuple( Level.ERROR, "The Virus Scan Path set by the System is not accessible - {}")); 
	}

}
