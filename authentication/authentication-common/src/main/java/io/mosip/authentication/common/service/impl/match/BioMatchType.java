package io.mosip.authentication.common.service.impl.match;

import static io.mosip.authentication.core.spi.indauth.match.MatchType.setOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.biometrics.entities.SingleAnySubtypeType;
/**
 * 
 * Match type for Bio Entity
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 *
 */
public enum BioMatchType implements MatchType {

	// Left Finger Minutiea
	FGRMIN_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.THUMB),
	FGRMIN_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.INDEX_FINGER),
	FGRMIN_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.MIDDLE_FINGER),
	FGRMIN_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.RING_FINGER),
	FGRMIN_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.LITTLE_FINGER),
	// Right Finger Minutiea
	FGRMIN_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.THUMB),
	FGRMIN_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.INDEX_FINGER),
	FGRMIN_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.MIDDLE_FINGER),
	FGRMIN_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.RING_FINGER),
	FGRMIN_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), CbeffDocType.FMR,
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.LITTLE_FINGER),
	// unknown finger Minutiea
	FGRMIN_UNKNOWN(IdaIdMapping.UNKNOWN_FINGER, CbeffDocType.FMR, null, null,
			setOf(MultiFingerprintMatchingStrategy.PARTIAL)),

	// Left Finger Image FGRIMG
	FGRIMG_LEFT_THUMB(IdaIdMapping.LEFTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.THUMB),
	FGRIMG_LEFT_INDEX(IdaIdMapping.LEFTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.INDEX_FINGER),
	FGRIMG_LEFT_MIDDLE(IdaIdMapping.LEFTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.MIDDLE_FINGER),
	FGRIMG_LEFT_RING(IdaIdMapping.LEFTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.RING_FINGER),
	FGRIMG_LEFT_LITTLE(IdaIdMapping.LEFTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.LEFT, SingleAnySubtypeType.LITTLE_FINGER),

	// Right Finger Image
	FGRIMG_RIGHT_THUMB(IdaIdMapping.RIGHTTHUMB, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.THUMB),
	FGRIMG_RIGHT_INDEX(IdaIdMapping.RIGHTINDEX, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.INDEX_FINGER),
	FGRIMG_RIGHT_MIDDLE(IdaIdMapping.RIGHTMIDDLE, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.MIDDLE_FINGER),
	FGRIMG_RIGHT_RING(IdaIdMapping.RIGHTRING, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.RING_FINGER),
	FGRIMG_RIGHT_LITTLE(IdaIdMapping.RIGHTLITTLE, setOf(FingerPrintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR),
			SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.LITTLE_FINGER),
	
	FGRIMG_UNKNOWN(IdaIdMapping.UNKNOWN_FINGER, setOf(CbeffDocType.FINGER, CbeffDocType.FMR), null, null,
			setOf(MultiFingerprintMatchingStrategy.PARTIAL)),

	// Multi-fingerPrint
	// FIXME get Bio ID info of all fingers and return the map
	FGRMIN_COMPOSITE(IdaIdMapping.FINGERPRINT, setOf(MultiFingerprintMatchingStrategy.PARTIAL), CbeffDocType.FMR, null,
			null),

	FGRMIN_MULTI(IdaIdMapping.FINGERPRINT, setOf(MultiFingerprintMatchingStrategy.PARTIAL), CbeffDocType.FMR, null,
			null),

	FGRIMG_COMPOSITE(IdaIdMapping.FINGERPRINT, setOf(MultiFingerprintMatchingStrategy.PARTIAL), setOf(CbeffDocType.FINGER, CbeffDocType.FMR), null,
			null),
	
	FGRIMG_MULTI(IdaIdMapping.FINGERPRINT, setOf(MultiFingerprintMatchingStrategy.PARTIAL), CbeffDocType.FINGER, null,
			null),

	RIGHT_IRIS(IdaIdMapping.RIGHTIRIS, setOf(IrisMatchingStrategy.PARTIAL), CbeffDocType.IRIS,
			SingleAnySubtypeType.RIGHT, null),

	LEFT_IRIS(IdaIdMapping.LEFTIRIS, setOf(IrisMatchingStrategy.PARTIAL), CbeffDocType.IRIS, SingleAnySubtypeType.LEFT,
			null),

	// FIXME get Bio ID info of all eyes and return the map
	IRIS_COMP(IdaIdMapping.IRIS, setOf(CompositeIrisMatchingStrategy.PARTIAL), CbeffDocType.IRIS, null, null),
	// unknown IRIS
	IRIS_UNKNOWN(IdaIdMapping.UNKNOWN_IRIS, CbeffDocType.IRIS, null, null,
			setOf(CompositeIrisMatchingStrategy.PARTIAL)),

	FACE(IdaIdMapping.FACE, setOf(FaceMatchingStrategy.PARTIAL), CbeffDocType.FACE, null, null),
	
	MULTI_MODAL(IdaIdMapping.MULTI_MODAL_BIOMETRICS, CbeffDocType.values(), null, null, setOf(MultiModalBiometricsMatchingStrategy.PARTIAL));


	/** The allowed matching strategy. */
	private Set<MatchingStrategy> allowedMatchingStrategy;

	/** The identity info function. */
	private Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction;

	/** The id mapping. */
	private IdMapping idMapping;

	/** The cbeff doc type. */
	private CbeffDocType[] cbeffDocTypes;

	/** The sub type. */
	private SingleAnySubtypeType subType;

	/** The single any subtype. */
	private SingleAnySubtypeType singleAnySubtype;
	
	private BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy, Set<CbeffDocType> cbeffDocTypes,
			SingleAnySubtypeType subType, SingleAnySubtypeType singleAnySubtype) {
		this(idMapping,cbeffDocTypes.toArray(s -> new CbeffDocType[s]), subType, singleAnySubtype,allowedMatchingStrategy);
	}

	/**
	 * Instantiates a new bio match type.
	 *
	 * @param idMapping               the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param cbeffDocType            the cbeff doc type
	 * @param subType                 the sub type
	 * @param singleAnySubtype        the single any subtype
	 */
	private BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy, CbeffDocType cbeffDocType,
			SingleAnySubtypeType subType, SingleAnySubtypeType singleAnySubtype) {
		this(idMapping,new CbeffDocType[] {cbeffDocType}, subType, singleAnySubtype,allowedMatchingStrategy);
	}
	
	private BioMatchType(IdMapping idMapping, CbeffDocType[] cbeffDocType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleAnySubtype, Set<MatchingStrategy> allowedMatchingStrategy) {
		// This constructor should only called for Composite Match types, not by
		// individual/unknown match types. This is because here we get id info based on
		// sub-idmappings if available
		this(idMapping, allowedMatchingStrategy, cbeffDocType, subType, singleAnySubtype, null);
		Set<IdMapping> subIdMappings = idMapping.getSubIdMappings();
		if (subIdMappings.isEmpty()) {
			this.identityInfoFunction = requestDto -> getIdInfoFromBioIdInfo(requestDto.getBiometrics());
		} else {
			this.identityInfoFunction = requestDto -> getIdInfoFromSubIdMappings(requestDto, subIdMappings);
		}
	}
	
	private BioMatchType(IdMapping idMapping, CbeffDocType cbeffDocType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleAnySubtype, Set<MatchingStrategy> allowedMatchingStrategy) {
		this(idMapping, setOf(cbeffDocType), subType, singleAnySubtype, allowedMatchingStrategy);
	}

	/**
	 * Instantiates a new bio match type for UNKNOWN scenarios
	 *
	 * @param idMapping               the id mapping
	 * @param cbeffDocType            the cbeff doc type
	 * @param subType                 the sub type
	 * @param singleAnySubtype        the single any subtype
	 * @param allowedMatchingStrategy the allowed matching strategy
	 */

	private BioMatchType(IdMapping idMapping, Set<CbeffDocType> cbeffDocType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleAnySubtype, Set<MatchingStrategy> allowedMatchingStrategy) {
		// This constructor is called for UNKNOWN match types only. Make sure its id
		// info function only calls by self instead of its sub-idmappings
		this(idMapping, allowedMatchingStrategy, cbeffDocType.toArray(s -> new CbeffDocType[s]), subType, singleAnySubtype, null);
		this.identityInfoFunction = requestDto -> getIdInfoFromBioIdInfo(requestDto.getBiometrics());
	}

	/**
	 * Instantiates a new bio match type.
	 *
	 * @param idMapping               the id mapping
	 * @param allowedMatchingStrategy the allowed matching strategy
	 * @param cbeffDocType            the cbeff doc type
	 * @param subType                 the sub type
	 * @param singleAnySubtype        the single any subtype
	 * @param identityInfoFunction    the identity info function
	 */
	private BioMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy, CbeffDocType[] cbeffDocTypes,
			SingleAnySubtypeType subType, SingleAnySubtypeType singleAnySubtype,
			Function<IdentityDTO, Map<String, List<IdentityInfoDTO>>> identityInfoFunction) {
		this.idMapping = idMapping;
		this.cbeffDocTypes = cbeffDocTypes;
		this.subType = subType;
		this.singleAnySubtype = singleAnySubtype;
		this.identityInfoFunction = requestDto -> identityInfoFunction.apply(requestDto.getDemographics());
		this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
	}

	/**
	 * Gets the id info from bio id info.
	 *
	 * @param biometrics the biometrics
	 * @return the id info from bio id info
	 */
	private Map<String, List<IdentityInfoDTO>> getIdInfoFromBioIdInfo(List<BioIdentityInfoDTO> biometrics) {
		if(biometrics == null) {
			return Collections.emptyMap();
		}
		AtomicInteger count = new AtomicInteger(0);
		return biometrics.stream().filter(bioId -> {
			Optional<AuthType> authType = AuthType.getAuthTypeForMatchType(this, BioAuthType.values());
			if (authType.isPresent() && bioId.getData() != null && bioId.getData().getBioType() != null && bioId.getData().getBioType().equalsIgnoreCase(authType.get().getType())) {
				return authType.get() == BioAuthType.FACE_IMG || 
						(bioId.getData().getBioSubType() != null && 
						bioId.getData().getBioSubType().equalsIgnoreCase(getIdMapping().getSubType()));
			}
			return false;
		}).map(BioIdentityInfoDTO::getData).map(DataDTO::getBioValue)
				.map(value -> Arrays.asList(new IdentityInfoDTO(null, value))).collect(Collectors.toMap(value -> {
					String idname = idMapping.getIdname();
					if (idname.contains(IdAuthCommonConstants.UNKNOWN_BIO)) {
						int countVal = count.incrementAndGet();
						idname = idname.replace(IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER, String.valueOf(countVal));
					}
					return idname;
				}, value -> value));
	}

	/**
	 * Gets the id info from sub id mappings.
	 *
	 * @param identityDto   the identity dto
	 * @param subIdMappings the sub id mappings
	 * @return the id info from sub id mappings
	 */

	private Map<String, List<IdentityInfoDTO>> getIdInfoFromSubIdMappings(RequestDTO identityDto,
			Set<IdMapping> subIdMappings) {
		BioMatchType[] subMatchTypes = getMatchTypesForSubIdMappings(subIdMappings);
		return getIdValuesMap(identityDto, subMatchTypes);
	}

	/**
	 * Gets the match types for sub id mappings.
	 *
	 * @param subIdMappings the sub id mappings
	 * @return the match types for sub id mappings
	 */

	public BioMatchType[] getMatchTypesForSubIdMappings(Set<IdMapping> subIdMappings) {
		List<CbeffDocType> cbeffDocTypes = Arrays.asList(this.getCbeffDocTypes());
		return Arrays.stream(BioMatchType.values())
				.filter(bioMatchType -> subIdMappings.contains(bioMatchType.getIdMapping()))
				.filter(bioMatchType -> cbeffDocTypes.containsAll(Arrays.asList(bioMatchType.getCbeffDocTypes())))
				.toArray(size -> new BioMatchType[size]);
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
	public BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> getEntityInfoMapper() {
		return (entity, props) -> entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getIdMapping()
	 */
	public IdMapping getIdMapping() {
		return idMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#
	 * getIdentityInfoFunction()
	 */
	@Override
	public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
		return identityInfoFunction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchType#getCategory()
	 */
	@Override
	public Category getCategory() {
		return Category.BIO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.MatchType#mapEntityInfo(java.
	 * util.Map, io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public Map<String, Entry<String, List<IdentityInfoDTO>>> mapEntityInfo(Map<String, List<IdentityInfoDTO>> idEntity,
			IdInfoFetcher idinfoFetcher) throws IdAuthenticationBusinessException {
		return idinfoFetcher.getCbeffValues(idEntity, cbeffDocTypes, this);
	}

	/**
	 * Gets the cbeff doc type.
	 *
	 * @return the cbeff doc type
	 */

	public CbeffDocType[] getCbeffDocTypes() {
		return cbeffDocTypes.clone();
	}

	/**
	 * Gets the id values map.
	 *
	 * @param identityDto   the identity dto
	 * @param bioMatchTypes the bio match types
	 * @return the id values map
	 */
	public static Map<String, List<IdentityInfoDTO>> getIdValuesMap(RequestDTO identityDto,
			BioMatchType... bioMatchTypes) {
		return Stream.of(bioMatchTypes)
				.flatMap(bioMatchType -> bioMatchType.getIdentityInfoFunction().apply(identityDto).entrySet().stream())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue,
						(list1, list2) -> Stream.concat(list1.stream(), list1.stream()).collect(Collectors.toList())));
	}

	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public SingleAnySubtypeType getSubType() {
		return subType;
	}

	/**
	 * Gets the single any subtype.
	 *
	 * @return the single any subtype
	 */
	public SingleAnySubtypeType getSingleAnySubtype() {
		return singleAnySubtype;
	}
}
