package io.mosip.kernel.core.idobjectvalidator.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Manoj SP
 *
 */
public enum IdObjectValidatorDocumentMapping {
	
	POA("proofOfAddress", "POA"),
	
	POI("proofOfIdentity", "POI"),
	
	POR("proofOfRelationship", "POR"),
	
	POE("proofOfException", "POE");

	private final String attributeName;
	
	private final String code;
	
	IdObjectValidatorDocumentMapping(String attributeName, String code) {
		this.attributeName = attributeName;
		this.code = code;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getCode() {
		return code;
	}
	
	public static Map<String, String> getAllMapping() {
		return Arrays.stream(values()).parallel()
				.collect(Collectors.toMap(IdObjectValidatorDocumentMapping::getCode,
						IdObjectValidatorDocumentMapping::getAttributeName));
	}
}
