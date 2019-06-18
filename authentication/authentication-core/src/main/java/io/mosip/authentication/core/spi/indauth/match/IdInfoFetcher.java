package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;;

/**
 * The IdInfoFetcher interface that provides the helper methods invoked by the
 * classes involved in ID Info matching.
 *
 * @author Loganathan.Sekar
 */
public interface IdInfoFetcher {

	/**
	 * Get Language code for Language type.
	 *
	 * @param langType language type
	 * @return language code
	 */
	public String getLanguageCode(LanguageType langType);

	/**
	 * To check language type.
	 *
	 * @param languageForMatchType the language for match type
	 * @param languageFromReq the language from req
	 * @return true, if successful
	 */
	public boolean checkLanguageType(String languageForMatchType, String languageFromReq);

	/**
	 * Get language name for Match Properties based on language code.
	 *
	 * @param languageCode language code
	 * @return language name
	 */
	public Optional<String> getLanguageName(String languageCode);

	/**
	 * Gets the identity info for the MatchType from the IdentityDTO.
	 *
	 * @param matchType the match type
	 * @param identity  the identity
	 * @param language the language
	 * @return the identity info
	 */
	public Map<String, String> getIdentityRequestInfo(MatchType matchType, RequestDTO identity, String language);

	/**
	 * Get the Validate Otp function.
	 *
	 * @return the ValidateOtpFunction
	 */
	public ValidateOtpFunction getValidateOTPFunction();

	/**
	 * To fetch cbeff values.
	 *
	 * @param idEntity the id entity
	 * @param cbeffDocType the cbeff doc type
	 * @param matchType the match type
	 * @return the cbeff values
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, Entry<String, List<IdentityInfoDTO>>> getCbeffValues(Map<String, List<IdentityInfoDTO>> idEntity,
			CbeffDocType cbeffDocType, MatchType matchType) throws IdAuthenticationBusinessException;

	/**
	 * To get Environment.
	 *
	 * @return the environment
	 */
	public Environment getEnvironment();

	/**
	 * Title info fetcher from Master data manager.
	 *
	 * @return the title fetcher
	 */
	public MasterDataFetcher getTitleFetcher();

	/**
	 * Gets the matching threshold.
	 *
	 * @param key the key
	 * @return the matching threshold
	 */
	public Optional<Integer> getMatchingThreshold(String key);

}
