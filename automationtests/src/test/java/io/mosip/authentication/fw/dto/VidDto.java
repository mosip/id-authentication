package io.mosip.authentication.fw.dto;

import java.util.Map;

public class VidDto {

	private static Map<String,String> vid;

	public static Map<String, String> getVid() {
		return vid;
	}

	public static void setVid(Map<String, String> vid) {
		VidDto.vid = vid;
	}

}
