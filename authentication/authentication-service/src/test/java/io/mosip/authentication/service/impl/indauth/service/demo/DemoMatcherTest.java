package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;

public class DemoMatcherTest {

	@Ignore
	@Test
	public void matchDemoDataTest() {
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDTO pid = new PersonalIdentityDTO();
		pid.setNamePri("john");
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<String, List<IdentityInfoDTO>>();
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setFirstName("john");
//		demoEntity.setMiddleName("Rajiv");
//		demoEntity.setLastName("Samuel");
		Collection<MatchInput> listMatchInputs = new ArrayList<>();
		DemoMatcher demoMatcher = new DemoMatcher();
		List<MatchOutput> listMatchOutput = new ArrayList<MatchOutput>();
		LocationInfoFetcher locationInfoFetcher = null;
		Function<LanguageType, String> languageNameFetcher = null;
//		languageCodeFetcher
//		IdentityDTO identityDTO = new IdentityDTO();
//		List<MatchOutput> listMatchOutputExp = demoMatcher.matchDemoData(identityDTO, demoEntity, listMatchInputs,
//				locationInfoFetcher, languageCodeFetcher, languageNameFetcher);
////		List<MatchOutput> listMatchOutputExp = demoMatcher.matchDemoData(identityDTO, demoEntity, listMatchInputs,
//				locationInfoFetcher, languageCodeFetcher);
//		assertEquals(listMatchOutput, listMatchOutputExp);
	}

	@Ignore
	@Test
	public void matchTypeTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDTO pid = new PersonalIdentityDTO();
		pid.setNamePri("John Rajiv Samuel");
		demoDTO.setPi(pid);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setFirstName("John");
		demoEntity.setMiddleName("Rajiv");
		demoEntity.setLastName("Samuel");
		MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, null, 100);
		MatchOutput matchOutputExpect = new MatchOutput(100, true, null, matchInput.getDemoMatchType());
		DemoMatcher demoMatcher = new DemoMatcher();
		Method matchTypeMethod = DemoMatcher.class.getDeclaredMethod("matchType", DemoDTO.class, DemoEntity.class,
				MatchInput.class, LocationInfoFetcher.class);
		matchTypeMethod.setAccessible(true);
		LocationInfoFetcher locationInfoFetcher = null;
		MatchOutput matchOutputAct = (MatchOutput) matchTypeMethod.invoke(demoMatcher, demoDTO, demoEntity, matchInput,
				locationInfoFetcher);
		assertEquals(matchOutputExpect, matchOutputAct);
	}

	@Ignore
	@Test
	public void testMatchTypeMethodFail() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DemoDTO demoDTO = new DemoDTO();
		DemoEntity demoEntity = new DemoEntity();
		MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, "A", 100);
//		Method matchTypeMethod = DemoMatcher.class.getDeclaredMethod("matchType", IdentityDTO.class, DemoEntity.class,
//				MatchInput.class, LocationInfoFetcher.class, LanguageFetcher.class);
//		matchTypeMethod.setAccessible(true);
		DemoMatcher demoMatcher = new DemoMatcher();
		LocationInfoFetcher locationInfoFetcher = null;
		IdentityDTO identityDTO = new IdentityDTO();
//		LanguageFetcher languageFetcher = null;
//		MatchOutput matchOutput = (MatchOutput) matchTypeMethod.invoke(identityDTO, demoEntity, matchInput,
//				locationInfoFetcher, languageFetcher);
//		assertEquals(null, matchOutput);

	}

}
