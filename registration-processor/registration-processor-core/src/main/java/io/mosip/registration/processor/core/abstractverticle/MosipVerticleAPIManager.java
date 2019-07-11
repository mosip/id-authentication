package io.mosip.registration.processor.core.abstractverticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.constant.HealthConstant;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author Mukul Puspam
 *
 */
public abstract class MosipVerticleAPIManager extends MosipVerticleManager {

	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;

	@Autowired
	Environment environment;

	@Autowired
	ObjectMapper objectMapper;

	/**
	 * This method creates a body handler for the routes
	 *
	 * @param vertx
	 * @return
	 */
	public Router postUrl(Vertx vertx, MessageBusAddress consumeAddress, MessageBusAddress sendAddress) {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		if (consumeAddress == null && sendAddress == null)
			configureHealthCheckEndpoint(vertx, router, environment.getProperty(HealthConstant.SERVLET_PATH), null,
					null);
		else if (consumeAddress == null)
			configureHealthCheckEndpoint(vertx, router, environment.getProperty(HealthConstant.SERVLET_PATH), null,
					sendAddress.getAddress());
		else if (sendAddress == null)
			configureHealthCheckEndpoint(vertx, router, environment.getProperty(HealthConstant.SERVLET_PATH),
					consumeAddress.getAddress(), null);
		else
			configureHealthCheckEndpoint(vertx, router, environment.getProperty(HealthConstant.SERVLET_PATH),
					consumeAddress.getAddress(), sendAddress.getAddress());
		return router;
	}

	public void configureHealthCheckEndpoint(Vertx vertx, Router router, final String servletPath,
			String consumeAddress, String sendAddress) {
		StageHealthCheckHandler healthCheckHandler = new StageHealthCheckHandler(vertx, null, objectMapper,
				environment);
		router.get(servletPath + HealthConstant.HEALTH_ENDPOINT).handler(healthCheckHandler);
		if (servletPath.contains("receiver") || servletPath.contains("uploader")) {
			healthCheckHandler.register("virusscanner", healthCheckHandler::virusScanHealthChecker);
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Verticle",
					future -> healthCheckHandler.senderHealthHandler(future, vertx, sendAddress));
		}
		if (servletPath.contains("packetvalidator") || servletPath.contains("osi") || servletPath.contains("demo")
				|| servletPath.contains("bio") || servletPath.contains("uin") || servletPath.contains("quality")
				|| servletPath.contains("abishandler")) {
			healthCheckHandler.register("hdfscheck", healthCheckHandler::hdfsHealthChecker);
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Send", future -> {
						healthCheckHandler.senderHealthHandler(future, vertx, sendAddress);
					});
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Consume",
					future -> {
						healthCheckHandler.consumerHealthHandler(future, vertx, consumeAddress);
					});
		}
		if (servletPath.contains("external")) {
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Send", future -> {
						healthCheckHandler.senderHealthHandler(future, vertx, sendAddress);
					});
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Consume",
					future -> {
						healthCheckHandler.senderHealthHandler(future, vertx, consumeAddress);
					});
		}
		if (servletPath.contains("manual")) {
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Verticle",
					future -> healthCheckHandler.senderHealthHandler(future, vertx, sendAddress));
		}
		if (servletPath.contains("print") || servletPath.contains("abismiddleware")) {
			healthCheckHandler.register("queuecheck", healthCheckHandler::queueHealthChecker);
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Verticle",
					future -> healthCheckHandler.consumerHealthHandler(future, vertx, consumeAddress));
		}
		if (servletPath.contains("sender")) {
			healthCheckHandler.register("hdfscheck", healthCheckHandler::hdfsHealthChecker);
			healthCheckHandler.register(
					servletPath.substring(servletPath.lastIndexOf("/") + 1, servletPath.length()) + "Verticle",
					future -> healthCheckHandler.consumerHealthHandler(future, vertx, consumeAddress));
		}

		healthCheckHandler.register("diskSpace", healthCheckHandler::dispSpaceHealthChecker);
		healthCheckHandler.register("db", healthCheckHandler::databaseHealthChecker);
	}

	/**
	 * This method creates server for vertx web application
	 * 
	 * @param router
	 * @param port
	 */
	public void createServer(Router router, int port) {
		vertx.createHttpServer().requestHandler(router::accept).listen(port);
	}

	/**
	 * This method returns a response to the routing context
	 * 
	 * @param ctx
	 * @param object
	 */
	public void setResponse(RoutingContext ctx, Object object) {
		ctx.response().putHeader("content-type", "text/plain").putHeader("Access-Control-Allow-Origin", "*")
				.putHeader("Access-Control-Allow-Methods", "GET, POST").setStatusCode(200)
				.end(Json.encodePrettily(object));
	};

	/**
	 * This method returns a response to the routing context
	 * 
	 * @param ctx
	 * @param object
	 * @param contentType
	 */
	public void setResponseWithDigitalSignature(RoutingContext ctx, Object object, String contentType) {
		HttpServerResponse response = ctx.response();
		if (isEnabled)
			response.putHeader("Response-Signature", digitalSignatureUtility.getDigitalSignature(object.toString()));
		response.putHeader("content-type", contentType).putHeader("Access-Control-Allow-Origin", "*")
				.putHeader("Access-Control-Allow-Methods", "GET, POST").setStatusCode(200)
				.end(Json.encodePrettily(object));

	};
}