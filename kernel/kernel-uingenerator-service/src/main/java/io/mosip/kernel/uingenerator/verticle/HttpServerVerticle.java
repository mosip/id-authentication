package io.mosip.kernel.uingenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.auth.adapter.handler.AuthHandler;
import io.mosip.kernel.uingenerator.config.UinServiceRouter;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

/**
 * Verticle for Uin generation http server
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */

public class HttpServerVerticle extends AbstractVerticle {

	Environment environment;

	/**
	 * Field for UinGeneratorRouter
	 */
	private UinServiceRouter uinServiceRouter;

	private AuthHandler authHandler;

	/**
	 * Initialize beans
	 * 
	 * @param context context
	 */
	public HttpServerVerticle(final ApplicationContext context) {
		authHandler = (AuthHandler) context.getBean("authHandler");
		uinServiceRouter = (UinServiceRouter) context.getBean("uinServiceRouter");
		environment = context.getEnvironment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start(io.vertx.core.Future)
	 */
	@Override
	public void start(Future<Void> future) {
		HttpServer httpServer = vertx.createHttpServer();
		authHandler.addCorsFilter(httpServer, vertx);
		httpServer.requestHandler(uinServiceRouter.createRouter(vertx));
		httpServer.listen(config().getInteger(UinGeneratorConstant.HTTP_PORT,
				Integer.parseInt(environment.getProperty(UinGeneratorConstant.SERVER_PORT))), result -> {
					if (result.succeeded()) {
						uinServiceRouter.checkAndGenerateUins(vertx);
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});
	}
}
