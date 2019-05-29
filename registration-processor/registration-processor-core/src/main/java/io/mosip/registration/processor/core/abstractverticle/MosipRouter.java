package io.mosip.registration.processor.core.abstractverticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

@Component
public class MosipRouter {
	/** Vertx router for routes */
	private Router router;

	/** Vertx route for api */
	private Route route;

	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;

	/**
	 * This method sets router for API
	 * 
	 * @param router
	 */
	public void setRoute(Router router) {
		this.router = router;
	}

	/**
	 * This method returns router for API
	 * 
	 * @return
	 */
	public Router getRouter() {
		return this.router;
	}

	/**
	 * This method is used for post API call
	 * 
	 * @param url
	 * @return
	 */
	public Route post(String url) {
		this.route = this.router.post(url);
		return this.route;
	}

	/**
	 * this method is used to handle request and failure handler including
	 * validation of token
	 *
	 * @param requestHandler
	 * @param failureHandler
	 */
	public void handler(Handler<RoutingContext> requestHandler, Handler<RoutingContext> failureHandler) {
		this.route.blockingHandler(this::validateToken).blockingHandler(requestHandler, false)
				.failureHandler(failureHandler);
	}

	public void nonSecureHandler(Handler<RoutingContext> requestHandler, Handler<RoutingContext> failureHandler) {
		this.route.blockingHandler(requestHandler, false)
				.failureHandler(failureHandler);
	}

	public void handler(Handler<RoutingContext> requestHandler, Handler<RoutingContext> requestHandler2,
			Handler<RoutingContext> failureHandler) {
		this.route.blockingHandler(this::validateToken).blockingHandler(requestHandler, false)
				.blockingHandler(requestHandler2, false).failureHandler(failureHandler);
	}

	/**
	 * this method is used to handle request only
	 * 
	 * @param requestHandler
	 */
	public void handler(Handler<RoutingContext> requestHandler) {
		this.route.blockingHandler(requestHandler, false);
	}

	/**
	 * This method is used for get API call
	 * 
	 * @param url
	 * @return
	 */
	public Route get(String url) {
		this.route = this.router.get(url);
		return this.route;
	}

	/**
	 * This method is used for validating token
	 * 
	 * @param routingContext
	 */
	private void validateToken(RoutingContext routingContext) {
		String token = routingContext.request().getHeader("Cookie");
		System.out.println("++++++++++++++Cookie++++++++ "+token);
		tokenValidator.validate(token, routingContext.normalisedPath());
		routingContext.next();
	}

}
