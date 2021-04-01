package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.mosip.authentication.core.dto.DemoMatcherUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.demoauth.DemoNormalizer;

/**
 * The Interface MatchingStrategy.
 * @author  Arun Bose
 */
public interface TextMatchingStrategy extends MatchingStrategy {

	public default int match(Map<String, String> reqValues, Map<String, String> entityValues, Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		String reqInfo = reqValues.values().stream().collect(Collectors.joining(" "));
		String entityInfo = entityValues.values().stream().collect(Collectors.joining(" "));
		return  getMatchFunction().match(reqInfo, entityInfo, matchProperties);
	}
	
	public static int normalizeAndMatch(Object reqInfo, 
			Object entityInfo, 
			Map<String, Object> props,
			NormalizeFunction normalizeFunction, 
			BiFunction<String, String, Integer> matchFunction) throws IdAuthenticationBusinessException {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Object demoNormalizerObject=  props.get("demoNormalizer");
			Object langObject=props.get("langCode");
			if(demoNormalizerObject instanceof  DemoNormalizer && langObject instanceof String) {
				DemoNormalizer demoNormalizer=(DemoNormalizer)demoNormalizerObject;
			    String langCode=(String)langObject;
				String refInfoText = normalizeFunction.normalizeText(demoNormalizer, (String) reqInfo, langCode, props);
				String entityInfoText = normalizeFunction.normalizeText(demoNormalizer, (String) entityInfo, langCode, props);
				return matchFunction.apply(refInfoText, entityInfoText);
			} else {
				//If language code is not present or demo normalizer is not present, directly perform match
				return matchFunction.apply((String) reqInfo, (String) entityInfo);
			}
		} else {
			return reqInfo.equals(entityInfo) ? DemoMatcherUtil.EXACT_MATCH_VALUE : 0;
		}
	}
	

	@FunctionalInterface
	public static interface NormalizeFunction {
		String normalizeText(DemoNormalizer demoNormalizer, String inputText, String langCode, Map<String, Object> props) throws IdAuthenticationBusinessException;
	}
	
	
}
