package io.mosip.authentication.service.impl.indauth.service.demo;

import java.lang.reflect.InvocationTargetException;

import org.junit.Ignore;
import org.junit.Test;

public class DemoMatcherTest {

	@Ignore
	@Test
	public void matchDemoDataTest() {

//		pid.setNamePri("john");
//		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<String, List<IdentityInfoDTO>>();
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setFirstName("john");
//		demoEntity.setMiddleName("Rajiv");
//		demoEntity.setLastName("Samuel");
//		Collection<MatchInput> listMatchInputs = new ArrayList<>();
//		IdInfoMatcher demoMatcher = new IdInfoMatcher();
//		List<MatchOutput> listMatchOutput = new ArrayList<MatchOutput>();
//		LocationInfoFetcher locationInfoFetcher = null;
//		Function<LanguageType, String> languageNameFetcher = null;
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
//
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setFirstName("John");
//		demoEntity.setMiddleName("Rajiv");
//		demoEntity.setLastName("Samuel");
//		MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, null, 100);
//		MatchOutput matchOutputExpect = new MatchOutput(100, true, null, matchInput.getDemoMatchType());
//		IdInfoMatcher demoMatcher = new IdInfoMatcher();
//		Method matchTypeMethod = IdInfoMatcher.class.getDeclaredMethod("matchType", DemoDTO.class, DemoEntity.class,
//				MatchInput.class, LocationInfoFetcher.class);
//		matchTypeMethod.setAccessible(true);
//		LocationInfoFetcher locationInfoFetcher = null;
//		MatchOutput matchOutputAct = (MatchOutput) matchTypeMethod.invoke(demoMatcher, demoDTO, demoEntity, matchInput,
//				locationInfoFetcher);
//		assertEquals(matchOutputExpect, matchOutputAct);
	}

	@Ignore
	@Test
	public void testMatchTypeMethodFail() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
//		DemoDTO demoDTO = new DemoDTO();
//		DemoEntity demoEntity = new DemoEntity();
//		MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, "A", 100);
////		Method matchTypeMethod = IdInfoMatcher.class.getDeclaredMethod("matchType", IdentityDTO.class, DemoEntity.class,
////				MatchInput.class, LocationInfoFetcher.class, LanguageFetcher.class);
////		matchTypeMethod.setAccessible(true);
//		IdInfoMatcher demoMatcher = new IdInfoMatcher();
//		LocationInfoFetcher locationInfoFetcher = null;
//		IdentityDTO identityDTO = new IdentityDTO();
////		LanguageFetcher languageFetcher = null;
//		MatchOutput matchOutput = (MatchOutput) matchTypeMethod.invoke(identityDTO, demoEntity, matchInput,
//				locationInfoFetcher, languageFetcher);
//		assertEquals(null, matchOutput);

	}

}
