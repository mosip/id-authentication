package io.mosip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Dinga {

	public static void main(String[] args) {
		String timeStampWithHour = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).replace(".", "");
		
		System.err.println(timeStampWithHour);
		
		SimpleDateFormat ttampWithHour = new SimpleDateFormat("yyyyMMddHHmmss");
		ttampWithHour.setTimeZone(TimeZone.getTimeZone("UTC"));
		String val = ttampWithHour.format(new Date());
		
		System.err.println(val);
	}
}
