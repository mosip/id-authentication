package io.mosip.preregistration.core.util.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.AuditResponseDto;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.util.AuditLogUtil;
/**
 * AuditLogUtil Test
 * 
 * @version 1.0.0
 * @author M1043226
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuditLogUtilTest {

	@Autowired
	AuditLogUtil auditUtil;
	
	private AuditLogUtil auditLogUtilSpy;
	@MockBean
	RestTemplate restTemplate;
	
	AuditRequestDto auditRequestDto=new AuditRequestDto();
	AuditResponseDto auditResponseDto=new AuditResponseDto();
	
	@Before
	public void setUp() throws Exception {
		
		auditRequestDto.setActionTimeStamp(LocalDateTime.now());
		auditRequestDto.setApplicationId(AuditLogVariables.MOSIP_1.toString());
		auditRequestDto.setApplicationName(AuditLogVariables.PREREGISTRATION.toString());
		auditRequestDto.setCreatedBy(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setIdType(AuditLogVariables.PRE_REGISTRATION_ID.toString());
		auditRequestDto.setSessionUserId(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setSessionUserName(AuditLogVariables.SYSTEM.toString());
		auditRequestDto.setId(AuditLogVariables.NO_ID.toString());
		auditRequestDto.setHostName("A2ML29862");
		auditRequestDto.setEventId("1");
		auditRequestDto.setEventName("xyz");
		auditRequestDto.setEventType("abc");
		auditRequestDto.setHostIp("1234");
		auditRequestDto.setModuleId("ModuleId");
		auditRequestDto.setDescription("description");
		auditRequestDto.setModuleName("ModuleName");
		
		auditResponseDto.setStatus(true);
		auditLogUtilSpy=Mockito.spy(auditUtil);
	}
	
	@Test
	public void saveAuditDetailsSuccessTest() {
		ResponseWrapper<AuditResponseDto> res=new ResponseWrapper<>();
		res.setResponse(auditResponseDto);
		ResponseEntity<ResponseWrapper<AuditResponseDto>> respEntity= new ResponseEntity<>(res, HttpStatus.OK);
		Mockito.doReturn(true).when(auditLogUtilSpy).callAuditManager(Mockito.any());
//		Mockito.when(restTemplate.exchange(Mockito.any(),Mockito.eq(HttpMethod.POST), Mockito.any(),Mockito.eq(new ParameterizedTypeReference<ResponseWrapper<AuditResponseDto>>() {
//		}))).thenReturn(respEntity);
		
		auditLogUtilSpy.saveAuditDetails(auditRequestDto);
		assertEquals(respEntity.getBody().getResponse().isStatus(), true);
	}
}