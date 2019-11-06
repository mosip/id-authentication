package io.mosip.kernel.vidgenerator.router;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.dto.VidFetchResponseDto;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * Router for vertx server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class VidFetcherRouter {
	
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
		Router router = Router.router(vertx);
		router.get().handler(routingContext -> {
			// send a publish event to vid pool checker
			vertx.eventBus().publish(EventType.CHECKPOOL, EventType.CHECKPOOL);
			ResponseWrapper<VidFetchResponseDto> reswrp = new ResponseWrapper<>();
			vertx.executeBlocking(blockingCodeHandler -> {
				String expiryDateString =routingContext.request().getParam(VIDEXPIRY);
				LocalDateTime expiryTime=null;
				if(expiryDateString != null) {
	            if(expiryDateString.trim().isEmpty()) {
	            	//exception
	            }
				DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN);
				expiryTime = LocalDateTime.parse(expiryDateString,dateTimeFormatter);
				}
				VidFetchResponseDto vidFetchResponseDto=vidService.fetchVid(expiryTime);
				String timestamp = DateUtils.getUTCCurrentDateTimeString();
				reswrp.setResponsetime(DateUtils.convertUTCToLocalDateTime(timestamp));
				reswrp.setResponse(vidFetchResponseDto);
				reswrp.setErrors(null);
				blockingCodeHandler.complete();
			}, resultHandler -> {
	         try {
				routingContext.response().end(objectMapper.writeValueAsString(reswrp));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			});
		});
    
		return router;
	}
/*
	private void configureHealthCheckEndpoint(Vertx vertx, Router router, final String servletPath) {
		UinServiceHealthCheckerhandler healthCheckHandler = new UinServiceHealthCheckerhandler(vertx, null,
				objectMapper, environment);
		router.get(servletPath + UinGeneratorConstant.HEALTH_ENDPOINT).handler(healthCheckHandler);
		healthCheckHandler.register("db", healthCheckHandler::databaseHealthChecker);
		healthCheckHandler.register("diskspace", healthCheckHandler::dispSpaceHealthChecker);
		healthCheckHandler.register("uingeneratorverticle",
				future -> healthCheckHandler.verticleHealthHandler(future, vertx));
	}*/


	/*
	 * private void setError(RoutingContext routingContext, ServiceError error,
	 * RequestWrapper<UinEntity> reqwrp) { ResponseWrapper<ServiceError>
	 * errorResponse = new ResponseWrapper<>();
	 * errorResponse.getErrors().add(error); errorResponse.setId(reqwrp.getId());
	 * errorResponse.setVersion(reqwrp.getVersion()); try {
	 * routingContext.response().putHeader("content-type",
	 * UinGeneratorConstant.APPLICATION_JSON)
	 * .setStatusCode(200).end(objectMapper.writeValueAsString(errorResponse)); }
	 * catch (JsonProcessingException e1) {
	 * 
	 * } }
	 */

}
