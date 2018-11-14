package io.mosip.registration.test.service.packet.encryption;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.service.impl.GeoLocationCaptureImpl;

public class GeoLocationCaptureImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private GeoLocationCaptureImpl geoLocationCaptureImpl ;
	@Mock
	private AuditFactoryImpl auditFactory;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testGetLatLongDtls() {
		
		Map<String, Object> map1=geoLocationCaptureImpl.getLatLongDtls();
		assertTrue(!map1.isEmpty());
		assertTrue((double)map1.get("latitude")==12.99194);
		assertTrue((double)map1.get("longitude")==80.2471);
		assertTrue(map1.get("errorMessage")=="success");


	}

}
