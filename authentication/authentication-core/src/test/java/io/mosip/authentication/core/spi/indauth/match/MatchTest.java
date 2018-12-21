package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;
import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;

public class MatchTest {

	@Test
	public void TestgetMatchProperties() {
		AuthType authType = new AuthType() {

			@Override
			public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isAssociatedMatchType(MatchType matchType) {
				return false;
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
					Function<LanguageType, String> languageInfoFetcher, Environment environment) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
					Function<LanguageType, String> languageInfoFetcher) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public LanguageType getLangType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDisplayName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<MatchType> getAssociatedMatchTypes() {
				return null;
			}
		};

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		IdInfoFetcher languageInfoFetcher=null;
		Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, languageInfoFetcher);

		System.err.println(authType.getLangType());
		System.err.println(authType.getDisplayName());
		System.err.println(authType.getAssociatedMatchTypes());
		MatchType matchType = new MatchType() {

			@Override
			public AuthUsageDataBit getUsedBit() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AuthUsageDataBit getMatchedBit() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public  Function<IdentityDTO, Map<String,List<IdentityInfoDTO>>> getIdentityInfoFunction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IdMapping getIdMapping() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Category getCategory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		AuthType[] authTypes = new AuthType[] { authType };
		AuthType.getAuthTypeForMatchType(matchType, authTypes);
	}

	@Test
	public void TestMatchtype() {
		MatchType matchType = new MatchType() {

			@Override
			public AuthUsageDataBit getUsedBit() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public AuthUsageDataBit getMatchedBit() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Function<IdentityDTO, Map<String,List<IdentityInfoDTO>>> getIdentityInfoFunction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IdMapping getIdMapping() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Category getCategory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		matchType.getAllowedMatchingStrategy(MatchingStrategyType.EXACT);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.PARTIAL);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.PHONETICS);
		matchType.getAllowedMatchingStrategy(MatchingStrategyType.DEFAULT_MATCHING_STRATEGY);
		matchType.getCategory();
		matchType.getLanguageType();
		matchType.getCategory().BIO.getType();
		matchType.getCategory().DEMO.getType();
		matchType.getCategory().OTP.getType();
		MatchType.Category.getCategory("bio");
		MatchingStrategyType.getMatchStrategyType("E");
		IdMapping idMapping = new IdMapping() {

			@Override
			public Function<MappingConfig, List<String>> getMappingFunction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getIdname() {
				// TODO Auto-generated method stub
				return "name";
			}
		};
		String name = "name";
		IdMapping[] authTypes = new IdMapping[] { idMapping };
		IdMapping.getIdMapping(name, authTypes);

	}

}
