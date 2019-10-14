package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.DeviceServiceRepository;
import io.mosip.kernel.masterdata.test.TestBootApplication;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
public class DeviceProviderManagementIntegrationTest {

	@Autowired
	private DeviceServiceRepository deviceServiceRepository;
	
	@Autowired
	private DeviceProviderRepository deviceProviderRepository;
	
	
	
	@Before
	public void setUp() {
		
        when(deviceServiceRepository.findByIdAndDProviderId(Mockito.anyString(), Mockito.anyString())).thenReturn(value)
	}
}
