package io.mosip.registration.processor.landingzone.scanner.job.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Service
public class LandingzoneScannerStage extends MosipVerticleManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(LandingzoneScannerStage.class);

	private static final String USER = "MOSIP_SYSTEM";

	private static final String LOGDISPLAY = "{} - {}";

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	@Autowired
	protected RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private static final String VIRUS_SCAN_NOT_ACCESSIBLE = "The Virus Scan Path set by the System is not accessible";
	private static final String ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = "The Enrolment Status table is not accessible";

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		mosipEventBus.getEventbus().setPeriodic(secs * 1000, msg -> {
			process(new MessageDTO());
			this.send(mosipEventBus, MessageBusAddress.LANDING_ZONE_BUS_OUT, new MessageDTO());
		}

		);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		List<InternalRegistrationStatusDto> getEnrols = new ArrayList<>();
		try {

			getEnrols = this.registrationStatusService
					.findbyfilesByThreshold(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE.toString());

			if (!(getEnrols.isEmpty())) {
				getEnrols.forEach(dto -> {
					String description = "";
					boolean isTransactionSuccessful = false;
					String registrationId = dto.getRegistrationId();
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
							description = registrationId + "moved successfully to virus scan.";
							LOGGER.info(LOGDISPLAY, dto.getRegistrationId(), "moved successfully to virus scan.");
						}
					} catch (TablenotAccessibleException e) {
						LOGGER.error(LOGDISPLAY, ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE, e);
						description = "Registration status table not accessible for packet " + registrationId;
					} catch (IOException | FileNotFoundInDestinationException e) {
						LOGGER.error(LOGDISPLAY, VIRUS_SCAN_NOT_ACCESSIBLE, e);
						description = "Virus scan path set by the system is not accessible for packet "
								+ registrationId;
					} finally {

						String eventId = "";
						String eventName = "";
						String eventType = "";
						eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
						eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
								: EventName.EXCEPTION.toString();
						eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
								: EventType.SYSTEM.toString();

						auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
								registrationId);

					}

				});
			} else if (getEnrols.isEmpty()) {

				LOGGER.info("There are currently no files to be moved");
			}
		} catch (TablenotAccessibleException e) {

			LOGGER.error(LOGDISPLAY, ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}
		return object;
	}

}
