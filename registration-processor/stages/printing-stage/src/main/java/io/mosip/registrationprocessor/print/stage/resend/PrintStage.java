package io.mosip.registrationprocessor.print.stage.resend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registrationprocessor.print.stage.exception.handler.PrintGlobalExceptionHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class PrintStage extends MosipVerticleAPIManager{

	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintStage.class);
	
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;
	
	@Value("${server.port}")
	private String port;
	
	private MosipEventBus mosipEventBus;
	
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}
	
	@Autowired
	public PrintGlobalExceptionHandler globalExceptionHandler;

	/**
	 * deploys this verticle
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl);
	}
	
	
	// Need clarify where to push the template 
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, messageDTO);
	}
	
	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage
	 * 
	 * @param router
	 */
	private void routes(Router router) {
		
		router.post("/v0.1/registration-processor/print-stage/resend").handler(ctx -> {
			reSendPrintPdf(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, globalExceptionHandler.handler(failureHandler.failure()));
		});
		
		router.get("/print-stage/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context->{
			this.setResponse(context, context.failure().getMessage());
		});
	}

	private void reSendPrintPdf(RoutingContext ctx) {
		JsonObject object = ctx.getBodyAsJson();
		System.out.println(object.toString());
		MessageDTO messageDTO = new MessageDTO();
			this.setResponse(ctx, "Re-sending to Queue");
			//this.sendMessage(messageDTO);
	}
}
