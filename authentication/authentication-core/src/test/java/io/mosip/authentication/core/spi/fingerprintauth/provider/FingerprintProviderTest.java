package io.mosip.authentication.core.spi.fingerprintauth.provider;

import static org.junit.Assert.assertTrue;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;

public class FingerprintProviderTest {
	byte[] finger1 = new byte[] { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 8, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1, 0, 0,
			0, 40, 39, -128, -124, 0, -21, 60, 80, -128, -94, 1, 1, -105, 80, -128, 115, 0, -43, -56, 87, -128, -90, 1,
			24, -105, 80, 64, -70, 0, -26, -59, 100, -128, 119, 0, -75, 18, 100, 64, 101, 1, 51, -40, 100, -128, -120,
			0, -91, 2, 100, 64, 102, 0, -98, 24, 100, 64, 39, 0, -21, -63, 100, -128, -50, 0, -92, -29, 100, 64, 66, 1,
			70, -48, 67, 64, 17, 0, -37, -76, 100, 64, -111, 0, -32, -72, 53, -128, 103, 0, -16, -44, 93, -128, -83, 0,
			-36, -56, 93, 64, 97, 1, 14, -37, 100, -128, 83, 1, 11, -48, 100, -128, -83, 1, 42, -106, 100, 64, -46, 0,
			-7, -52, 100, -128, -102, 1, 64, 21, 100, 64, -51, 1, 46, -73, 67, 64, -124, 1, 86, 98, 73, 64, -44, 1, 63,
			-88, 53, -128, 97, 1, 90, 107, 73, 64, 38, 0, -100, -81, 100, -128, -94, 0, -25, -78, 67, 64, -117, 0, -48,
			-46, 60, 64, -96, 0, -52, -41, 67, 64, -118, 1, 31, 14, 87, 64, -60, 0, -33, -48, 100, 64, 77, 0, -52, -67,
			100, 64, -102, 0, -89, -16, 100, 64, -67, 1, 56, -98, 80, 64, -38, 0, -57, -42, 80, 64, -55, 1, 64, 54, 53,
			-128, 121, 1, 91, 109, 73, 64, 74, 0, -113, -88, 100, 64, -117, 0, 109, 5, 100, 0, 0 };

	byte[] finger1scan2 = new byte[] { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 2, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1,
			0, 0, 0, 40, 38, -128, -126, 0, -40, -74, 40, -128, -118, 0, -51, -58, 93, 64, -124, 1, 25, -101, 60, 64,
			106, 1, 25, 11, 53, -128, 82, 0, -62, -60, 80, 64, -84, 1, 31, -65, 87, 64, -73, 0, -65, -44, 100, 64, 120,
			1, 60, -64, 7, 64, -50, 1, 14, -51, 73, -128, 106, 0, -110, -7, 93, 64, -110, 1, 74, 40, 40, 64, -101, 1,
			82, 30, 53, 64, -63, 0, 121, 110, 93, -128, -124, 1, 10, -109, 87, 64, 109, 0, -52, 102, 60, 64, -81, 0,
			-17, -51, 100, 64, 110, 0, -67, -62, 73, 64, 101, 0, -78, -75, 87, 64, -120, 1, 47, -74, 73, -128, 52, 0,
			-3, -49, 100, -128, 83, 1, 51, -51, 27, -128, 123, 0, -107, -20, 93, -128, -83, 0, -100, -32, 100, -128,
			101, 1, 75, 20, 20, 64, 71, 0, -118, 23, 100, 64, 45, 0, 124, -86, 100, 64, -105, 0, -38, -59, 87, 64, -95,
			0, -44, -50, 100, 64, 127, 0, -68, -45, 87, -128, 71, 0, -35, -49, 93, 64, 67, 1, 7, -38, 100, -128, -76, 1,
			28, -72, 87, 64, 104, 1, 51, 94, 27, -128, 88, 0, -95, 15, 93, 64, -45, 1, 1, 91, 53, 64, 48, 0, -70, -67,
			100, -128, -90, 1, 76, 44, 40, 64, -82, 1, 84, 12, 40, 0, 0 };

