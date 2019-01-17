package io.mosip.authentication.core.spi.indauth.match;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;;

/**
 * The IdInfoFetcher interface that provides the helper methods invoked by the
 * classes involved in ID Info matching.
 *
 * @author Loganathan.Sekaran
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
	 * @return the identity info
	 */
	public Map<String, String> getIdentityInfo(MatchType matchType, IdentityDTO identity);

	/**
	 * Gets the iris provider for the BioInfo value.
	 *
	 * @param bioinfovalue the bioinfovalue
	 * @return the iris provider
	 */
	public MosipBiometricProvider getIrisProvider(BioInfo bioinfovalue);

	/**
	 * Gets the finger print provider for the BioInfo value.
	 *
	 * @param bioinfovalue the bioinfovalue
	 * @return the finger print provider
	 */
	public MosipBiometricProvider getFingerPrintProvider(BioInfo bioinfovalue);

}
