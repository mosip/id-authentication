/**
 * 
 */
package io.mosip.kernel.uingenerator.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.service.UinGeneratorService;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Router for vertx server
 * 
 * @author Dharmesh Khandelwal
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Component
public class UinGeneratorRouter {

	/**
	 * Field for environment
	 */
	@Autowired
	Environment environment;

	/**
	 * Field for UinGeneratorService
	 */
	@Autowired
	private UinGeneratorService uinGeneratorService;

	/**
	 * Creates router for vertx server
	 * 
	 * @param vertx
	 *            vertx
	 * @return Router
	 */

	public Router createRouter(Vertx vertx) {
		Router router = Router.router(vertx);

		router.get(environment.getProperty(UinGeneratorConstant.UIN_PATH)).handler(this::getRouter);

		router.route().handler(BodyHandler.create());
		router.put(environment.getProperty(UinGeneratorConstant.UIN_PATH)).consumes("*/json")
				.handler(this::updateRouter);

		checkAndGenerateUins(vertx);
		return router;
	}

	/**
	 * get method to get one UIN from Database
	 * 
	 * @param vertx
	 *            vertx
	 * @return Router
	 */
	private void getRouter(RoutingContext routingContext) {
		UinResponseDto uin = new UinResponseDto();
		try {
			uin = uinGeneratorService.getUin();
			routingContext.response().setStatusCode(200).end(Json.encode(uin));
		} catch (UinNotFoundException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
			ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
			errorResponse.getErrors().add(error);
			//errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
			routingContext.response().setStatusCode(200).end(Json.encode(errorResponse));
		}
	}

	/**
	 * update router for update the status of the given UIN
	 * 
	 * @param vertx
	 *            vertx
	 * @return Router
	 */
	private void updateRouter(RoutingContext routingContext) {
		UinStatusUpdateReponseDto uinresponse = new UinStatusUpdateReponseDto();
		JsonObject uin;
		try {
			uin = routingContext.getBodyAsJson();
		} catch (RuntimeException e) {
			routingContext.response().setStatusCode(400).end();
			return;

		}
		if (uin == null) {
			routingContext.response().setStatusCode(400).end();
			return;
		}
		try {
			uinresponse = uinGeneratorService.updateUinStatus(uin);
			routingContext.response().setStatusCode(200).end(Json.encode(uinresponse));
		} catch (UinNotFoundException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
			ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
			errorResponse.getErrors().add(error);
			//errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
			routingContext.response().setStatusCode(200).end(Json.encode(errorResponse));
		} catch (UinStatusNotFoundException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorMessage());
			ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
			errorResponse.getErrors().add(error);
			//errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
			routingContext.response().setStatusCode(200).end(Json.encode(errorResponse));
		} catch (UinNotIssuedException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorMessage());
			ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
			errorResponse.getErrors().add(error);
			//errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
			routingContext.response().setStatusCode(200).end(Json.encode(errorResponse));
		}

	}

	/**
	 * Checks and generate uins
	 * 
	 * @param vertx
	 *            vertx
	 */
	public void checkAndGenerateUins(Vertx vertx) {
		vertx.eventBus().send(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, UinGeneratorConstant.GENERATE_UIN);
	}
}