	byte[] finger2 = new byte[] { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 104, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1, 0,
			0, 0, 40, 55, 64, 101, 0, -30, -95, 87, 64, 111, 0, -47, -76, 73, -128, -116, 0, -11, 62, 80, 64, -103, 0,
			-51, -59, 93, -128, 81, 0, -63, 30, 87, 64, -88, 1, 1, -71, 80, -128, 71, 0, -68, 44, 87, -128, 56, 0, -51,
			57, 87, 64, -78, 1, 5, -59, 80, -128, -70, 0, -53, -55, 93, 64, -80, 1, 29, -76, 100, 64, -52, 0, -14, -47,
			100, 64, 62, 1, 52, 19, 87, 64, 91, 1, 82, 7, 100, 64, -124, 1, 85, 31, 80, -128, -87, 0, 125, -34, 100,
			-128, -62, 1, 74, 47, 27, 64, -38, 0, -121, -35, 100, 64, -121, 0, 84, -14, 87, -128, 127, 0, -7, -81, 93,
			-128, 99, 0, -6, -100, 100, -128, 119, 1, 10, 47, 93, 64, 111, 0, -67, -87, 80, -128, 59, 0, -33, 48, 87,
			64, 65, 0, -52, 48, 87, 64, -78, 0, -27, -59, 93, 64, 84, 0, -80, 26, 93, 64, 48, 0, -19, 67, 80, -128, 61,
			0, -75, -71, 93, -128, 37, 0, -45, -61, 100, 64, -113, 0, -114, -36, 100, -128, 123, 0, 127, -13, 100, 64,
			106, 1, 83, 11, 100, 64, -87, 1, 74, 46, 20, 64, 52, 0, 125, -90, 100, -128, -80, 0, 112, -28, 100, 64, -74,
			0, 106, 109, 100, -128, 113, 0, -7, 52, 93, -128, -114, 0, -33, -66, 93, -128, 89, 0, -54, 32, 73, 64, 125,
			0, -68, -56, 80, -128, 65, 0, -5, 25, 87, -128, 89, 1, 28, -112, 100, -128, 98, 1, 37, -105, 100, 64, 55, 0,
			-9, 39, 60, -128, 55, 1, 17, -121, 93, -128, -92, 1, 38, 55, 100, -128, 52, 1, 26, 16, 33, 64, 30, 1, 8, 78,
			67, 64, 73, 0, -124, 25, 93, -128, -52, 1, 46, -74, 53, 64, -112, 1, 87, 17, 20, 64, -82, 1, 86, 20, 20, 64,
			-64, 1, 86, 10, 27, 64, 62, 0, 98, 23, 93, 0, 0 };

	MosipFingerprintProvider fp = new FingerprintProvider() {

		@Override
		public Optional<Map> segmentFingerprint(byte[] fingerImage) {
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

		@Override
		public String createMinutiae(byte[] inputImage) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	FingerprintProvider fingerPrint = new FingerprintProvider() {

		@Override
		public Optional<Map> segmentFingerprint(byte[] fingerImage) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FingerprintDeviceInfo deviceInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String createMinutiae(byte[] inputImage) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@Test
	public void testISOScoreCalculatorSameFingerDiffScan() {
		double score = fp.matchScoreCalculator(finger1, finger1scan2);
		assertTrue(score > 100);
	}

	@Test
	public void testISOScoreCalculatorDiffFinger() {
		double score = fp.matchScoreCalculator(finger1, finger2);
		assertTrue(score < 100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testISOScoreCalculatorException() {
		fp.matchScoreCalculator(new byte[] { 1, 2 }, new byte[] { 1, 2 });
	}

	@Test(expected = JsonSyntaxException.class)
	public void testMinutiaeScoreCalculatorException() {
		fp.matchScoreCalculator("123", "123");
	}

	@Test
	public void testMinutiaeScoreSameFingerDiffScan() {
		FingerprintTemplate template1 = new FingerprintTemplate().convert(finger1);
		FingerprintTemplate template2 = new FingerprintTemplate().convert(finger1scan2);
		double score = fp.matchScoreCalculator(template1.serialize(), template2.serialize());
		assertTrue(score > 100);
	}

	@Test
	public void testMinutiaeScoreDiffFinger() {
		FingerprintTemplate template1 = new FingerprintTemplate().convert(finger1);
		FingerprintTemplate template2 = new FingerprintTemplate().convert(finger2);
		double score = fp.matchScoreCalculator(template1.serialize(), template2.serialize());
		assertTrue(score < 100);
	}

	@Test
	public void testmatchMinutiea() {
		byte[] refInfo = Base64.getEncoder().encode(finger1);
		String value = new String(refInfo);
		fingerPrint.matchMinutiea(value, value);
		fingerPrint.matchImage(value, value);
		fingerPrint.decodeValue(value);
	}

	@Test
	public void testMultiMatchMinutae() {
		Map<String, String> reqInfo = new HashMap<>();
		String leftIndex = "Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA";
		reqInfo.put("leftIndex", leftIndex);
		String rightIndex = "Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA";
		reqInfo.put("rightIndex", rightIndex);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("leftIndex", leftIndex);
		entityInfo.put("rightIndex", rightIndex);
		double score = fingerPrint.matchMultiMinutae(reqInfo, entityInfo);
		fingerPrint.matchMultiImage(reqInfo, entityInfo);
		assertTrue(score > 500);
	}

	@Test
	public void testmatcImage() {
		byte[] refInfo = Base64.getEncoder().encode(finger1);
		String value = new String(refInfo);
		fingerPrint.matchImage(value, value);
		fingerPrint.decodeValue(value);
	}

	@Test
	public void testMultiMatchImage() {
		Map<String, String> reqInfo = new HashMap<>();
		String leftIndex = "Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA";
		reqInfo.put("leftIndex", leftIndex);
		String rightIndex = "Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA";
		reqInfo.put("rightIndex", rightIndex);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("leftIndex", leftIndex);
		entityInfo.put("rightIndex", rightIndex);
		double score = fingerPrint.matchMultiImage(reqInfo, entityInfo);
		assertTrue(score > 500);
	}

	@Test
	public void testInavalidMultiMatchMinutae() {
		Map<String, String> reqInfo = new HashMap<>();
		String leftIndex = "Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA";
		String rightIndex = "Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA";
		reqInfo.put("leftIndex", rightIndex);
		reqInfo.put("rightIndex", leftIndex);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("leftIndex", leftIndex);
		entityInfo.put("rightIndex", rightIndex);
		double score = fingerPrint.matchMultiMinutae(reqInfo, entityInfo);
		fingerPrint.matchMultiImage(reqInfo, entityInfo);
		assertTrue(score < 60);
	}

}
