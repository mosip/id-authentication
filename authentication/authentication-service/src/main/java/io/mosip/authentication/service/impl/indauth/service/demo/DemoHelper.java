package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.service.config.IDAMappingConfig;

@Component
public class DemoHelper {
	
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";
	
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	/** The environment. */
	@Autowired
	private Environment environment;
	
	public Optional<String> getLanguageName(String languageCode) {
		String languagName = null;
		String key = null;
		if (languageCode != null) {
			key = "mosip.phonetic.lang.".concat(languageCode.toLowerCase());
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				String[] split = property.split("-");
				languagName = split[0];
			}
		}
		return Optional.ofNullable(languagName);
	}
	
	public String getLanguageCode(LanguageType langType) {
		if (langType == LanguageType.PRIMARY_LANG) {
			return environment.getProperty(PRIMARY_LANG_CODE);
		} else {
			return environment.getProperty(SECONDARY_LANG_CODE);
		}
	}
	
	public Optional<Object> getIdentityInfo(DemoMatchType matchType, IdentityDTO identity) {
		String language = getLanguageCode(matchType.getLanguageType());
		return Optional.of(identity).flatMap(identityDTO -> getInfo(matchType.getIdentityInfoFunction().apply(identityDTO), language));
	}

	private static Optional<Object> getInfo(List<IdentityInfoDTO> identityInfos, String language) {
		if (identityInfos != null && !identityInfos.isEmpty()) {
			return identityInfos.parallelStream()
					.filter(id -> id.getLanguage() != null && language.equalsIgnoreCase(id.getLanguage()))
					.<Object>map(IdentityInfoDTO::getValue).findAny();
		}
		return Optional.empty();
	}
	
	private static Optional<IdentityValue> getIdentityValue(String name, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoInfo) {
		List<IdentityInfoDTO> identityInfoList = demoInfo.get(name);
		if (identityInfoList != null && !identityInfoList.isEmpty()) {
			return identityInfoList.stream().filter(
					idinfo -> idinfo.getLanguage() != null && idinfo.getLanguage().equalsIgnoreCase(languageCode))
					.map(idInfo -> new IdentityValue(languageCode, idInfo.getValue())).findAny();
		}

		return Optional.empty();
	}

	public List<String> getIdMappingValue(IdMapping idMapping) {
		List<String> mappings = idMapping.getMappingFunction().apply(idMappingConfig);
		List<String> fullMapping = new ArrayList<>();
		for (String mappingStr : mappings) {
			Optional<IdMapping> mappingInternal = IdMapping.getIdMapping(mappingStr);
			if (mappingInternal.isPresent() && idMapping != mappingInternal.get()) {
				List<String> internalMapping = getIdMappingValue(mappingInternal.get());
				fullMapping.addAll(internalMapping);
			} else {
				fullMapping.add(mappingStr);
			}
		}
		return fullMapping;
	}

	private List<IdentityValue> getDemoValue(List<String> propertyNames, String languageCode,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return propertyNames.stream().map(propName -> getIdentityValue(propName, languageCode, demoEntity))
				.filter(val -> val.isPresent()).<IdentityValue>map(Optional::get).collect(Collectors.toList());
	}

	public IdentityValue getEntityInfo(DemoMatchType matchType, Map<String, List<IdentityInfoDTO>> demoEntity) {
		String languageCode = getLanguageCode(matchType.getLanguageType()).toLowerCase();
		Optional<String> languageName = getLanguageName(languageCode);
		List<String> propertyNames = getIdMappingValue(matchType.getIdMapping());
		List<IdentityValue> demoValues = getDemoValue(propertyNames, languageCode, demoEntity);
		String[] demoValuesStr = demoValues.stream().map(IdentityValue::getValue).toArray(size -> new String[size]);
		String demoValue = concatValues(demoValuesStr);
		String entityInfo = matchType.getEntityInfoFetcher().apply(demoValue);
		return new IdentityValue(languageName.orElse(""), entityInfo);
	}
	
	public static String concatValues(String... demoValues) {
		StringBuilder demoBuilder = new StringBuilder();
		for (int i = 0; i < demoValues.length; i++) {
			String demo = demoValues[i];
			if (null != demo && demo.length() > 0) {
				demoBuilder.append(demo);
				if (i < demoValues.length - 1) {
					demoBuilder.append(" ");
				}
			}
		}
		return demoBuilder.toString();
	}
	
	


}
