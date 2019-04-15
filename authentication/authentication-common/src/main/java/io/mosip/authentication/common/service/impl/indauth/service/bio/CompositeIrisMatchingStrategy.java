package io.mosip.authentication.common.service.impl.indauth.service.bio;

import java.util.Map;
import java.util.function.BiFunction;

import io.mosip.authentication.common.impl.indauth.match.MatchingStrategyImpl;
import io.mosip.authentication.common.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;

/**
 * The Enum CompositeIrisMatchingStrategy - used to compare and evaluate the
 * IRIS value received from the request and entity
 * 
 * @author Sanjay Murali
 */
public enum CompositeIrisMatchingStrategy implements MatchingStrategy {

	@SuppressWarnings("unchecked")
	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(IrisProvider.class.getSimpleName());
			if (object instanceof BiFunction) {
				BiFunction<Map<String, String>, Map<String, String>, Double> func = (BiFunction<Map<String, String>, Map<String, String>, Double>) object;
				Map<String, String> reqInfoMap = (Map<String, String>) reqInfo;
				reqInfoMap.put(getIdvid(), (String) props.get(getIdvid())); // FIXME will be removed when iris sdk is
																			// provided
				return (int) func.apply(reqInfoMap, (Map<String, String>) entityInfo).doubleValue();
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorMessage(),
								BioAuthType.IRIS_COMP_IMG.getType()));
			}
		}
		return 0;
	});

	/** The Constant IDVID. */
	private static final String IDVID = "idvid";

	/** The matching strategy impl. */
	private MatchingStrategyImpl matchingStrategyImpl;

	/**
	 * Instantiates a new composite iris matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	private CompositeIrisMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#
	 * getMatchingStrategy()
	 */
	public MatchingStrategy getMatchingStrategy() {
		return matchingStrategyImpl;
	}

}
