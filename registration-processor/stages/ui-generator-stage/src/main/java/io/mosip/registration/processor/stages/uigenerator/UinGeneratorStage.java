package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idgenerator.uin.dto.UinResponseDto;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;


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
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The registration id. */
	private String registrationId = "";

	@Override
	public MessageDTO process(MessageDTO object) {
		this.registrationId = object.getRid();
		System.out.println(this.registrationId);

		try {
			UinResponseDto uinResponseDto=	(UinResponseDto) registrationProcessorRestClientService.getApi(ApiName.UINGENERATOR, null, "", "", UinResponseDto.class);
		System.out.println("our UIN   :   "+uinResponseDto.getUin());
		
		
		} catch (ApisResourceAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
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
