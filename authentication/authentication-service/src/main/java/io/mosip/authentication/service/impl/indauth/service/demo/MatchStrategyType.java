package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.stream.Stream;

public enum MatchStrategyType {

	EXACT("EXACT"), PARTIAL("PARTIAL"), PHONETICS("PHONETICS");

	public static final MatchStrategyType default_Matching_Strategy = MatchStrategyType.EXACT;

	private String type;

	private MatchStrategyType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static Optional<MatchStrategyType> getMatchStrategyType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
