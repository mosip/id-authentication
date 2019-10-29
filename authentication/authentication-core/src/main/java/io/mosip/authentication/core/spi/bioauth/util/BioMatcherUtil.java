package io.mosip.authentication.core.spi.bioauth.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * 
 * Util class to calculate Composite and Individual's Match score based on
 * Request and Entity info's
 * 
 * @author Dinesh Karuppiah.T
 */
@Component
public class BioMatcherUtil {

	@Autowired(required = false)
	@Qualifier("finger")
	private IBioApi fingerApi;
	
	@Autowired(required = false)
	@Qualifier("face")
	private IBioApi faceApi;
	
	@Autowired(required = false)
	@Qualifier("iris")
	private IBioApi irisApi;
	
	@Autowired(required = false)
	@Qualifier("composite.biometrics")
	private IBioApi compositeBiometricApi;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	Environment environment;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(BioMatcherUtil.class);

	/**
	 * Match the request and entity values and return calculated score.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param properties 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public double matchValue(Map<String, String> reqInfo, Map<String, String> entityInfo, Map<String, Object> properties)
			throws IdAuthenticationBusinessException {
		IdMapping[] idMappings = (IdMapping[]) properties.get(IdMapping.class.getSimpleName()); 
		BIR[][] objArrays = getBirValues(reqInfo, entityInfo, idMappings);
		BIR[] reqInfoObj = objArrays[0];
		BIR[] entityBIR = objArrays[1];
		Optional<BIR> reqBIR = Stream.of(reqInfoObj)
				.findFirst();
		if (reqBIR.isPresent()) {
			Score[] match;
			try {
				logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA", "matchValue",
						"entityBIR size >>>" + entityBIR.length);
				match = getBioSdkInstance(reqBIR.get().getBdbInfo().getFormat().getType()).match(reqBIR.get(), entityBIR, null);
				logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA", "matchValue",
						"match size >>>" + match.length);
				Arrays.asList(match).stream().forEach(score -> logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA",
						"matchValue", "internal score Value >>>" + score.getInternalScore()));
				return Stream.of(match).mapToLong(Score::getInternalScore).max().orElse(0);
			} catch (BiometricException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "matchValue", "Biovalue not Matched");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}
		}
		return 0;
	}

	/**
	 * To create BIRType based on requested input.
	 *
	 * @param info
	 *            the info
	 * @return the bir
	 */
	private BIR getBir(Object info, String type) {
		BIRBuilder birBuilder = new BIRBuilder();
		if (info instanceof String) {
			RegistryIDType format = new RegistryIDType();
			format.setOrganization(String.valueOf(CbeffConstant.FORMAT_OWNER));
			format.setType(type);
			BDBInfo bdbInfo = new BDBInfo.BDBInfoBuilder().withFormat(format).build();
			String reqInfoStr = (String) info;
			byte[] decodedrefInfo = decodeValue(reqInfoStr);
			birBuilder.withBdb(decodedrefInfo);
			birBuilder.withBdbInfo(bdbInfo);
		}
		return birBuilder.build();
	}

