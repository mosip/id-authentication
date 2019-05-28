package io.mosip.registration.processor.core.auth.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.core.auth.dto.AuthBioType;
import io.mosip.registration.processor.core.code.BioType;

public class BioTypeMapperUtil {
	/** The status map. */
	private static EnumMap<BioType, AuthBioType> statusMap = new EnumMap<>(
			BioType.class);

	/** The unmodifiable map. */
	private static Map<BioType, AuthBioType> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	public BioTypeMapperUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<BioType, AuthBioType> statusMapper() {

		statusMap.put(BioType.FINGER, AuthBioType.FMR);
		statusMap.put(BioType.FACE, AuthBioType.FID);
		statusMap.put(BioType.IRIS,AuthBioType.IIR);
	
		return unmodifiableMap;

	}

	public String getStatusCode(BioType bioType) {
		Map<BioType, AuthBioType> mapStatus = BioTypeMapperUtil
				.statusMapper();

		return mapStatus.get(BioType.valueOf(bioType.toString())).toString();
	}
}
