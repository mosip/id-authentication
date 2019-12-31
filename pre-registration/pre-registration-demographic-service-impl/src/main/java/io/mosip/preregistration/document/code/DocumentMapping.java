package io.mosip.preregistration.document.code;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DocumentMapping {

	POA("proofOfAddress", "POA"),

	POI("proofOfIdentity", "POI"),

	POR("proofOfRelationship", "POR"),

	POE("proofOfException", "POE");

	private final String attributeName;

	private final String code;

	DocumentMapping(String attributeName, String code) {
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
				.collect(Collectors.toMap(DocumentMapping::getCode, DocumentMapping::getAttributeName));
	}
}
