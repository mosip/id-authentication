package io.mosip.registration.processor.connector;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.connector.stage.ConnectorStage;



/**
 * Hello world!
 *
 */
public class ConnectorApplication 
{
    public static void main( String[] args )
    {
    	AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan(
				  "io.mosip.registration.processor.connector.config",
				  "io.mosip.registration.processor.connector.stage");
		configApplicationContext.refresh();
		ConnectorStage connectorStage = configApplicationContext.getBean(ConnectorStage.class);
		connectorStage.deployVerticle();
    }
}
