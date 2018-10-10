package io.mosip.authentication.service.impl.indauth.service.demo;

/**
 * @author Arun Bose 
 * The Interface DemoEntityInfoFetcher.
 */
@FunctionalInterface
public interface DemoEntityInfoFetcher {

	 /**
 	 * Gets the info.
 	 *
 	 * @param demoEntity the demo entity
 	 * @return the info
 	 */
 	Object getInfo(DemoEntity demoEntity);
	 
}

