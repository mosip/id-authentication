package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.GlobalContextParamDAOImpl;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.service.impl.GlobalContextParamServiceImpl;

public class GlobalContextParamServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private GlobalContextParamServiceImpl gloablContextParamServiceImpl;
	
	@Mock
	private GlobalContextParamDAOImpl globalContextParamDAOImpl;

	@Test
	public void findInvalidLoginCountTest() {
		List<GlobalContextParam> paramList = new ArrayList<>();
		GlobalContextParam globalContextParam1 = new GlobalContextParam();
		globalContextParam1.setName("INVALID_LOGIN_COUNT");
		globalContextParam1.setVal("1");
		List<String> params = new ArrayList<>();
		params.add("INVALID_LOGIN_COUNT");
		Mockito.when(globalContextParamDAOImpl.findInvalidLoginCount(params)).thenReturn(paramList);
		assertEquals(paramList, gloablContextParamServiceImpl.findInvalidLoginCount(params));
	}
	
	@Test
	public void findRejectionOnholdCommentsTest() {
		GlobalContextParam globalContextParam1 = new GlobalContextParam();
		globalContextParam1.setName("ONHOLD_COMMENTS");
		globalContextParam1.setVal("Gender-photo mismatch,Age-photo mismatch,Name correction required");
		Mockito.when(globalContextParamDAOImpl.findRejectionOnholdComments("ONHOLD_COMMENTS")).thenReturn(globalContextParam1);
		GlobalContextParam globalContextParam = gloablContextParamServiceImpl.findRejectionOnholdComments("ONHOLD_COMMENTS");
		assertEquals(globalContextParam1,globalContextParam);
	}

}
