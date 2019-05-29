package io.mosip.registration.processor.camel.bridge;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;

/**
 * This class starts Vertx camel bridge.
 *
 * @author Mukul Puspam
 * @author Pranav kumar
 * @since 0.0.1
 */
@Component
public class MosipBridgeFactory extends MosipVerticleManager {

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	Environment environment;
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipBridgeFactory.class);

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public void getEventBus() {
		this.getEventBus(this, clusterManagerUrl);
	}

	@Override
	public void start() throws Exception {
		String camelRoutesFileName;
		JndiRegistry registry=null;
		    String[] beanNames=applicationContext.getBeanDefinitionNames();
		    if (beanNames != null) {
		      Map<String,String> enviroment= new HashMap<>();
		      enviroment.put("java.naming.factory.initial", "org.apache.camel.util.jndi.CamelInitialContextFactory");
		      registry= new JndiRegistry(enviroment);
		      for (String name : beanNames) {
		            registry.bind(name,applicationContext.getBean(name));
		      }
		    }
		String zone = environment.getProperty("registration.processor.zone");
		if(zone.equalsIgnoreCase("dmz")) {
            camelRoutesFileName = environment.getProperty("camel.routes.dmz.file.name");
        }
        else {
        	camelRoutesFileName = environment.getProperty("camel.routes.secure.file.name");
        }
		CamelContext camelContext = new DefaultCamelContext(registry);
		camelContext.setStreamCaching(true);
		VertxComponent vertxComponent = new VertxComponent();
		vertxComponent.setVertx(vertx);
        RestTemplate restTemplate = new RestTemplate();
        String camelRoutesUri = environment.getProperty("camel.routes.url");
        String camelRoutesUrl = camelRoutesUri + camelRoutesFileName;
        ResponseEntity<Resource> responseEntity = restTemplate.exchange(camelRoutesUrl, HttpMethod.GET, null,
                Resource.class);
        RoutesDefinition routes = camelContext.loadRoutesDefinition(responseEntity.getBody().getInputStream());
		camelContext.addRouteDefinitions(routes.getRoutes());
		camelContext.addComponent("vertx", vertxComponent);
		camelContext.start();
		CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)).start();
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		// TODO Auto-generated method stub
		return null;
	}
}
