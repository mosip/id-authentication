package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 *
 * @author Rakesh Roshan
 */
public enum BioMatchType implements MatchType {

	// Left Finger Minutiea
	FGRMIN_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftThumb,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftIndex,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftMiddle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftRing,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftLittle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	// Right Finger Minutiea
	FGRMIN_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightThumb,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightIndex,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightMiddle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightRing,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),
	FGRMIN_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightLittle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_MINUTIAE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_MINUTIAE),

	// Left Finger Image FGRIMG
	FGRIMG_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftThumb,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftIndex,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftMiddle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftRing,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getLeftLittle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),

	// Right Finger Image
	FGRIMG_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightThumb,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightIndex,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightMiddle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightRing,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE),
	FGRIMG_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), IdentityDTO::getRightLittle,
			AuthUsageDataBit.USED_BIO_FINGERPRINT_IMAGE, AuthUsageDataBit.MATCHED_BIO_FINGERPRINT_IMAGE);

	/** The mosipLogger. */
	private static final Logger mosipLogger = IdaLogger.getLogger(BioMatchType.class);

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	private Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction;

	private IdMapping idMapping;

	BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,
			Function<IdentityDTO, List<IdentityInfoDTO>> identityInfoFunction, AuthUsageDataBit usedBit,
			AuthUsageDataBit matchedBit) {
		this.idMapping = idMapping;
		this.identityInfoFunction = identityInfoFunction;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
		this.usedBit = usedBit;
		this.matchedBit = matchedBit;
	}

	/**
	 * Gets the allowed matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @return the allowed matching strategy
	 */
	public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
		return allowedMatchingStrategy.stream().filter(ms -> ms.getType().equals(matchStrategyType)).findAny();
	}

	/**
	 * Gets the entity info.
	 *
	 * @return the entity info
	 */
	public Function<String, String> getEntityInfoMapper() {
		return Function.identity();
	}

	/**
	 * Gets the used bit.
	 *
	 * @return the used bit
	 */
	public AuthUsageDataBit getUsedBit() {
		return usedBit;
	}

	/**
	 * Gets the matched bit.
	 *
	 * @return the matched bit
	 */
	public AuthUsageDataBit getMatchedBit() {
		return matchedBit;
	}

	/**
	 * Sets the of.
	 *
	 * @param matchingStrategies the matching strategies
	 * @return the sets the
	 */
	public static Set<MatchingStrategy> setOf(MatchingStrategy... matchingStrategies) {
		return Stream.of(matchingStrategies).collect(Collectors.toSet());

	}

	public IdMapping getIdMapping() {
		return idMapping;
	}

	@Override
	public Function<IdentityDTO, List<IdentityInfoDTO>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}

	private static Logger getLogger() {
		return mosipLogger;
	}

	@Override
	public LanguageType getLanguageType() {
		return LanguageType.PRIMARY_LANG;
	}

	@Override
	public Category getCategory() {
		return Category.BIO;
	}
}
