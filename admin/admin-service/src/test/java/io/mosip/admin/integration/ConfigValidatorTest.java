package io.mosip.admin.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import io.mosip.admin.configvalidator.ProcessFlowConfigValidator;

public class ConfigValidatorTest {
	
	@Test
	public void testValidateConfig() {
		ProcessFlowConfigValidator mockList = mock(ProcessFlowConfigValidator.class);
		when(mockList.validateDocumentProcess()).thenReturn(Mockito.anyBoolean());
		//verify(mockList);
		
		
	}

}
