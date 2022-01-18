package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;

public class DynamicDemoAttributeMatchingStrategyTest {


	@Test
	public void testMatch_EmptyValues() throws Exception {
		Map<String, Object> properties = Map.of("demoMatcherUtil", Mockito.mock(DemoMatcherUtil.class));
		Map<String, String> reqValues = Map.of();
		Map<String, String> entityValues = Map.of();
		int res = DynamicDemoAttributeMatchingStrategy.EXACT.match(reqValues, entityValues, properties);
		assertEquals(0, res);
	}
	
	@Test
	public void testMatchFunction_NonStringValues_positive() throws Exception {
		Map<String, Object> properties = Map.of("demoMatcherUtil", Mockito.mock(DemoMatcherUtil.class));
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(1, 1, properties);
		assertEquals(100, res);
	}
	
	@Test
	public void testMatchFunction_NonStringValues_negative() throws Exception {
		Map<String, Object> properties = Map.of("demoMatcherUtil", Mockito.mock(DemoMatcherUtil.class));
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(1, 2, properties);
		assertEquals(0, res);
	}
	
	@Test
	public void testMatchFunction_StringValues_positive() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil);
		
		String reqInfo = "abc";
		String entityInfo = "abc";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(100);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(100, res);
	}
	
	@Test
	public void testMatchFunction_StringValues_negative() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil);

		String reqInfo = "abc";
		String entityInfo = "xyz";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(0);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		assertEquals(0, res);
	}
	
	@Test
	public void testMatchFunction_StringValues_NotMapped_checkIdName_positive() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "residenceStatus";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "abc";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(100);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(100, res);
	}
	
	@Test
	public void testMatchFunction_StringValues_NotMapped_checkIdName_negative() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "residenceStatus";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "xyz";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(0);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(0, res);
	}
	
	@Test
	public void testMatchFunction_Name_Mapped_checkIdName_positive() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "residenceStatus";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Mockito.when(mappingConfig.getName()).thenReturn(List.of(idName));
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "abc";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(100);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(100, res);
	}
	
	@Test
	public void testMatchFunction_Name_Mapped_checkIdName_negative() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "residenceStatus";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Mockito.when(mappingConfig.getName()).thenReturn(List.of(idName));
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "xyz";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(0);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(0, res);
	}
	
	@Test
	public void testMatchFunction_Addr_Mapped_checkIdName_positive() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "fullName";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Mockito.when(mappingConfig.getFullAddress()).thenReturn(List.of(idName));
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "abc";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(100);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(100, res);
	}
	
	@Test
	public void testMatchFunction_Addr_Mapped_checkIdName_negative() throws Exception {
		DemoMatcherUtil demoMatcherUtil = Mockito.mock(DemoMatcherUtil.class);
		String idName = "zone";
		MappingConfig mappingConfig = Mockito.mock(MappingConfig.class);
		Mockito.when(mappingConfig.getFullAddress()).thenReturn(List.of(idName));
		Map<String, Object> properties = Map.of("demoMatcherUtil", demoMatcherUtil
				,"idName",idName,"mappingConfig", mappingConfig);
		
		String reqInfo = "abc";
		String entityInfo = "xyz";
		Mockito.when(demoMatcherUtil.doExactMatch(reqInfo, entityInfo)).thenReturn(0);
		int res = DynamicDemoAttributeMatchingStrategy.EXACT
				.getMatchFunction().match(reqInfo, entityInfo, properties);
		
		assertEquals(0, res);
	}
	
	@Test
	public void testGetType() {
		assertEquals(MatchingStrategyType.EXACT, DynamicDemoAttributeMatchingStrategy.EXACT.getType());
	}
	
}