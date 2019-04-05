package io.mosip.registrationprocessor.externalStage.stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registrationprocessor.externalStage.entity.MessageRequestDTO;
import io.vertx.core.json.JsonObject;



/**
 * External stage verticle class
 *
 */
@Service
public class ExternalStage extends MosipVerticleAPIManager{
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ExternalStage.class);
	/** request id */
	private static final String ID="io.mosip.registrationprocessor";
	/** request version */
	private static final String VERSION="1.0";
	/** mosipEventBus */
	private MosipEventBus mosipEventBus;
	/** vertx Cluster Manager Url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	
	
	/**
	 * rest client to send requests
	 */
	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
	/**
	 * method to deploy external stage verticle
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.EXTERNAL_STAGE_BUS_IN,MessageBusAddress.EXTERNAL_STAGE_BUS_OUT);
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		MessageRequestDTO requestdto= new MessageRequestDTO();
		requestdto.setId(ID);
		List<String> list= new ArrayList<String>();
		list.add(object.getRid());
		requestdto.setRequest(list);
		requestdto.setRequesttime(LocalDateTime.now().toString());
		requestdto.setVersion(VERSION);
		try {
			Boolean temp=(Boolean) registrationProcessorRestService.postApi(ApiName.EISERVICE,
					"", "", requestdto, Boolean.class);
			if(temp) {
				object.setIsValid(true);
			}
			else {
				object.setIsValid(false);	
			}
		} catch (ApisResourceAccessException e) {
			object.setIsValid(false);		
			e.printStackTrace();
		}
		JsonObject jsonObject = JsonObject.mapFrom(object);
		jsonObject.put("external", "true");
		
		regProcLogger.debug("", "", "sent to next stage --> ", object.toString());
		return object;
	}
	
}
