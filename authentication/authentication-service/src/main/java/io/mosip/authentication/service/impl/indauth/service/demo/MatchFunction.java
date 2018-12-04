package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;

/**
 * 
 * @author Dinesh Karuppiah
 */

@FunctionalInterface
public interface MatchFunction {

	int match(Object value1, Object value2, Map<String, Object> matchProperties);

}
