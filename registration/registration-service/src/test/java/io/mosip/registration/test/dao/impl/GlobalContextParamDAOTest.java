package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

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
import io.mosip.registration.repositories.GlobalContextParamRepository;

public class GlobalContextParamDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private GlobalContextParamDAOImpl globalContextParamDAOImpl;

	@Mock
	private GlobalContextParamRepository globalContextParamRepository;

	@Test
	public void findInvalidLoginCountTest() {
		List<GlobalContextParam> paramList = new ArrayList<>();
		GlobalContextParam globalContextParam1 = new GlobalContextParam();
		globalContextParam1.setName("INVALID_LOGIN_COUNT");
		globalContextParam1.setVal("1");
		List<String> params = new ArrayList<>();
		params.add("INVALID_LOGIN_COUNT");
		Mockito.when(globalContextParamRepository.findByNameIn(params)).thenReturn(paramList);
		assertEquals(paramList, globalContextParamDAOImpl.findInvalidLoginCount(params));
	}

	@Test
	public void findRejectionOnholdCommentsTest() {
		GlobalContextParam globalContextParam1 = new GlobalContextParam();
		globalContextParam1.setName("ONHOLD_COMMENTS");
		globalContextParam1.setVal("Gender-photo mismatch,Age-photo mismatch,Name correction required");
		Mockito.when(globalContextParamRepository.findByName("ONHOLD_COMMENTS")).thenReturn(globalContextParam1);
		assertEquals(globalContextParam1 , globalContextParamDAOImpl.findRejectionOnholdComments("ONHOLD_COMMENTS"));
	}
}
