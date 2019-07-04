package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.BiFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * Matching Strategy for Fingerprint
 * 
 * @author Dinesh Karuppiah.T
 */
public enum FingerPrintMatchingStrategy implements MatchingStrategy {

	@SuppressWarnings("unchecked")
	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(IdaIdMapping.FINGERPRINT.getIdname());
			if (object instanceof BiFunctionWithBusinessException) {
				BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = (BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double>) object;
				return (int) func.apply((Map<String, String>) reqInfo, (Map<String, String>) entityInfo).doubleValue();
			} else {
				logError(IdAuthenticationErrorConstants.BIO_MISMATCH);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH);
			}
		} else {
			Object object = props.get(BioAuthType.class.getSimpleName());
			if (object instanceof BioAuthType) {
				BioAuthType bioAuthType = ((BioAuthType) object);
				if (bioAuthType.equals(BioAuthType.FGR_MIN)) {
					logError(IdAuthenticationErrorConstants.BIO_MISMATCH);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH);
				} else if (bioAuthType.equals(BioAuthType.FGR_IMG)) {
					logError(IdAuthenticationErrorConstants.BIO_MISMATCH);
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorMessage(),
									BioAuthType.FACE_IMG.getType()));
				} else {
					logError(IdAuthenticationErrorConstants.BIO_MISMATCH);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH);
				}
			} else {
				logError(IdAuthenticationErrorConstants.BIO_MISMATCH);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH);
			}
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FingerPrintMatchingStrategy.class);

	/** The Constant AGE Matching strategy. */
	private static final String TYPE = "FingerPrintMatchingStrategy";

	private FingerPrintMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
	}

	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, TYPE,
				"Inside Fingerprint Strategy" + errorConstants.getErrorCode(), errorConstants.getErrorMessage());
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
