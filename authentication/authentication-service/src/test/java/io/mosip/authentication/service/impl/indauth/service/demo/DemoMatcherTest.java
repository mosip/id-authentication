package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;

public class DemoMatcherTest {
	
	

	


	@Test
	public void matchDemoDataTest() {
		DemoDTO demoDTO =new DemoDTO();
		PersonalIdentityDTO pid=new PersonalIdentityDTO();
		pid.setNamePri("john");
		DemoEntity demoEntity=new DemoEntity();
		demoEntity.setFirstName("john");
		demoEntity.setMiddleName("Rajiv");
		demoEntity.setLastName("Samuel");
		List<MatchInput> listMatchInputs=new ArrayList<>();
		DemoMatcher demoMatcher=new DemoMatcher();
		List<MatchOutput> listMatchOutput=new ArrayList<MatchOutput>();
		List<MatchOutput> listMatchOutputExp=demoMatcher.matchDemoData(demoDTO, demoEntity, listMatchInputs);
		assertEquals(listMatchOutput, listMatchOutputExp);
	}
	
	@Test
	public void matchTypeTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DemoDTO demoDTO =new DemoDTO();
		PersonalIdentityDTO pid =new PersonalIdentityDTO();
		pid.setNamePri("John Rajiv Samuel");
		demoDTO.setPi(pid);
		DemoEntity demoEntity=new DemoEntity();
		demoEntity.setFirstName("John");
		demoEntity.setMiddleName("Rajiv");
		demoEntity.setLastName("Samuel");
		MatchInput matchInput=new MatchInput(DemoMatchType.NAME_PRI,null,100);
		MatchOutput  matchOutputExpect=new MatchOutput(100, true,null, matchInput.getDemoMatchType());
		DemoMatcher demoMatcher=new DemoMatcher();
		Method matchTypeMethod=DemoMatcher.class.getDeclaredMethod("matchType",DemoDTO.class,DemoEntity.class,MatchInput.class);
		matchTypeMethod.setAccessible(true);
		MatchOutput  matchOutputAct=(MatchOutput) matchTypeMethod.invoke(demoMatcher,demoDTO,demoEntity,matchInput);
		 assertEquals(matchOutputExpect,matchOutputAct);
	}
	
	@Test
	public void testMatchTypeMethodFail() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DemoDTO demoDTO =new DemoDTO();
		DemoEntity demoEntity=new DemoEntity();
		MatchInput matchInput=new MatchInput(DemoMatchType.NAME_PRI,"A",100);
		Method matchTypeMethod=DemoMatcher.class.getDeclaredMethod("matchType",DemoDTO.class,DemoEntity.class,MatchInput.class);
		matchTypeMethod.setAccessible(true);
		DemoMatcher demoMatcher=new DemoMatcher();
		MatchOutput matchOutput=(MatchOutput) matchTypeMethod.invoke(demoMatcher,demoDTO, demoEntity,matchInput);
		assertEquals(null, matchOutput);
		
		
	}
	

}
