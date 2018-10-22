package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;

import io.mosip.authentication.core.dto.indauth.DemoDTO;

/**
 * This functional interface is used to get the DemoDTO
 * @author Arun Bose
 */
@FunctionalInterface
public interface DemoDTOInfoFetcher {
	/**
	 * method is used to fetch the object
	 * @return Object
	 * 
	 */
	Optional<Object> getInfo(DemoDTO demoDTO);

}
