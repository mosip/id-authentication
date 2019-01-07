package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Map;
import java.util.function.BiFunction;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.indauth.service.demo.AgeMatchingStrategy;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;

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
				logError(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		} else {
			Object object = props.get(BioAuthType.class.getSimpleName());
			if (object instanceof BioAuthType) {
				BioAuthType bioAuthType = ((BioAuthType) object);
				if (bioAuthType.equals(BioAuthType.FGR_MIN)) {
					logError(IdAuthenticationErrorConstants.FGRMIN_MISMATCH);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FGRMIN_MISMATCH);
				} else if (bioAuthType.equals(BioAuthType.FGR_IMG)) {
					logError(IdAuthenticationErrorConstants.FGRIMG_MISMATCH);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FGRIMG_MISMATCH);
				} else {
					logError(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				}
			} else {
				logError(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FingerPrintMatchingStrategy.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The Constant AGE Matching strategy. */
	private static final String TYPE = "FingerPrintMatchingStrategy";

	private FingerPrintMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
	}

	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(DEFAULT_SESSION_ID, TYPE, "Inside AgeMathing Strategy" + errorConstants.getErrorCode(),
				errorConstants.getErrorMessage());
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
