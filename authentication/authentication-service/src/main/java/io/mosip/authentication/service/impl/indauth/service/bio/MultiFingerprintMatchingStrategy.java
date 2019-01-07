package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Map;
import java.util.function.BiFunction;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * MatchingStrategy definition for multi-fingerprints matching.
 * 
 * @author Prem.Kumar4
 *
 */
public enum MultiFingerprintMatchingStrategy implements MatchingStrategy {

	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(FingerprintProvider.class.getSimpleName());
			if (object instanceof BiFunction) {
				BiFunction<Map<String, String>, Map<String, String>, Double> func = (BiFunction<Map<String, String>, Map<String, String>, Double>) object;
				return (int) func.apply((Map<String, String>) reqInfo, (Map<String, String>) entityInfo).doubleValue();
			}else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		}
		return 0;
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	private MultiFingerprintMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
	}

	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

	@Override
	public int match(Map<String, String> reqValues, Map<String, String> entityValues,
			Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		return matchFunction.match(reqValues, entityValues, matchProperties);
	}

}
