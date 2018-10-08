package io.mosip.authentication.service.impl.indauth.service.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchInput {
	
	private DemoMatchType demoMatchType;
	
	private String matchStrategyType;
	
	private int matchValue;

	
	
	
	

}
