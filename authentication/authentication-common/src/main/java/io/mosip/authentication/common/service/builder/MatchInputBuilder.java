package io.mosip.authentication.common.service.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * 
 * Builder class to match Inputs
 *
 * @author Dinesh Karuppiah.T
 *
 */
@Component
public class MatchInputBuilder {

	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	public static final int DEFAULT_EXACT_MATCH_VALUE = 100;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	private BioMatcherUtil bioMatcherUtil;

	@Autowired
	private IdInfoHelper idInfoHelper;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The Constant DEFAULT_MATCH_VALUE. */
	public static final String DEFAULT_MATCH_VALUE = "demo.threshold";

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param authTypes      the auth types
	 * @param matchTypes     the match types
	 * @return the list
	 */
	public List<MatchInput> buildMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes,
			MatchType[] matchTypes) {
		Set<String> languages = idInfoHelper.getAllowedLang();
		return Stream.of(matchTypes).flatMap(matchType -> {
			List<MatchInput> matchInputs = new ArrayList<>();
			if (matchType.isMultiLanguage()) {
				for (String language : languages) {
					addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, language);
				}
			} else {
				addMatchInput(authRequestDTO, authTypes, matchType, matchInputs, null);
			}
			return matchInputs.stream();
		}).filter(Objects::nonNull).collect(Collectors.toList());

	}

	/**
	 * Add MatchInput
	 * 
	 * @param authRequestDTO
	 * @param authTypes
	 * @param matchType
	 * @param matchInputs
	 * @param language
	 */
	private void addMatchInput(AuthRequestDTO authRequestDTO, AuthType[] authTypes, MatchType matchType,
			List<MatchInput> matchInputs, String language) {
		Map<String, String> infoFromAuthRequest = matchType.getReqestInfoFunction().apply(authRequestDTO);
		Optional<AuthType> authTypeOpt = AuthType.getAuthTypeForMatchType(matchType, authTypes);
		if (authTypeOpt.isPresent()) {
			matchInputs
					.add(buildMatchInput(authRequestDTO, matchType, infoFromAuthRequest, authTypeOpt.get(), language));
		}
	}

	/**
	 * Build Match Input
	 * 
	 * @param authRequestDTO
	 * @param matchType
	 * @param infoFromAuthRequest
	 * @param authType
	 * @param language
	 * @return
	 */
	private MatchInput buildMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType,
			Map<String, String> infoFromAuthRequest, AuthType authType, String language) {
		if (infoFromAuthRequest.isEmpty()) {
			// For Identity
			Optional<RequestDTO> identityOpt = Optional.ofNullable(authRequestDTO.getRequest());
			if (identityOpt.isPresent()) {
				RequestDTO identity = identityOpt.get();
				if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)
						&& idInfoFetcher.getIdentityRequestInfo(matchType, identity, language).size() > 0) {
					return contstructMatchInput(authRequestDTO, matchType, authType, language);
				}
			}
		} else {
			// For non-identity
			if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)
					&& authType.isAuthTypeInfoAvailable(authRequestDTO)) {
				return contstructMatchInput(authRequestDTO, matchType, authType, null);
			}
		}
		return null;
	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param matchType      TODO
	 * @param authType       TODO
	 * @param language       the language
	 * @return the list
	 */
	private MatchInput contstructMatchInput(AuthRequestDTO authRequestDTO, MatchType matchType, AuthType authType,
			String language) {

		if (matchType.getCategory() == Category.BIO && !authType.isAuthTypeInfoAvailable(authRequestDTO)) {
			return null;
		} else {
			Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
			String matchingStrategy = MatchingStrategyType.DEFAULT_MATCHING_STRATEGY.getType();

			Optional<String> matchingStrategyOpt = authType.getMatchingStrategy(authRequestDTO, language);
			if (matchingStrategyOpt.isPresent()) {
				matchingStrategy = matchingStrategyOpt.get();
				if (matchingStrategyOpt.get().equals(MatchingStrategyType.PARTIAL.getType())
						|| matchingStrategyOpt.get().equals(MatchingStrategyType.PHONETICS.getType())) {
					Optional<Integer> matchThresholdOpt = authType.getMatchingThreshold(authRequestDTO, language,
							environment, idInfoFetcher);
					matchValue = matchThresholdOpt.orElseGet(() -> Integer
							.parseInt(environment.getProperty(IdAuthConfigKeyConstants.DEFAULT_MATCH_VALUE)));
				}
			}
			Map<String, Object> matchProperties = authType.getMatchProperties(authRequestDTO, idInfoFetcher, language);

			return new MatchInput(authType, matchType, matchingStrategy, matchValue, matchProperties, language);
		}
	}

}
