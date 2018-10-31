package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityValue;

/**
 * @author Arun Bose The Interface DemoEntityInfoFetcher.
 */
@FunctionalInterface
public interface DemoEntityInfoFetcher {

	/**
	 * Gets the info.
	 *
	 * @param demoEntity the demo entity
	 * @return the info
	 */
	String getInfo(List<IdentityValue> idValues);

}
