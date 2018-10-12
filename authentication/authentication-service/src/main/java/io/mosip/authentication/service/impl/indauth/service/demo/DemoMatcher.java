package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.DemoDTO;

// TODO: Auto-generated Javadoc
/**
 * @author Arun Bose
 * The Class DemoMatcher.
 */
@Component

public class DemoMatcher {
	
	/**
	 * Match demo data.
	 *
	 * @param demoDTO the demo DTO
	 * @param demoEntity the demo entity
	 * @param matchInput the match input
	 * @return the list
	 */
	public List<MatchOutput> matchDemoData(DemoDTO demoDTO,DemoEntity demoEntity,List<MatchInput> listMatchInputs){
		return listMatchInputs.parallelStream()
				.map(input -> matchType(demoDTO, demoEntity, input))
				.collect(Collectors.toList());
	}

	/**
	 * Match type.
	 *
	 * @param demoDTO the demo DTO
	 * @param demoEntity the demo entity
	 * @param input the input
	 * @return the match output
	 */
	private MatchOutput matchType(DemoDTO demoDTO, DemoEntity demoEntity, MatchInput input) {
		String matchStrategyTypeStr = input.getMatchStrategyType();
		if(matchStrategyTypeStr == null) {
			matchStrategyTypeStr = MatchStrategyType.EXACT.getType();
		}

		Optional<MatchStrategyType> matchStrategyType = MatchStrategyType.getMatchStrategyType(matchStrategyTypeStr);
		if(matchStrategyType.isPresent() ) {
			MatchStrategyType strategyType = matchStrategyType.get();
			Optional<MatchingStrategy> matchingStrategy = input.getDemoMatchType().getAllowedMatchingStrategy(strategyType);
			if(matchingStrategy.isPresent()) {
				MatchingStrategy strategy = matchingStrategy.get();
				Object reqInfo =  input.getDemoMatchType().getDemoInfoFetcher().getInfo(demoDTO);
				Object entityInfo = input.getDemoMatchType().getEntityInfoFetcher().getInfo(demoEntity);
				MatchFunction matchFunction = strategy.getMatchFunction();
				int mtOut = matchFunction.doMatch(reqInfo, entityInfo);
				boolean matchOutput = mtOut >= input.getMatchValue();
				return new MatchOutput(mtOut,matchOutput,input.getMatchStrategyType(),input.getDemoMatchType());
	       }
		}
		return null;
	}

}
