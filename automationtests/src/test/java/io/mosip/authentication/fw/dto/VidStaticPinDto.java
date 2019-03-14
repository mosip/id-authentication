package io.mosip.authentication.fw.dto;

import java.util.Map;

public class VidStaticPinDto {
		
		private static Map<String,String> vidStaticPin;

		public static Map<String, String> getVidStaticPin() {
			return vidStaticPin;
		}

		public static void setVidStaticPin(Map<String, String> vidStaticPin) {
			VidStaticPinDto.vidStaticPin = vidStaticPin;
		}

}
