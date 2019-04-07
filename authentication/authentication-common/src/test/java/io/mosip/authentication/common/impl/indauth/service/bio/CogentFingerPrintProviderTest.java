package io.mosip.authentication.common.impl.indauth.service.bio;

import org.junit.Test;

import io.mosip.authentication.common.impl.fingerauth.provider.CogentFingerprintProvider;

public class CogentFingerPrintProviderTest {
	
	CogentFingerprintProvider cogentFingerPrintProvider=new CogentFingerprintProvider();
	
 @Test
 public void testCogentFingerPrintTest() {
	 
	 cogentFingerPrintProvider.captureFingerprint(null, null);
	 cogentFingerPrintProvider.deviceInfo();
	 cogentFingerPrintProvider.segmentFingerprint(null);
	 cogentFingerPrintProvider.createMinutiae(null);
 }

}
