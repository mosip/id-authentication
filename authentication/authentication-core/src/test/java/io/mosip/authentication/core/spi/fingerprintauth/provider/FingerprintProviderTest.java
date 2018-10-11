package io.mosip.authentication.core.spi.fingerprintauth.provider;

import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;

public class FingerprintProviderTest {
	
	MosipFingerprintProvider fp = new FingerprintProvider() {
		
		@Override
		public Optional<Map<?, ?>> segmentFingerprint(byte[] fingerImage) {
			return null;
		}
		
		@Override
		public FingerprintDeviceInfo deviceInfo() {
			return null;
		}
		
		@Override
		public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
			return null;
		}
	};
	
	@Test
	public void testISOScoreCalculatorSuccess() {
		double score = fp.scoreCalculator(new byte[] {1,2,3,4,5,6,7,8,9,0}, new byte[] {1,2,3,4,5,6,7,8,9,0});
		System.err.println(score);
	}

}
