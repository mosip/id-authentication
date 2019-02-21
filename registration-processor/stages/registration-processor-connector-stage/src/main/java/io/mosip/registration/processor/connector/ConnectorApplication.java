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
     * the main method
     * @param args
     */
    public static void main( String[] args ) 
    {
    	AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan(
				  "io.mosip.registration.processor.connector.config",
				  "io.mosip.registration.processor.connector.stage");
		configApplicationContext.refresh();
		ConnectorStage connectorStage = (ConnectorStage) configApplicationContext.getBean("connectorStage");
		connectorStage.deployVerticle();
    }
}
