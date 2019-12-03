package io.mosip.kernel.pridgenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.pridgenerator.constant.EventType;
import io.mosip.kernel.pridgenerator.constant.PRIDGeneratorConstant;
import io.mosip.kernel.pridgenerator.router.PridFetcherRouter;
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

public class PridFetcherVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(PridFetcherVerticle.class);

	private Environment environment;

	/**
	 * Field for UinGeneratorRouter
	 */
	private PridFetcherRouter pridFetcherRouter;

	//private AuthHandler authHandler;

	/**
	 * Initialize beans
	 * 
	 * @param context context
	 */
	public PridFetcherVerticle(final ApplicationContext context) {
		//authHandler = (AuthHandler) context.getBean("authHandler");
		pridFetcherRouter = (PridFetcherRouter) context.getBean("pridFetcherRouter");
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
		// giving the root to parent router
		parentRouter.route().consumes(PRIDGeneratorConstant.APPLICATION_JSON)
				.produces(PRIDGeneratorConstant.APPLICATION_JSON);
		//System.out.println(environment.getProperty(PRIDGeneratorConstant.SERVER_SERVLET_PATH));
		// mount all the routers to parent router
		parentRouter.mountSubRouter(
				environment.getProperty(PRIDGeneratorConstant.SERVER_SERVLET_PATH) + PRIDGeneratorConstant.PRID,
				pridFetcherRouter.createRouter(vertx));

		httpServer.requestHandler(parentRouter);
		httpServer.listen(Integer.parseInt(environment.getProperty(PRIDGeneratorConstant.SERVER_PORT)), result -> {
			if (result.succeeded()) {
				LOGGER.debug("prid fetcher verticle deployed");
				vertx.eventBus().publish(EventType.CHECKPOOL, EventType.CHECKPOOL);
				future.complete();
			} else if (result.failed()) {
				LOGGER.error("prid fetcher verticle deployment failed with cause ", result.cause());
				future.fail(result.cause());
			}
		});
	}
}
