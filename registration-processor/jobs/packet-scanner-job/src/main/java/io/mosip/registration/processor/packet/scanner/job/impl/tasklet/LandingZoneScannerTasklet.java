package io.mosip.registration.processor.packet.scanner.job.impl.tasklet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class LandingZoneScannerTasklet.
 *
 * @author M1030448
 */
@Component
public class LandingZoneScannerTasklet implements Tasklet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LandingZoneScannerTasklet.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/** The registration status service. */
	@Autowired
	protected RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;


	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The is transaction successful. */
	boolean isTransactionSuccessful = false;

	/** The Constant VIRUS_SCAN_NOT_ACCESSIBLE. */
	private static final String VIRUS_SCAN_NOT_ACCESSIBLE = "The Virus Scan Path set by the System is not accessible";

	/** The Constant ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE. */
	private static final String ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status table is not accessible";

	/**
	 * Executes LandingZoneScannerTasklet to move registration packet from the
	 * landing zone to virus scan folder.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @return RepeatStatus
	 * @throws Exception the exception
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		List<InternalRegistrationStatusDto> getEnrols = new ArrayList<>();
		try {

			getEnrols = this.registrationStatusService
					.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());
			if (!(getEnrols.isEmpty())) {
				getEnrols.forEach(dto -> {
					try {

						this.filemanager.copy(dto.getRegistrationId(), DirectoryPathDto.LANDING_ZONE,
								DirectoryPathDto.VIRUS_SCAN);
						if (this.filemanager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN, dto.getRegistrationId())) {

							dto.setStatusCode(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
							dto.setStatusComment("packet is in status packet for virus scan");
							dto.setUpdatedBy(USER);
							this.registrationStatusService.updateRegistrationStatus(dto);

							this.filemanager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN,
									dto.getRegistrationId());
							isTransactionSuccessful = true;

							LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), "moved successfully to virus scan.");
						}
					} catch (TablenotAccessibleException e) {

						LOGGER.error(LOGDISPLAY, ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE, e);
					} catch (IOException | FileNotFoundInDestinationException e) {

						LOGGER.error(LOGDISPLAY, VIRUS_SCAN_NOT_ACCESSIBLE, e);
					}

				});
			} else if (getEnrols.isEmpty()) {

				LOGGER.info("There are currently no files to be moved");
			}
		} catch (TablenotAccessibleException e) {

			LOGGER.error(LOGDISPLAY, ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}
		finally{

			if (!(getEnrols.isEmpty())) {
				eventId = isTransactionSuccessful ? EventId.RPR_403.toString() : EventId.RPR_405.toString();
				eventName=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventName.DELETE.toString(): EventName.EXCEPTION.toString();	
				eventType=	eventId.equalsIgnoreCase(EventId.RPR_403.toString()) ? EventType.BUSINESS.toString(): EventType.SYSTEM.toString();
			} else if (getEnrols.isEmpty()) {

				eventId = EventId.RPR_401.toString();
				eventName = EventName.GET.toString();
				eventType = EventType.BUSINESS.toString();
			}

			String description = isTransactionSuccessful ? "File moved from landing zone to virusscan zone successfully" : "File moveing from landing zone to virusscan zone failed";

			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,AuditLogConstant.NO_ID.toString());
		}
		return RepeatStatus.FINISHED;
	}

}
