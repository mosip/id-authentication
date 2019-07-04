package io.mosip.registration.processor.core.auth.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.core.auth.dto.AuthBioSubType;
import io.mosip.registration.processor.core.code.BioSubType;

public class BioSubTypeMapperUtil {
	/** The status map. */
	private static EnumMap<BioSubType, AuthBioSubType> statusMap = new EnumMap<>(
			BioSubType.class);

	/** The unmodifiable map. */
	private static Map<BioSubType, AuthBioSubType> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	public BioSubTypeMapperUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<BioSubType, AuthBioSubType> statusMapper() {

		statusMap.put(BioSubType.LEFT_INDEX_FINGER, AuthBioSubType.LEFT_INDEX);
		statusMap.put(BioSubType.LEFT_LITTLE_FINGER, AuthBioSubType.LEFT_LITTLE);
		statusMap.put(BioSubType.LEFT_MIDDLE_FINGER,AuthBioSubType.LEFT_MIDDLE);
		statusMap.put(BioSubType.LEFT_RING_FINGER, AuthBioSubType.LEFT_RING);
		statusMap.put(BioSubType.LEFT_THUMB, AuthBioSubType.LEFT_THUMB);
		statusMap.put(BioSubType.RIGHT_INDEX_FINGER, AuthBioSubType.RIGHT_INDEX);
		statusMap.put(BioSubType.RIGHT_LITTLE_FINGER, AuthBioSubType.RIGHT_LITTLE);
		statusMap.put(BioSubType.RIGHT_MIDDLE_FINGER, AuthBioSubType.RIGHT_MIDDLE);
		statusMap.put(BioSubType.RIGHT_RING_FINGER, AuthBioSubType.RIGHT_RING);
		statusMap.put(BioSubType.RIGHT_THUMB, AuthBioSubType.RIGHT_THUMB);
		statusMap.put(BioSubType.IRIS_LEFT, AuthBioSubType.LEFT);
		statusMap.put(BioSubType.IRIS_RIGHT, AuthBioSubType.RIGHT);
		statusMap.put(BioSubType.FACE, AuthBioSubType.UNKNOWN);
	
		return unmodifiableMap;

	}

	public String getStatusCode(BioSubType bioSubType) {
		Map<BioSubType, AuthBioSubType> mapStatus = BioSubTypeMapperUtil
				.statusMapper();

		return mapStatus.get(BioSubType.valueOf(bioSubType.toString())).toString();
	}
	
}

