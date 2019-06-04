package io.mosip.kernel.uingenerator.config;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.auth.adapter.handler.AuthHandler;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilException;
import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.service.UinService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Router for vertx server
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Component
public class UinServiceRouter {

	/**
	 * Field for environment
	 */
	@Autowired
	Environment environment;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private AuthHandler authHandler;

	@Autowired
	private SignatureUtil signatureUtil;

	/**
	 * Field for UinGeneratorService
	 */
	@Autowired
	private UinService uinGeneratorService;

	/**
	 * Creates router for vertx server
	 * 
	 * @param vertx vertx
	 * @return Router
	 */
	public Router createRouter(Vertx vertx) {
		Router router = Router.router(vertx);
		final String servletPath = environment.getProperty(UinGeneratorConstant.SERVER_SERVLET_PATH);
		String path = servletPath + UinGeneratorConstant.VUIN;
		String profile = environment.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE);

		router.route().handler(routingContext -> {
			routingContext.response().headers().add(CONTENT_TYPE, UinGeneratorConstant.APPLICATION_JSON);
			routingContext.next();
		});

		if (!profile.equalsIgnoreCase("test")) {
			authHandler.addAuthFilter(router, path, HttpMethod.GET, "REGISTRATION_PROCESSOR");
		}
		router.get(path).handler(routingContext -> getRouter(vertx, routingContext));

		router.route().handler(BodyHandler.create());
		if (!profile.equalsIgnoreCase("test")) {
			authHandler.addAuthFilter(router, path, HttpMethod.PUT, "REGISTRATION_PROCESSOR");
		}
		router.put(path).consumes(UinGeneratorConstant.APPLICATION_JSON).handler(this::updateRouter);

		configureHealthCheckEndpoint(vertx, router, servletPath);

