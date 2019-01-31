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
 * This class sends message to next stage after successful completion of manual
 * verification.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@Component
public class ManualVerificationStage extends MosipVerticleAPIManager implements ApplicationContextAware {

	@Autowired
	private ManualVerificationService manualAdjudicationService;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;

	private ApplicationContext context;

	@Autowired
	private CustomEnvironment env;

	/**
	 * Deploy stage.
	 */
	public void deployStage() {
		//env = context.getBean(CustomEnvironment.class);
		if (this.mosipEventBus == null) {
			this.mosipEventBus = this.getEventBus(this, env.getClusterManagerUrl());
		}
	}

	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, env.getServerPort());
	}

	private void routes(Router router) {
		router.post("/v0.1/registration-processor/manual-verification/applicantBiometric").handler(ctx -> {
			processBiometric(ctx);
		}).failureHandler(handlerObj -> {
			String obj = handlerObj.failure().getMessage();
			/** this.setResponse(context, obj); */
			handlerObj.response().putHeader("content-type", "application/json").end(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/applicantDemographic").handler(ctx -> {
			processDemographic(ctx);
		}).failureHandler(handlerObj -> {
			String obj = handlerObj.failure().getMessage();

			handlerObj.response().putHeader("content-type", "application/json").end(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/assignment").handler(ctx -> {
			processAssignment(ctx);
		}).failureHandler(handlerObj -> {
			String obj = handlerObj.failure().getMessage();
			/** this.setResponse(context, obj); */
			handlerObj.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/decision").handler(ctx -> {
			processDecision(ctx);
		}).failureHandler(handlerObj -> {
			String obj = handlerObj.failure().getMessage();
			// this.setResponse(context, obj);
			handlerObj.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

		router.post("/v0.1/registration-processor/manual-verification/packetInfo").handler(ctx -> {
			processPacketInfo(ctx);
		}).failureHandler(handlerObj -> {
			String obj = handlerObj.failure().getMessage();
			/** this.setResponse(context, obj); */
			handlerObj.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});
		
		router.get("/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
			
		}).failureHandler(context->{
			String obj = context.failure().getMessage();
			//this.setResponse(context, obj);
			context.response().putHeader("content-type", "application/json").end(obj);
			System.out.println(obj);
		});

	}

	public void processBiometric(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(obj.getString("regId"),
				obj.getString("fileName"));
		if (packetInfo != null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	public void processDemographic(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		byte[] packetInfo = manualAdjudicationService.getApplicantFile(obj.getString("regId"),
				PacketFiles.DEMOGRAPHIC.name());
		if (packetInfo != null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	public void processAssignment(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		UserDto userDto = new UserDto();
		userDto.setUserId(obj.getString("userId"));
		ManualVerificationDTO manualVerificationDTO = manualAdjudicationService.assignApplicant(userDto);
		if (manualVerificationDTO != null) {
			this.setResponse(ctx, manualVerificationDTO);
		}
	}

	public void processDecision(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
	}

	public void processPacketInfo(RoutingContext ctx) {
		JsonObject obj = ctx.getBodyAsJson();
		PacketMetaInfo packetInfo = manualAdjudicationService.getApplicantPacketInfo(obj.getString("regId"));
		if (packetInfo != null) {
			this.setResponse(ctx, packetInfo);
		}
	}

	/**
	 * Send message.
	 *
	 * @param messageDTO
	 *            the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.MANUAL_VERIFICATION_BUS, messageDTO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context = ctx;
	}

}
