package io.mosip.authentication.common.service.impl.indauth.service.bio;

import java.util.Map;
import java.util.function.BiFunction;

import io.mosip.authentication.common.impl.indauth.match.MatchingStrategyImpl;
import io.mosip.authentication.common.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.faceauth.provider.FaceProvider;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * The Enum FaceMatchingStrategy.
 * 
 * @author Dinesh Karuppiah.T
 */
public enum FaceMatchingStrategy implements MatchingStrategy {
	/** The Constant idvid. */

	@SuppressWarnings("unchecked")
	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {

		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(FaceProvider.class.getSimpleName());
			if (object instanceof BiFunction) {
				BiFunction<Map<String, String>, Map<String, String>, Double> func = (BiFunction<Map<String, String>, Map<String, String>, Double>) object;
				Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
				reqInfoMap.put(getIdvid(), (String) props.get(getIdvid()));
				return (int) func.apply(reqInfoMap, (Map<String, String>) entityInfo).doubleValue();
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorMessage(),
								BioAuthType.FACE_IMG.getType()));
			}
		}
		return 0;
	});

	/** The Constant IDVID. */
	private static final String IDVID = "idvid";
	
	/** The matching strategy impl. */
	private MatchingStrategyImpl matchingStrategyImpl;


	/**
	 * Instantiates a new iris matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	private FaceMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		matchingStrategyImpl = new MatchingStrategyImpl(matchStrategyType, matchFunction);
	}

	/**
	 * Gets the idvid.
	 *
	 * @return the idvid
	 */
	public static String getIdvid() {
		return IDVID;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getMatchingStrategy()
	 */
	@Override
	public MatchingStrategy getMatchingStrategy() {
		return matchingStrategyImpl;
	}


}
