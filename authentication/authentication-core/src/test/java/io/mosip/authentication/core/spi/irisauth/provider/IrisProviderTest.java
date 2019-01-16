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
}
