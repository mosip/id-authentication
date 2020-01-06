package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.GlobalParamName;
import io.mosip.registration.dao.impl.GlobalParamDAOImpl;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.repositories.GlobalParamRepository;

public class GlobalParamDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private GlobalParamDAOImpl globalContextParamDAOImpl;

	@Mock
	private GlobalParamRepository globalParamRepository;

	@Test
	public void getGlobalParamsTest() {
		List<GlobalParamName> params = new ArrayList<>(); 
		
		Mockito.when(globalParamRepository.findByIsActiveTrueAndValIsNotNull()).thenReturn(params);
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		assertEquals(globalParamMap, globalContextParamDAOImpl.getGlobalParams());
	}
	
	@Test
	public void saveAllTest() {
		List<GlobalParam> params = new ArrayList<>(); 
		
		Mockito.when(globalParamRepository.saveAll(Mockito.any())).thenReturn(new LinkedList<GlobalParam>());
		globalContextParamDAOImpl.saveAll(params);
	}
	@Test
	public void get() {  
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName("name");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("code");
		Mockito.when(globalParamRepository.findById(Mockito.any(),Mockito.any())).thenReturn(globalParam);
		//globalContextParamDAOImpl.get("name");
		assertEquals(globalParam.getName(), globalContextParamDAOImpl.get(globalParamId).getName());
	}  
	
	@Test
	public void getAllTest()
	{  
		List<GlobalParam> params = new ArrayList<>(); 
		
		GlobalParam globalParam=new GlobalParam();
		globalParam.setName("1234");
		params.add(globalParam);
		
		List<String> list=new  LinkedList<>();
		list.add("1234");
		
		Mockito.when(globalParamRepository.findByNameIn(list)).thenReturn(params);
		//globalContextParamDAOImpl.get("name");
		assertEquals(params, globalContextParamDAOImpl.getAll(list));
	}  
	
	@Test
	public void updateSoftwareUpdateStatusSuccessTest() {
		
		GlobalParamId globalParamId=new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("N");
		
		GlobalParam globalParam1 = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("Y");
		
		Mockito.when(globalContextParamDAOImpl.get(globalParamId)).thenReturn(globalParam);
		Mockito.when(globalParamRepository.update(globalParam)).thenReturn(globalParam1);
		GlobalParam globalParam2 = globalContextParamDAOImpl.updateSoftwareUpdateStatus(true, Timestamp.from(Instant.now()));
		assertEquals(globalParam2.getVal(),globalParam1.getVal());
	}
	
	@Test
	public void updateSoftwareUpdateStatusFailureTest() {
		
		GlobalParamId globalParamId=new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");
		
		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("Y");
		
		GlobalParam globalParam1 = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("N");
		
		Mockito.when(globalContextParamDAOImpl.get(globalParamId)).thenReturn(globalParam);
		Mockito.when(globalParamRepository.update(globalParam)).thenReturn(globalParam1);
		GlobalParam globalParam2 = globalContextParamDAOImpl.updateSoftwareUpdateStatus(false, Timestamp.from(Instant.now()));
		assertEquals(globalParam2.getVal(),globalParam1.getVal());
	}
	
	@Test
	public void updatetest() {
		GlobalParam globalParam=new GlobalParam();
		GlobalParamId globalParamId=new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.INITIAL_SETUP);
		globalParamId.setLangCode("en");
		globalParam.setGlobalParamId(globalParamId);
		Mockito.when(globalParamRepository.update(globalParam)).thenReturn(globalParam);
		
		assertEquals(globalContextParamDAOImpl.update(globalParam),globalParam);
	}
}
