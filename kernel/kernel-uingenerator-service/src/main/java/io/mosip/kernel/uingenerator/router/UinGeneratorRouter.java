/**
 * 
 */
package io.mosip.kernel.uingenerator.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ErrorResponse;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.generator.UinProcesser;
import io.mosip.kernel.uingenerator.service.UinGeneratorService;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

/**
 * Router for vertx server
 * 
 * @author Dharmesh Khandelwal
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
	 * Field for UinProcesser
	 */
	@Autowired
	private UinProcesser uinProcesser;

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
		router.get(environment.getProperty(UinGeneratorConstant.SERVER_SERVLET_PATH) + UinGeneratorConstant.V1_0_UIN)
				.handler(routingContext -> {
					UinResponseDto uin = new UinResponseDto();
					try {
						uin = uinGeneratorService.getUin();
						routingContext.response().setStatusCode(200).end(Json.encode(uin));
					} catch (UinNotFoundException e) {
						ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
								UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
						ErrorResponse<ServiceError> errorResponse = new ErrorResponse<>();
						errorResponse.getErrors().add(error);
						errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
						routingContext.response().setStatusCode(200).end(Json.encode(errorResponse));
					} finally {
						checkAndGenerateUins(vertx);
					}
				});
		return router;
	}

	/**
	 * Checks and generate uins
	 * 
	 * @param vertx
	 *            vertx
	 */
	public void checkAndGenerateUins(Vertx vertx) {
		boolean shouldGenerateUins = uinProcesser.shouldGenerateUins();
		if (shouldGenerateUins) {
			vertx.eventBus().send(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, UinGeneratorConstant.GENERATE_UIN);
		}
	}
}
