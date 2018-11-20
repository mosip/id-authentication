package io.mosip.authentication.core.util;

/**
 * Method to generate masking the values
 * 
 * @author Sanjay Murali
 */
public class MaskUtil {
	
	private MaskUtil() {
		
	}
	
	public static String generateMaskValue(String maskValue, int maskNo) {
		char[] maskedDetail = maskValue.toCharArray();
		for(int i=0; i<maskNo && i < maskedDetail.length; i++) {
			maskedDetail[i] = 'X';
		}
		return String.valueOf(maskedDetail);
	}
	
}
