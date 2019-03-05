package io.mosip.authentication.service.impl.indauth.service.bio;
import static io.mosip.authentication.core.spi.indauth.match.MatchType.setOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.BioIdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;;

/**
 * 
 *
 * @author Rakesh Roshan
 */
public enum BioMatchType implements MatchType {

	// Left Finger Minutiea
	FGRMIN_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR, 
			SingleAnySubtypeType.LEFT,SingleAnySubtypeType.THUMB),
	FGRMIN_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,SingleAnySubtypeType.INDEX_FINGER),
	FGRMIN_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,SingleAnySubtypeType.MIDDLE_FINGER),
	FGRMIN_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,SingleAnySubtypeType.RING_FINGER),
	FGRMIN_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,SingleAnySubtypeType.LITTLE_FINGER),
	// Right Finger Minutiea
	FGRMIN_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.THUMB),
	FGRMIN_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.INDEX_FINGER),
	FGRMIN_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.MIDDLE_FINGER),
	FGRMIN_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.RING_FINGER),
	FGRMIN_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.LITTLE_FINGER),

	// Left Finger Image FGRIMG
	FGRIMG_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,
			SingleAnySubtypeType.THUMB),
	FGRIMG_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,
			SingleAnySubtypeType.INDEX_FINGER),
	FGRIMG_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,
			SingleAnySubtypeType.MIDDLE_FINGER),
	FGRIMG_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,
			SingleAnySubtypeType.RING_FINGER),
	FGRIMG_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.LEFT,
			SingleAnySubtypeType.LITTLE_FINGER),

	// Right Finger Image
	FGRIMG_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,
			SingleAnySubtypeType.THUMB),
	FGRIMG_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,
			SingleAnySubtypeType.INDEX_FINGER),
	FGRIMG_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.MIDDLE_FINGER),
	FGRIMG_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), 
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT,
			SingleAnySubtypeType.RING_FINGER),
	FGRIMG_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR,SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.LITTLE_FINGER),

	// Multi-fingerPrint
	//FIXME get Bio ID info of all fingers and return the map
	FGRMIN_MULTI(IdaIdMapping.FINGERPRINT, setOf(MultiFingerprintMatchingStrategy.PARTIAL),
			CbeffDocType.FMR, null, null, identityDto -> getIdValuesMap(identityDto,FGRMIN_LEFT_THUMB,FGRMIN_LEFT_INDEX,FGRMIN_LEFT_MIDDLE,FGRMIN_LEFT_RING,FGRMIN_LEFT_LITTLE,
					                                                            FGRMIN_RIGHT_THUMB,FGRMIN_RIGHT_INDEX,FGRMIN_RIGHT_MIDDLE,FGRMIN_RIGHT_RING,FGRMIN_RIGHT_LITTLE)),

	RIGHT_IRIS(IdaIdMapping.RIGHTEYE, setOf(IrisMatchingStrategy.PARTIAL),
			CbeffDocType.IRIS, SingleAnySubtypeType.RIGHT,null),

	LEFT_IRIS(IdaIdMapping.LEFTEYE, setOf(IrisMatchingStrategy.PARTIAL), 
			CbeffDocType.IRIS,SingleAnySubtypeType.LEFT, null),
	
	//FIXME get Bio ID info of all eyes and return the map
	IRIS_COMP(IdaIdMapping.IRIS, setOf(CompositeIrisMatchingStrategy.PARTIAL), CbeffDocType.IRIS, null, null,identityDTO->getIdValuesMap(identityDTO,LEFT_IRIS,RIGHT_IRIS)),
	
	FACE(IdaIdMapping.FACE, Collections.emptySet(), CbeffDocType.FACE, null, null);

	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The used bit. */
	private AuthUsageDataBit usedBit;

	/** The matched bit. */
	private AuthUsageDataBit matchedBit;

	private Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction;

	private IdMapping idMapping;

	private CbeffDocType cbeffDocType;

	private BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,CbeffDocType cbeffDocType, SingleAnySubtypeType subType, SingleAnySubtypeType singleSubtype) {
		this(idMapping, allowedMatchingStrategy,cbeffDocType,subType,singleSubtype, null);
		this.identityInfoFunction = (IdentityDTO identityDTO) -> {
			Optional<String> valueOpt = identityDTO.getBiometrics()
						.stream()
					.filter(bioId -> {
						if (bioId.getType().equalsIgnoreCase(cbeffDocType.getType().name())) {
							if (bioId.getType().equalsIgnoreCase(CbeffDocType.FMR.getType().name())) {
								return bioId.getSubType().equalsIgnoreCase(subType.name() + "_" + singleSubtype.name());
							} else if (bioId.getType().equalsIgnoreCase(CbeffDocType.IRIS.getType().name())) {
								return bioId.getSubType().equalsIgnoreCase(subType.name());
							} else if (bioId.getType().equalsIgnoreCase(CbeffDocType.FACE.getType().name())) {
								return true;
							}
						}
						return false;
					})
						.map(BioIdentityInfoDTO::getValue)
						.findAny();
			if(valueOpt.isPresent()) {
				Map<String, List<IdentityInfoDTO>> valuesMap = new  HashMap<>();
				List<IdentityInfoDTO> values = Arrays.asList(new IdentityInfoDTO(null, valueOpt.get()));
				valuesMap.put(idMapping.getIdname(), values );
				return valuesMap;
			}
			return Collections.emptyMap();
		};
	}

	private BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy,CbeffDocType cbeffDocType, SingleAnySubtypeType subType, SingleAnySubtypeType singleSubtype,
			Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction) {
		this.idMapping = idMapping;
		this.cbeffDocType = cbeffDocType;
		this.identityInfoFunction = identityInfoFunction;
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
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
	public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper() {
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

	public IdMapping getIdMapping() {
		return idMapping;
	}

	@Override
	public Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}

	@Override
	public Category getCategory() {
		return Category.BIO;
	}

	@Override
	public Map<String, Entry<String, List<IdentityInfoDTO>>> mapEntityInfo(Map<String, List<IdentityInfoDTO>> idEntity,
			IdInfoFetcher idinfoFetcher) throws IdAuthenticationBusinessException {
		return idinfoFetcher.getCbeffValues(idEntity, cbeffDocType, this);
	}

	public CbeffDocType getCbeffDocType() {
		return cbeffDocType;
	}
	
	public static Map<String, List<IdentityInfoDTO>> getIdValuesMap(IdentityDTO identityDto, BioMatchType... bioMatchTypes) {
	  return Stream.of(bioMatchTypes)
			  		.flatMap(bioMatchType -> 
			  						bioMatchType.getIdentityInfoFunction()
			  									.apply(identityDto)
			  									.entrySet()
			  									.stream())
			  		.collect(Collectors.toMap(Entry::getKey, Entry::getValue, 
			  										(list1, list2) 
			  											-> Stream.concat(list1.stream(), list1.stream()).collect(Collectors.toList())));	
	}
	
	
}
