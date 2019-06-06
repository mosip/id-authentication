package io.mosip.registration.util.common;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ io.mosip.registration.context.ApplicationContext.class })
public class PageFlowTest {

	PageFlow pageFlow;
	
	@Test
	public void testGetInitialPageDetails() {
		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> map =new HashMap<>();
		map.put(RegistrationConstants.FINGERPRINT_DISABLE_FLAG, "n");
		map.put(RegistrationConstants.IRIS_DISABLE_FLAG, "n");
		map.put(RegistrationConstants.FACE_DISABLE_FLAG, "n");
		map.put(RegistrationConstants.DOC_DISABLE_FLAG, "n");
		when(ApplicationContext.map()).thenReturn(map);

		pageFlow = new PageFlow();
		pageFlow.getInitialPageDetails();
	}

}
