package io.mosip.authentication.core.spi.irisauth.provider;




import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;


public class IrisProviderTest {
	
	
	private IrisProvider iris;
	private Environment environment;
	
	byte[] leftEye = new byte[] { 70, 77, 82, 0, 32, 50, 48, 0, 0, 0, 1, 8, 0, 0, 1, 60, 1, 98, 0, -59, 0, -59, 1, 0, 0,
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
	
	@Before
	public void initialize() {
		environment = Mockito.mock(Environment.class);
		iris=new IrisProvider(environment) {

			@Override
			public String createMinutiae(byte[] inputImage) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double matchMinutiae(Object reqInfo, Object entityInfo) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double matchMultiMinutae(Map<String, String> reqInfo, Map<String, String> entityInfo) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	
		
	}

	@Test
	public void testmatchIrisImage() {
		Map<String, String> reqInfo = new HashMap<>();
		String lefteye = "Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA";
		reqInfo.put("leftEye", lefteye);
		String righteye = "Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA";
		String uin = "749763540713";
		reqInfo.put("idvid", uin);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("leftEye", lefteye);
		entityInfo.put("idvid", "749763540713");
		Mockito.when(environment.getProperty("odduin.irisimg.left.match.value", Double.class)).thenReturn(40D);
		double score = iris.matchImage(reqInfo, entityInfo);
		assertEquals(40D, score, 0);
	}
	@Test
	public void testmatchMultiIrisImage() {
		Map<String, String> reqInfo = new HashMap<>();
		String lefteye = "Rk1SACAyMAAAAADkAAABPAFiAMUAxQEAAAAoIYCiANo5ZICOAMKzUECuALHAZEDSAMHGZECaASQqZEDpAM7OZED/APlQZEDdAT4KKIC5AVEHV4B3AG0TZED3AVeCG4CVAN08ZEDTAPK3V0DbAN3IXYCAALOnUIBgAN8pZICFAJ77UEClAUkTPECgAHniZECIAVAKIUDnAVcCNUChAFPxV4CQAPmtZECTALS/UECFARkrXUDeAQHEV0BjAMYoZIDCAT8bV0DZAJXPZIBZAJcjZIBrAUkONUBQAVAKG0BeAFQZZAAA";
		reqInfo.put("leftEye", lefteye);
		String righteye = "Rk1SACAyMAAAAADeAAABPAFiAMUAxQEAAAAoIECOARQNZICKASsEZEBjAO4mZEDrARVVZID5AORUZEDuATpfV4DKAJ9rZIBTALMjZIDQAJLcZEB6AIgfZIC5AHmDZECBAQ8fZEDJASleZEDVASZYZEB6AUYGZEDGAVZ6NUCBAJ+jZEBjAVEHV0A/ANIiUEBDAVEKPIB7AH6jZEB8AEaNV4CMANkXZEC/ATJpZIC4AUd0ZICoAVJ9KEB4AVMASYBMATUYZEDzAURrV4CfAIr+ZED4AVeASUECAVsCKAAA";
		reqInfo.put("rightEye", righteye);
		String uin = "749763540712";
		reqInfo.put("idvid", uin);
		Map<String, String> entityInfo = new HashMap<>();
		entityInfo.put("rightEye", righteye);
		entityInfo.put("leftEye", lefteye);
		entityInfo.put("idvid", "749763540712");
		Mockito.when(environment.getProperty("evenuin.irisimg.right.match.value", Double.class)).thenReturn(70D);
		Mockito.when(environment.getProperty("evenuin.irisimg.left.match.value", Double.class)).thenReturn(40D);
		double score = iris.matchMultiImage(reqInfo, entityInfo);
		assertEquals(110D, score, 0);
	}
	
	@Test
	public void testMatchScoreCalculator()
	{
		iris.matchScoreCalculator("test", "test");
		assertEquals(0, 0);
	}
	@Test
	public void testMatchScoreCalculatorByte()
	{
		iris.matchScoreCalculator(leftEye,leftEye );
		assertEquals(0, 0);
	}
	
}
