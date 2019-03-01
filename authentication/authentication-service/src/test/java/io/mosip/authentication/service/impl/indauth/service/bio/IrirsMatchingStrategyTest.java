package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;

public class IrirsMatchingStrategyTest {

	@Test
	public void TestUnknownError() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = IrisMatchingStrategy.PARTIAL.getMatchFunction();
		String reqValues;
		String entityValues;
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(IrisProvider.class.getSimpleName(), "TEst");
		matchFunction.match("name", "name", matchProperties);
	}

}
