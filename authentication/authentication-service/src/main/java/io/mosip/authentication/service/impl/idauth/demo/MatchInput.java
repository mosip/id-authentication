package io.mosip.authentication.service.impl.idauth.demo;

import lombok.Data;

@Data
public class MatchInput {
	
	private DemoMatchType demoMatchType;
	
	private MatchStrategyType matchStrategyType;
	
	private int matchValue;

	public MatchInput(DemoMatchType demoMatchType, MatchStrategyType matchStrategyType, int matchValue) {
		super();
		this.demoMatchType = demoMatchType;
		this.matchStrategyType = matchStrategyType;
		this.matchValue = matchValue;
	}
	
	
	
	

}
