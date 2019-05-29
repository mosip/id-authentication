package io.mosip.authentication.core.spi.bioauth.util;

import java.util.Base64;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Enum BioAuthType.
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class BioMatcherUtil {

	@Autowired
	BioApiImpl bioApi;

	@Autowired
	CbeffImpl cbeffImpl;

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
	 */
	public double matchValue(Object reqInfo, Object entityInfo) {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			BIRType reqInfobirType = getBirType(reqInfo);
			BIRType entityInfoBirType = getBirType(entityInfo);
			BIRType[] entityInfobirType = new BIRType[] { entityInfoBirType };
			Score[] match = bioApi.match(reqInfobirType, entityInfobirType, null);
			long internalScore = match.length == 1 ? match[0].getInternalScore() : 0;
			logger.info(IdAuthCommonConstants.SESSION_ID, "IDA", "matchScoreCalculator",
					"Threshold Value >>>" + internalScore);
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
	private BIRType getBirType(Object info) {
		BIRType birType = new BIRType();
		if (info instanceof String) {
			String reqInfoStr = (String) info;
			byte[] decodedrefInfo = decodeValue(reqInfoStr);
			birType.setBDB(decodedrefInfo);
		}
		return birType;
	}

	/**
	 * Calculates composite Match score and returns sum value
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 */
	private double matchCompositeValue(Object[] reqInfo, Object[] entityInfo) {
		BIRType[] reqInfobirType = Stream.of(reqInfo).map(this::getBirType).toArray(size -> new BIRType[size]);
		BIRType[] entityInfobirType = Stream.of(entityInfo).map(this::getBirType).toArray(size -> new BIRType[size]);
		CompositeScore compositeScore = bioApi.compositeMatch(reqInfobirType, entityInfobirType, null);
		logger.info(IdAuthCommonConstants.SESSION_ID, "IDA", "matchScoreCalculator",
				"Threshold Value >>>" + compositeScore.getInternalScore());
		return compositeScore.getInternalScore();
	}

	/**
	 * 
	 * Match Multiple values and return calculated score
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 */
	public double matchMultiValue(Map<String, String> reqInfo, Map<String, String> entityInfo) {
		return matchMultiValues(reqInfo, entityInfo);
	}

	/**
	 * Match multiple value for Known entity.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @param matchScore the match score
	 * @return the double
	 */
	private double matchMultiValues(Map<String, String> reqInfo, Map<String, String> entityInfo) {
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
		
		return matchCompositeValue(reqInfoObj, entityInfoObj);
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
