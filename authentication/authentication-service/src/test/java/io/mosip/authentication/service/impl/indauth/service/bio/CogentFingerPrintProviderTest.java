package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.HashMap;

import org.junit.Test;

import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;

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
