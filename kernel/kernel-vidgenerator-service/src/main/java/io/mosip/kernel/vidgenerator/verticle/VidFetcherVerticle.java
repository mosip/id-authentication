package io.mosip.kernel.vidgenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.auth.adapter.handler.AuthHandler;
import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.router.VidFetcherRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * Verticle for fetching VID
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */

public class VidFetcherVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VidFetcherVerticle.class);

	private Environment environment;

	/**
	 * Field for UinGeneratorRouter
	 */
	private VidFetcherRouter vidFetcherRouter;

	private AuthHandler authHandler;

	/**
	 * Initialize beans
	 * 
	 * @param context context
	 */
	public VidFetcherVerticle(final ApplicationContext context) {
		authHandler = (AuthHandler) context.getBean("authHandler");
		vidFetcherRouter = (VidFetcherRouter) context.getBean("vidFetcherRouter");
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

		// Parent router so that global options can be applied to it in future
		Router parentRouter = Router.router(vertx);
		// giving the context path to parent router
		parentRouter.route("/vidgenerator").consumes("application/json").produces("application/json");

		// mount all the routers to parent router
		parentRouter.mountSubRouter("/vid", vidFetcherRouter.createRouter(vertx));

		httpServer.requestHandler(parentRouter);
		httpServer.listen(8080, result -> {
			if (result.succeeded()) {
				vertx.eventBus().publish(EventType.CHECKPOOL, EventType.CHECKPOOL);
				future.complete();
			} else {
				future.fail(result.cause());
			}
		});
	}
}
