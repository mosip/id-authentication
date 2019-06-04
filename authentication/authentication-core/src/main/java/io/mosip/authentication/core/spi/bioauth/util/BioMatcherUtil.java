package io.mosip.authentication.core.spi.bioauth.util;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * Util class to calculate Composite and Individual's Match score based on
 * Request and Entity info's
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class BioMatcherUtil {

	@Autowired
	IBioApi bioApi;

	@Autowired
	CbeffUtil cbeffUtil;

	@Autowired
	Environment environment;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(BioMatcherUtil.class);

	/**
	 * Match the request and entity values and return calculated score.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 */
	public double matchValue(Map<String, String> reqInfo, Map<String, String> entityInfo)
			throws IdAuthenticationBusinessException {

		Object[][] objArrays = matchValues(reqInfo, entityInfo);
		Object[] reqInfoObj = objArrays[0];
		Object[] entityInfoObj = objArrays[1];

		Optional<BIR> reqBIR = Stream.of(reqInfoObj).map(this::getBir).filter(Objects::nonNull).findFirst();
		BIR[] entityBIR = Stream.of(entityInfoObj).map(this::getBir).toArray(size -> new BIR[size]);
		long internalScore = 0;
		if (reqBIR.isPresent()) {
			Score[] match;
			try {
				match = bioApi.match(reqBIR.get(), entityBIR, null);
				internalScore = match.length == 1 ? match[0].getInternalScore()
						: Stream.of(match).mapToLong(Score::getInternalScore).max().orElse(0);
				logger.info(IdAuthCommonConstants.SESSION_ID, "IDA", "matchValue",
						"Threshold Value >>>" + internalScore);
			} catch (BiometricException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "matchValue", "Biovalue not Matched");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}

			return internalScore;
		}
		return 0;
	}

	/**
	 * To create BIRType based on requested input
	 * 
	 * @param info
	 * @return
	 */
	private BIR getBir(Object info) {
		BIRBuilder birBuilder = new BIRBuilder();
		if (info instanceof String) {
			String reqInfoStr = (String) info;
			byte[] decodedrefInfo = decodeValue(reqInfoStr);
			birBuilder.withBdb(decodedrefInfo);
		}
		return birBuilder.build();
	}

	/**
	 * Calculates composite Match score and returns sum value
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private double matchCompositeValue(Object[] reqInfo, Object[] entityInfo) throws IdAuthenticationBusinessException {
		BIR[] reqBIR = Stream.of(reqInfo).map(this::getBir).toArray(size -> new BIR[size]);
		BIR[] entityBIR = Stream.of(entityInfo).map(this::getBir).toArray(size -> new BIR[size]);
		CompositeScore compositeScore;
		try {
			compositeScore = bioApi.compositeMatch(reqBIR, entityBIR, null);
			logger.info(IdAuthCommonConstants.SESSION_ID, "IDA", "matchScoreCalculator",
					"Threshold Value >>>" + compositeScore.getInternalScore());
		} catch (BiometricException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "matchScoreCalculator", "Biovalue not Matched");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);

		}

		return compositeScore.getInternalScore();
	}

	/**
	 * 
	 * Match Multiple values and return calculated score
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public double matchMultiValue(Map<String, String> reqInfo, Map<String, String> entityInfo)
			throws IdAuthenticationBusinessException {
		return matchMultiValues(reqInfo, entityInfo);
	}

	/**
	 * Match multiple value for Known entity.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @param matchScore the match score
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 */
	private double matchMultiValues(Map<String, String> reqInfo, Map<String, String> entityInfo)
			throws IdAuthenticationBusinessException {
		Object[][] objArrays = matchValues(reqInfo, entityInfo);
		Object[] reqInfoObj = objArrays[0];
		Object[] entityInfoObj = objArrays[1];
		return matchCompositeValue(reqInfoObj, entityInfoObj);
	}

	private Object[][] matchValues(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		Object[] reqInfoObj;
		Object[] entityInfoObj;

		int index = 0;
		if (reqInfo.keySet().stream().noneMatch(key -> key.startsWith(IdAuthCommonConstants.UNKNOWN_BIO))) {
			reqInfoObj = new Object[reqInfo.size()];
			entityInfoObj = new Object[reqInfo.size()];

			for (Map.Entry<String, String> e : reqInfo.entrySet()) {
				String key = e.getKey();
				reqInfoObj[index] = e.getValue();
				entityInfoObj[index] = entityInfo.get(key);
				index++;
			}
		} else {
			reqInfoObj = reqInfo.values().toArray();
			entityInfoObj = entityInfo.values().toArray();
		}

		return new Object[][] { reqInfoObj, entityInfoObj };
	}

	/**
	 * Decode value.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	private static byte[] decodeValue(String value) {
		return Base64.getDecoder().decode(value);
	}

}