		router.route(environment.getProperty(UinGeneratorConstant.SERVER_SERVLET_PATH) + "/*").handler(
				StaticHandler.create().setCachingEnabled(false).setWebRoot(UinGeneratorConstant.SWAGGER_UI_PATH)
						.setAlwaysAsyncFS(true).setAllowRootFileSystemAccess(true));
		return router;
	}

	private void configureHealthCheckEndpoint(Vertx vertx, Router router, final String servletPath) {
		UinServiceHealthCheckerhandler healthCheckHandler = new UinServiceHealthCheckerhandler(vertx, null,
				objectMapper, environment);
		router.get(servletPath + UinGeneratorConstant.HEALTH_ENDPOINT).handler(healthCheckHandler);
		healthCheckHandler.register("db", healthCheckHandler::databaseHealthChecker);
		healthCheckHandler.register("diskspace", healthCheckHandler::dispSpaceHealthChecker);
		healthCheckHandler.register("uingeneratorverticle",
				future -> healthCheckHandler.verticleHealthHandler(future, vertx));
	}

	private void getRouter(Vertx vertx, RoutingContext routingContext) {

		String resWrpJsonString = null;
		String signedData = null;

		try {
			UinResponseDto uin = new UinResponseDto();
			uin = uinGeneratorService.getUin();
			ResponseWrapper<UinResponseDto> reswrp = new ResponseWrapper<>();
			String timestamp = DateUtils.getUTCCurrentDateTimeString();
			reswrp.setResponsetime(DateUtils.convertUTCToLocalDateTime(timestamp));
			reswrp.setResponse(uin);
			reswrp.setErrors(null);
			String profile = environment.getProperty(UinGeneratorConstant.SPRING_PROFILES_ACTIVE);
			if (!profile.equalsIgnoreCase("test")) {
				resWrpJsonString = objectMapper.writeValueAsString(reswrp);

				SignatureResponse cryptoManagerResponseDto = signatureUtil.sign(resWrpJsonString, timestamp);
				signedData = cryptoManagerResponseDto.getData();
			}
			routingContext.response().putHeader("response-signature", signedData)
					.end(objectMapper.writeValueAsString(reswrp));
		} catch (UinNotFoundException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
			setError(routingContext, error);
		} catch (SignatureUtilClientException e1) {
			ExceptionUtils.logRootCause(e1);
			setError(routingContext, e1.getList().get(0));
		} catch (SignatureUtilException e1) {
			ExceptionUtils.logRootCause(e1);
			ServiceError error = new ServiceError(UinGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					e1.toString());
			setError(routingContext, error);
		} catch (Exception e) {
			ExceptionUtils.logRootCause(e);
			ServiceError error = new ServiceError(UinGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					e.getMessage());
			setError(routingContext, error);
		} finally {
			checkAndGenerateUins(vertx);
		}
	}

	/**
	 * update router for update the status of the given UIN
	 * 
	 * @param vertx vertx
	 * @return Router
	 */
	private void updateRouter(RoutingContext routingContext) {
		UinStatusUpdateReponseDto uinresponse = new UinStatusUpdateReponseDto();
		UinEntity uin;
		RequestWrapper<UinEntity> reqwrp;
		try {
			reqwrp = objectMapper.readValue(routingContext.getBodyAsJson().toString(),
					new TypeReference<RequestWrapper<UinEntity>>() {
					});
			uin = reqwrp.getRequest();
		} catch (Exception e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					e.getMessage());
			setError(routingContext, error);
			return;
		}

		if (uin == null) {
			routingContext.response().setStatusCode(200).end();
			return;
		}
		try {
			uinresponse = uinGeneratorService.updateUinStatus(uin);
			ResponseWrapper<UinStatusUpdateReponseDto> reswrp = new ResponseWrapper<>();
			reswrp.setResponse(uinresponse);
			reswrp.setId(reqwrp.getId());
			reswrp.setVersion(reqwrp.getVersion());
			reswrp.setErrors(null);
			routingContext.response().putHeader("content-type", UinGeneratorConstant.APPLICATION_JSON)
					.setStatusCode(200).end(objectMapper.writeValueAsString(reswrp));
		} catch (UinNotFoundException e) {
			ExceptionUtils.logRootCause(e);
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
			setError(routingContext, error, reqwrp);
		} catch (UinStatusNotFoundException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorCode(),
					UinGeneratorErrorCode.UIN_STATUS_NOT_FOUND.getErrorMessage());
			setError(routingContext, error, reqwrp);
		} catch (UinNotIssuedException e) {
			ServiceError error = new ServiceError(UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorCode(),
					UinGeneratorErrorCode.UIN_NOT_ISSUED.getErrorMessage());
			setError(routingContext, error, reqwrp);
		} catch (Exception e) {
			ExceptionUtils.logRootCause(e);
			ServiceError error = new ServiceError(UinGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					e.getMessage());
			setError(routingContext, error, reqwrp);
		}

	}

	/**
	 * Checks and generate uins
	 * 
	 * @param vertx vertx
	 */
	public void checkAndGenerateUins(Vertx vertx) {
		vertx.eventBus().send(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, UinGeneratorConstant.GENERATE_UIN);
	}

	private void setError(RoutingContext routingContext, ServiceError error) {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		errorResponse.getErrors().add(error);
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode reqNode;
		if (routingContext.getBodyAsJson() != null) {
			try {
				reqNode = objectMapper.readTree(routingContext.getBodyAsJson().toString());
				errorResponse.setId(reqNode.path("id").asText());
				errorResponse.setVersion(reqNode.path("version").asText());
			} catch (IOException e) {
			}
		}
		try {
			routingContext.response().putHeader("content-type", UinGeneratorConstant.APPLICATION_JSON)
					.setStatusCode(200).end(objectMapper.writeValueAsString(errorResponse));
		} catch (JsonProcessingException e1) {

		}
	}

	private void setError(RoutingContext routingContext, ServiceError error, RequestWrapper<UinEntity> reqwrp) {
		ResponseWrapper<ServiceError> errorResponse = new ResponseWrapper<>();
		errorResponse.getErrors().add(error);
		errorResponse.setId(reqwrp.getId());
		errorResponse.setVersion(reqwrp.getVersion());
		try {
			routingContext.response().putHeader("content-type", UinGeneratorConstant.APPLICATION_JSON)
					.setStatusCode(200).end(objectMapper.writeValueAsString(errorResponse));
		} catch (JsonProcessingException e1) {

		}
	}

}