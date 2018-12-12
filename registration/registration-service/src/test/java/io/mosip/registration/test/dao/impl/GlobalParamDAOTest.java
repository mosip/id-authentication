package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.dao.impl.GlobalParamDAOImpl;
import io.mosip.registration.repositories.GlobalParamRepository;

public class GlobalParamDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private GlobalParamDAOImpl globalContextParamDAOImpl;

	@Mock
	private GlobalParamRepository globalContextParamRepository;

	@Test
	public void getGlobalParamsTest() {
		List<GlobalParamName> params = new ArrayList<>(); 
		
		Mockito.when(globalContextParamRepository.findByIsActiveTrue()).thenReturn(params);
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		assertEquals(globalParamMap, globalContextParamDAOImpl.getGlobalParams());
	}
}
