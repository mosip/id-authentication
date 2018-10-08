package io.mosip.authentication.service.impl.indauth.service.demo;

public enum NameMatchingStrategy implements MatchingStrategy{

	EXACT(MatchStrategyType.EXACT,100,null),
	PARTIAL(MatchStrategyType.PARTIAL,100,null),
	PHONETICS(MatchStrategyType.PHONETICS,100,null);
	
	private MatchFunction matchFunction;
	
	private int matchValue;
	
	private MatchStrategyType matchStrategyType;
	
	private NameMatchingStrategy(MatchStrategyType matchStrategyType , int matchValue, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchValue = matchValue;
		this.matchStrategyType = matchStrategyType;
	}

	@Override
	public MatchStrategyType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDefaultMatchValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MatchFunction getMatchFunction() {
		// TODO Auto-generated method stub
		return null;
	}


	
   
}
