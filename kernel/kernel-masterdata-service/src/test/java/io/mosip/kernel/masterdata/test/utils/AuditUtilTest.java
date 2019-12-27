package io.mosip.kernel.masterdata.test.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.masterdata.dto.request.AuditRequestDto;
import io.mosip.kernel.masterdata.utils.AuditUtil;

@RunWith(JUnit4.class)
public class AuditUtilTest {

	@Autowired
	private AuditUtil auditUtil;
	
	@Value("${mosip.kernel.masterdata.audit-url}")
	private String auditUrl;
	
	private MockRestServiceServer mockRestServiceServer;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private AuditRequestDto auditRequestDto;
	
	@Before
	public void setUp() {
		mockRestServiceServer=mockRestServiceServer.bindTo(restTemplate).build();
		auditRequestDto = new AuditRequestDto();
		
	}
	
	@Test
	public void testAuditUtil() {
		RequestWrapper<AuditRequestDto> auditWrapper= new RequestWrapper<>();
		
	}
}