	/**
	 * Calculates composite Match score and returns sum value.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param type 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private double matchCompositeValue(BIR[] reqBIR, BIR[] entityBIR)
			throws IdAuthenticationBusinessException {
		CompositeScore compositeScore;
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA", "matchCompositeValue",
					"entityBIR size >>>" + entityBIR.length);
			compositeScore = compositeBiometricApi.compositeMatch(reqBIR, entityBIR, null);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA", "matchCompositeValue ",
					"composite Score >>>" + compositeScore.getInternalScore());
			Arrays.asList(compositeScore.getIndividualScores()).stream()
					.forEach(score -> logger.debug(IdAuthCommonConstants.SESSION_ID, "IDA", "matchCompositeValue",
							"individual score Value >>>" + score.getInternalScore()));
		} catch (BiometricException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "matchScoreCalculator", "Biovalue not Matched");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);

		}
		return compositeScore.getInternalScore();
	}

	/**
	 * Match Multiple values and return calculated score.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param properties 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public double matchMultiValue(Map<String, String> reqInfo, Map<String, String> entityInfo, Map<String, Object> properties)
			throws IdAuthenticationBusinessException {
		return matchMultiValues(reqInfo, entityInfo, properties);
	}

	/**
	 * Match multiple value for Known entity.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param type 
	 * @return the double
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private double matchMultiValues(Map<String, String> reqInfo, Map<String, String> entityInfo, Map<String, Object> properties)
			throws IdAuthenticationBusinessException {
		IdMapping[] idMappings = (IdMapping[]) properties.get(IdMapping.class.getSimpleName()); 
		BIR[][] objArrays = getBirValues(reqInfo, entityInfo, idMappings);
		BIR[] reqInfoObj = objArrays[0];
		BIR[] entityInfoObj = objArrays[1];
		return matchCompositeValue(reqInfoObj, entityInfoObj);
	}

	/**
	 * Match values.
	 *
	 * @param reqInfo
	 *            the req info
	 * @param entityInfo
	 *            the entity info
	 * @param idMappings 
	 * @return the object[][]
	 */
	private BIR[][] getBirValues(Map<String, String> reqInfo, Map<String, String> entityInfo, IdMapping[] idMappings) {
		BIR[] reqInfoObj;
		BIR[] entityInfoObj;

		int index = 0;
		if (reqInfo.keySet().stream().noneMatch(key -> key.startsWith(IdAuthCommonConstants.UNKNOWN_BIO))) {
			reqInfoObj = new BIR[reqInfo.size()];
			entityInfoObj = new BIR[reqInfo.size()];

			for (Map.Entry<String, String> e : reqInfo.entrySet()) {
				String key = e.getKey();
				String type = getType(key, idMappings);
				
				reqInfoObj[index] = getBir(e.getValue(), type);
				entityInfoObj[index] = getBir(entityInfo.get(key), type);
				index++;
			}
		} else {
			Function<? super Entry<String, String>, ? extends BIR> birMapper = e -> getBir(e.getValue(), getType(e.getKey(), idMappings));
			reqInfoObj = reqInfo.entrySet().stream()
							.map(birMapper)
							.toArray(s -> new BIR[s]);
			entityInfoObj = entityInfo.entrySet()
								.stream()
								.map(birMapper)
								.toArray(s -> new BIR[s]);
		}

		return new BIR[][] { reqInfoObj, entityInfoObj };
	}

	private String getType(String idName, IdMapping[] idMappings) {
		String typeForIdName = idInfoFetcher.getTypeForIdName(idName, idMappings).orElse("");
		long type = 0L;
		if(typeForIdName.equalsIgnoreCase(SingleType.FINGER.value())) {
			type = CbeffConstant.FORMAT_TYPE_FINGER; 
		} else if(typeForIdName.equalsIgnoreCase(SingleType.IRIS.value())) {
			type = CbeffConstant.FORMAT_TYPE_IRIS; 
		} else if(typeForIdName.equalsIgnoreCase(SingleType.FACE.value())) {
			type = CbeffConstant.FORMAT_TYPE_FACE; 
		}
		//Note: Finger minutiea type not handled based on the requirement
		return String.valueOf(type);
	}

	/**
	 * Decode value.
	 *
	 * @param value
	 *            the value
	 * @return the byte[]
	 */
	private static byte[] decodeValue(String value) {
		return CryptoUtil.decodeBase64(value);
	}

	private IBioApi getBioSdkInstance(String type) throws IdAuthenticationBusinessException {
		if (String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER).equals(type)
				|| String.valueOf(CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE).equals(type)) {
			return fingerApi;
		} else if (String.valueOf(CbeffConstant.FORMAT_TYPE_FACE).equals(type)) {
			return faceApi;
		} else if (String.valueOf(CbeffConstant.FORMAT_TYPE_IRIS).equals(type)) {
			return irisApi;
		} else {
			logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "getBioSdkInstance",
					"requested single type not found : " + type);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}
	
}
;