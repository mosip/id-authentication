package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.mosip.authentication.core.dto.indauth.DemoDTO;

public class DemoMatcher {
	
	public List<MatchOutput> matchDemoData(DemoDTO demoDTO,DemoEntity demoEntity,List<MatchInput> matchInput){
		return matchInput.parallelStream()
				.map(input -> matchType(demoDTO, demoEntity, input))
				.collect(Collectors.toList());
	}

	private MatchOutput matchType(DemoDTO demoDTO, DemoEntity demoEntity, MatchInput input) {
		Optional<MatchStrategyType> matchStrategyType = MatchStrategyType.getMatchStrategyType(input.getMatchStrategyType());
		if(matchStrategyType.isPresent() ) {
			MatchStrategyType strategyType = matchStrategyType.get();
			Optional<MatchingStrategy> matchingStrategy = input.getDemoMatchType().getAllowedMatchingStrategy(strategyType);
			if(matchingStrategy.isPresent()) {
				MatchingStrategy strategy = matchingStrategy.get();
				Object reqInfo =  input.getDemoMatchType().getDemoInfo().getInfo(demoDTO);
				Object entityInfo = input.getDemoMatchType().getEntityInfo().getInfo(demoEntity);
				MatchFunction matchFunction = strategy.getMatchFunction();
				int mtOut = matchFunction.doMatch(reqInfo, entityInfo);
				
				return new MatchOutput();
				
				//MatchUtil.do(reqName, entityName);
				//if(utilResult >= input.mt) { return matchoutput true; }
			}
		}
		return null;
	}

}
