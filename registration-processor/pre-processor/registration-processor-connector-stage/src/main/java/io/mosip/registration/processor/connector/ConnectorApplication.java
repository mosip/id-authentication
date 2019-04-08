package io.mosip.registration.processor.connector;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.connector.stage.ConnectorStage;

/**
 * Connector application class
 * @author Jyoti Prakash Nayak
 *
 */
public class ConnectorApplication 
{
	
    /**
     * the main method to execute connector stage
     * @param args
     */
    public static void main( String[] args ) 
    {
    	AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan(
				  "io.mosip.registration.processor.connector.config",
				  "io.mosip.registration.processor.connector.stage",
				  "io.mosip.registration.processor.core.config");
		configApplicationContext.refresh();
		ConnectorStage connectorStage = (ConnectorStage) configApplicationContext.getBean(ConnectorStage.class);
		connectorStage.deployVerticle();
    }
}
