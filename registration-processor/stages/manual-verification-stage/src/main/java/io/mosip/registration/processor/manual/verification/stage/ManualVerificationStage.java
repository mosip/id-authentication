package io.mosip.registration.processor.manual.verification.stage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.service.CustomEnvironment;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
	
/**
 * This class sends message to next stage after successful completion of manual verification.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@Component
public class ManualVerificationStage extends MosipVerticleAPIManager  implements ApplicationContextAware{
	
	/*@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	@Value("${server.port}")
	private int serverPort;*/

	@Autowired
	private ManualVerificationService manualAdjudicationService;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;
	
	private ApplicationContext context;

	private CustomEnvironment env;
	/**
	 * Deploy stage.
	 */
	public void deployStage() {
		env = context.getBean(CustomEnvironment.class);
		System.out.println("SERVER-PORT:-------=========================--------->" + env.getServerPort());
		if (this.mosipEventBus == null) {
			this.mosipEventBus = this.getEventBus(this, env.getClusterManagerUrl());
		}
		//this.mosipEventBus.getEventbus().deployVerticle(this);
	}

@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		System.out.println("++++++++++++++++++"+env.getServerPort());
		this.createServer(router, env.getServerPort());
	}

	private void routes(Router router) {
		System.out.println("Test inser routes----------------------");
		router.post("/v0.1/registration-processor/manual-verification/applicantBiometric").handler(ctx -> {
			processBiometric(ctx);
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/applicantDemographic").handler(ctx -> {
			processDemographic(ctx);
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/assignment").handler(ctx -> {
			processAssignment(ctx);
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/decision").handler(ctx -> {
			processDecision(ctx);
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/packetInfo").handler(ctx -> {
			processPacketInfo(ctx);
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

	}

	public void processBiometric(RoutingContext ctx) {
		JsonObject obj =ctx.getBodyAsJson();
		System.out.println(obj.toString());
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(obj.getString("regId"), obj.getString("fileName"));
		System.out.println(packetInfo);
		if(packetInfo!=null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	public void processDemographic(RoutingContext ctx) {
		JsonObject obj =ctx.getBodyAsJson();
		System.out.println(obj.toString());
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(obj.getString("regId"), PacketFiles.DEMOGRAPHICINFO.name());
		System.out.println(packetInfo);
		if(packetInfo!=null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	public void processAssignment(RoutingContext ctx) {
		JsonObject obj =ctx.getBodyAsJson();
		UserDto userDto = new UserDto();
		userDto.setUserId(obj.getString("userId"));
		System.out.println(obj.toString());
		ManualVerificationDTO  manualVerificationDTO  = manualAdjudicationService.assignApplicant(userDto);
		if(manualVerificationDTO!=null) {
			this.setResponse(ctx, manualVerificationDTO);
		}
	}


	public void processDecision(RoutingContext ctx) {
		JsonObject obj =ctx.getBodyAsJson();
		System.out.println(obj.toString());
	}

	public void processPacketInfo(RoutingContext ctx) {
		JsonObject obj =ctx.getBodyAsJson();
		System.out.println(obj.toString());
		PacketMetaInfo packetInfo = manualAdjudicationService.getApplicantPacketInfo(obj.getString("regId"));
		if(packetInfo!=null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	/**
	 * Send message.
	 *
	 * @param messageDTO the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.MANUAL_VERIFICATION_BUS, messageDTO);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context=ctx;
	}

}
