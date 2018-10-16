package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;

public class DemoMatcherTest {

	@Test
	public void matchDemoDataTest() {
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDTO pid = new PersonalIdentityDTO();
		pid.setNamePri("john");
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setFirstName("john");
		demoEntity.setMiddleName("Rajiv");
		demoEntity.setLastName("Samuel");
		List<MatchInput> listMatchInputs = new ArrayList<>();
		DemoMatcher demoMatcher = new DemoMatcher();
		List<MatchOutput> listMatchOutput = new ArrayList<MatchOutput>();
		LocationInfoFetcher locationInfoFetcher = null;
		List<MatchOutput> listMatchOutputExp = demoMatcher.matchDemoData(demoDTO, demoEntity, listMatchInputs,
				locationInfoFetcher);
		assertEquals(listMatchOutput, listMatchOutputExp);
	}

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

	@Test
	public void testMatchTypeMethodFail() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DemoDTO demoDTO = new DemoDTO();
		DemoEntity demoEntity = new DemoEntity();
		MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, "A", 100);
		Method matchTypeMethod = DemoMatcher.class.getDeclaredMethod("matchType", DemoDTO.class, DemoEntity.class,
				MatchInput.class, LocationInfoFetcher.class);
		matchTypeMethod.setAccessible(true);
		DemoMatcher demoMatcher = new DemoMatcher();
		LocationInfoFetcher locationInfoFetcher = null;
		MatchOutput matchOutput = (MatchOutput) matchTypeMethod.invoke(demoMatcher, demoDTO, demoEntity, matchInput,
				locationInfoFetcher);
		assertEquals(null, matchOutput);

	}

}
