package io.mosip.authentication.service.impl.idauth.demo;

import java.util.List;

import io.mosip.authentication.core.dto.indauth.DemoDTO;

public class DemoMatcher {
	
	public List<MatchOutput> matchDemoData(DemoDTO demoDTO,DemoEntity demoEntity,List<MatchInput> matchInput){
		
		List<MatchOutput>  listMatchOutput=null;
		matchInput.forEach(input -> matchType(demoDTO, demoEntity, input));
		return listMatchOutput;
	}

	private MatchOutput matchType(DemoDTO demoDTO, DemoEntity demoEntity, MatchInput input) {
		if(input.getDemoMatchType().getAllowedMatchingStrategy().contains(input.getMatchStrategyType())) {
			String reqName = (String) input.getDemoMatchType().getDemoInfo().getInfo(demoDTO);
			String entityName = (String) input.getDemoMatchType().getEntityInfo().getInfo(demoEntity);
			//MatchUtil.do(reqName, entityName);
			//if(utilResult >= input.mt) { return matchoutput true; }
		}
		return null;
	}

}
