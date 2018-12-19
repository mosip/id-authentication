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
 * @author Dinesh Karuppiah.T
 *
 */
public enum FingerPrintMatchingStrategy implements MatchingStrategy {

	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Object object = props.get(FingerprintProvider.class.getSimpleName());
			if (object instanceof BiFunction) {
				BiFunction<String, String, Double> func = (BiFunction<String, String, Double>) object;
				return (int) func.apply((String) reqInfo, (String) entityInfo).doubleValue();
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		} else {
			Object object = props.get(BioAuthType.class.getSimpleName());
			if (object instanceof BioAuthType) {
				BioAuthType bioAuthType = ((BioAuthType) object);
				if (bioAuthType.equals(BioAuthType.FGR_MIN)) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FGRMIN_MISMATCH);
				} else if (bioAuthType.equals(BioAuthType.FGR_IMG)) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FGRIMG_MISMATCH);
				} else {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				}
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	private FingerPrintMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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

}
