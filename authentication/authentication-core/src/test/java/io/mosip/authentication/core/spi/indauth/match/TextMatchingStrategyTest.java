package io.mosip.authentication.core.spi.indauth.match;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;

public class TextMatchingStrategyTest {

	TextMatchingStrategy textMatchingStrategy = new TextMatchingStrategy() {

		@Override
		public MatchingStrategyType getType() {
			return MatchingStrategyType.EXACT;
		}

		@Override
		public MatchFunction getMatchFunction() {
			return (reqInfo, entityInfo, matchProperties) -> 100;
		}
	};

	@Test
	public void TestmatchFunction() throws IdAuthenticationBusinessException {
		Map<String, String> reqValues = new HashMap<>();
		Map<String, String> entityValues = new HashMap<>();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("language", "fra");
		reqValues.put("Name", "Ibrahim Ibn Ali");
		entityValues.put("Name", "Ibrahim Ibn Ali");
		int match = textMatchingStrategy.match(reqValues, entityValues, matchProperties);
		assertThat(match).isEqualTo(100);
	}

}
