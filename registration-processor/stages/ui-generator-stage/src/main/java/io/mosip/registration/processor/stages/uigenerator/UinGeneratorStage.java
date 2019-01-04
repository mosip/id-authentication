package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.uingenerator.dto.UinResponseDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.util.UinAvailabilityCheck;



/**
 * The Class UinGeneratorStage.
 * @author M1047487
 */
@Service
public class UinGeneratorStage extends MosipVerticleManager {



	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinGeneratorStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The secs. */
	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;


	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The registration id. */
	private String registrationId = "";

	UinResponseDto uinResponseDto = new UinResponseDto();
	IdResponseDTO idResponseDTO = new IdResponseDTO();


	@Override
	public MessageDTO process(MessageDTO object) {
		String description = "";
		boolean isTransactionSuccessful = false;

		this.registrationId = object.getRid();
		System.out.println(this.registrationId);


		try {

			UinResponseDto uinResponseDto=	(UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "","", UinResponseDto.class);
			System.out.println("UIN GENERATION HAPPENING:    "+uinResponseDto.getUin());
			UinAvailabilityCheck uinAvailabilityCheck = new UinAvailabilityCheck();
			uinAvailabilityCheck.uinCheck("27847657360002520181208094036",adapter);

			demographicDedupeRepository.updateUinWrtRegistraionId("27847657360002520181208094036", uinResponseDto.getUin());





		} 
		catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage()+e.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		}catch (Exception ex) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),registrationId,PlatformErrorMessages.PACKET_DEMO_DEDUPE_FAILED.getMessage()+ex.getMessage());
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		}finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);


		}


		return null;
	}



	private IdResponseDTO sendIdRepoWithUin(boolean uinAvailable,UinResponseDto uinResponseDto) {




		return idResponseDTO;




	}








	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		MessageDTO mm=new MessageDTO();
		mm.setRid("132345");
		this.process(mm);

		//MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		//this.consumeAndSend(mosipEventBus, MessageBusAddress.UIN_GENERATION_BUS_IN, MessageBusAddress.UIN_GENERATION_BUS_OUT);

	}


}
