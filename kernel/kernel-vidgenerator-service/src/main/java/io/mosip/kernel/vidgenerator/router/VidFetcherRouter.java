package io.mosip.kernel.vidgenerator.router;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.constant.VIDGeneratorConstant;
import io.mosip.kernel.vidgenerator.constant.VIDGeneratorErrorCode;
import io.mosip.kernel.vidgenerator.dto.VidFetchResponseDto;
import io.mosip.kernel.vidgenerator.exception.VidGeneratorServiceException;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Router for vertx server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class VidFetcherRouter {
	
	@Value("${mosip.kernel.vid.get_executor_pool:400}")
	private int workerExecutorPool;

	private static final Logger LOGGER = LoggerFactory.getLogger(VidFetcherRouter.class);

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Autowired
	private VidService vidService;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Creates router for vertx server
	 * 
	 * @param vertx vertx
	 * @return Router
	 */
	public Router createRouter(Vertx vertx) {
		LOGGER.info("worker executor pool {}",workerExecutorPool);
		Router router = Router.router(vertx);
		router.get().handler(routingContext -> {
			LOGGER.info("publishing event to CHECKPOOL");
			// send a publish event to vid pool checker
			vertx.eventBus().publish(EventType.CHECKPOOL, EventType.CHECKPOOL);
			ResponseWrapper<VidFetchResponseDto> reswrp = new ResponseWrapper<>();
			WorkerExecutor executor=vertx.createSharedWorkerExecutor("get-vid", workerExecutorPool);
			executor.executeBlocking(blockingCodeHandler -> {
				String expiryDateString = routingContext.request().getParam(VIDGeneratorConstant.VIDEXPIRY);
				LocalDateTime expiryTime = null;
				if (expiryDateString != null) {
					if (expiryDateString.trim().isEmpty()) {
						ServiceError error = new ServiceError(
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_EMPTY.getErrorCode(),
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_EMPTY.getErrorMessage());
						setError(routingContext, error,blockingCodeHandler);
					}
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN);
					try {
					expiryTime = LocalDateTime.parse(expiryDateString, dateTimeFormatter);
					}catch(DateTimeParseException exception) {
						ServiceError error = new ServiceError(
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_PATTERN_INVALID.getErrorCode(),
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_PATTERN_INVALID.getErrorMessage());
						setError(routingContext, error,blockingCodeHandler);
					}
					if (expiryTime.isBefore(DateUtils.getUTCCurrentDateTime())) {
						ServiceError error = new ServiceError(
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_INVALID.getErrorCode(),
								VIDGeneratorErrorCode.VID_EXPIRY_DATE_INVALID.getErrorMessage());
						setError(routingContext, error,blockingCodeHandler);
					}
				}
				VidFetchResponseDto vidFetchResponseDto = null;
				try {
					vidFetchResponseDto = vidService.fetchVid(expiryTime);
				} catch (VidGeneratorServiceException exception) {
					ServiceError error = new ServiceError(exception.getErrorCode(), exception.getMessage());
					setError(routingContext, error,blockingCodeHandler);
				}
				String timestamp = DateUtils.getUTCCurrentDateTimeString();
				reswrp.setResponsetime(DateUtils.convertUTCToLocalDateTime(timestamp));
				reswrp.setResponse(vidFetchResponseDto);
				reswrp.setErrors(null);
				blockingCodeHandler.complete();
			},false, resultHandler -> {
				if(resultHandler.succeeded()) {
				try {
					routingContext.response().end(objectMapper.writeValueAsString(reswrp));
				} catch (JsonProcessingException exception) {
					ExceptionUtils.logRootCause(exception);
					ServiceError error = new ServiceError(VIDGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
							exception.getMessage());
					setError(routingContext, error,null);
				}}
			});
		});

		return router;
	}

	private void setError(RoutingContext routingContext, ServiceError error, Future<Object> blockingCodeHandler) {
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
				LOGGER.error(e.getMessage());
			}
		}
		try {
			routingContext.response().setStatusCode(200).end(objectMapper.writeValueAsString(errorResponse));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage());
		}
		
		LOGGER.error(error.getMessage());
		if(blockingCodeHandler != null) {
			blockingCodeHandler.fail(error.getMessage());
		}
	}

}