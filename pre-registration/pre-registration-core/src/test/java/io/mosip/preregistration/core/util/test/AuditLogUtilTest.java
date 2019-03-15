package io.mosip.preregistration.core.util.test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.AuditResponseDto;
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
	}
	
	@Test
	public void saveAuditDetailsSuccessTest() {
		ResponseEntity<AuditResponseDto> respEntity= new ResponseEntity<>(auditResponseDto, HttpStatus.OK);
		Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(),Mockito.eq(AuditResponseDto.class))).thenReturn(respEntity);
		auditUtil.saveAuditDetails(auditRequestDto);
		assertEquals(respEntity.getBody().isStatus(), true);
	}
}
