package io.mosip.registration.processor.camel.bridge;

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.camel.bridge.util.PropertyFileUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.AbstractVerticle;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;

/**
 * The Class MosipCamelBridge.
 * 
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class MosipCamelBridge extends AbstractVerticle {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipCamelBridge.class);

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		MosipBridgeFactory.getEventBus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() throws Exception {

		vertx.eventBus().consumer(MessageBusAddress.ERROR.getAddress(),
				message -> regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), "ERROR while doing operation >> : ",
						message.body().toString()));

		CamelContext camelContext = new DefaultCamelContext();
		RestTemplate restTemplate = new RestTemplate();
		String url = PropertyFileUtil.getProperty(MosipCamelBridge.class, "bootstrap.properties",
				"camel.routes.configuration");
		ResponseEntity<Resource> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, Resource.class);
		RoutesDefinition routes = camelContext.loadRoutesDefinition(responseEntity.getBody().getInputStream());
		camelContext.addRouteDefinitions(routes.getRoutes());
		camelContext.start();

		CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)).start();
	}

}
