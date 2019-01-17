package io.mosip.authentication.service.impl.indauth.service.bio;

import org.junit.Test;

import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;

public class MantraFingerPrintProviderTest {
	
	MantraFingerprintProvider mantraFingerPrintProvider=new MantraFingerprintProvider();
	
	 @Test
	 public void testMantraFingerPrintTest() {
		 
		 mantraFingerPrintProvider.captureFingerprint(null, null);
		 mantraFingerPrintProvider.deviceInfo();
		 mantraFingerPrintProvider.createMinutiae(null);
		 mantraFingerPrintProvider.segmentFingerprint(null);
		 mantraFingerPrintProvider.OnCaptureCompleted(false, 0, null, null);
		 mantraFingerPrintProvider.OnPreview(null);
	 }

}
