package com.mindtree.camel_bridge;


import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MosipBridgeFactory extends AbstractVerticle {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipBridgeFactory.class);

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static void getEventBus() {
		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, vertx -> {
			if (vertx.succeeded()) {
				vertx.result().deployVerticle(MosipBridgeFactory.class.getName(),
						new DeploymentOptions().setHa(true).setWorker(true));
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), "failed : ", vertx.cause().toString());
			}
		});
	}

	@Override
	public void start() throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.setStreamCaching(true);
		VertxComponent vertxComponent = new VertxComponent();
		vertxComponent.setVertx(vertx);
//		RestTemplate restTemplate = new RestTemplate();
//		String camelRoutesFileName = BridgeUtil.getPropertyFromConfigServer("camel.routes.file.name");
//		String camelRoutesUrl = PropertyFileUtil.getProperty(MosipBridgeFactory.class, "bootstrap.properties", "config.server.url");
//		camelRoutesUrl = camelRoutesUrl + "/*/" + BridgeUtil.getActiveProfile() + "/" + BridgeUtil.getCloudConfigLabel()
//				+ "/" + camelRoutesFileName;
//		ResponseEntity<Resource> responseEntity = restTemplate.exchange(camelRoutesUrl, HttpMethod.GET, null,
//				Resource.class);
//		RoutesDefinition routes = camelContext.loadRoutesDefinition(responseEntity.getBody().getInputStream());
		RoutesDefinition routesNew = camelContext
				.loadRoutesDefinition(ClassLoader.getSystemResourceAsStream("camel-routes-new.xml"));
		RoutesDefinition routesUpdate = camelContext
				.loadRoutesDefinition(ClassLoader.getSystemResourceAsStream("camel-routes-update.xml"));
		RoutesDefinition routesActivate = camelContext
				.loadRoutesDefinition(ClassLoader.getSystemResourceAsStream("camel-routes-activate.xml"));
		camelContext.addRouteDefinitions(routesNew.getRoutes());
		camelContext.addRouteDefinitions(routesUpdate.getRoutes());
		camelContext.addRouteDefinitions(routesActivate.getRoutes());
		camelContext.addComponent("vertx", vertxComponent);
		camelContext.start();
		CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)).start();
	}

}
