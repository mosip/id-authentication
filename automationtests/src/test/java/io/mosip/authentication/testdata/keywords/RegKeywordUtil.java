package io.mosip.authentication.testdata.keywords;

import java.sql.Timestamp;  
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import io.mosip.authentication.testdata.TestDataUtil;

public class RegKeywordUtil extends KeywordUtil{
	
	@Override
	public Map<String, String> precondtionKeywords(Map<String, String> map) {
		Map<String, String> returnMap = new HashMap<String, String>();
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals("$TIMESTAMP$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateCurrentTimeStamp());
				else
					returnMap.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue().contains("$TIMESTAMP$")) {
					String temp = entry.getValue().replace("$TIMESTAMP$", "");
					if (temp.contains("+")) {
						String[] time = temp.split(Pattern.quote("+"));
						String calType = time[0];
						int number = Integer.parseInt(time[1]);
						returnMap.put(entry.getKey(), generateTimeStamp(calType, "+", number));
					} else if (temp.contains("-")) {
						String[] time = temp.split("-");
						String calType = time[0];
						int number = Integer.parseInt(time[1]);
						returnMap.put(entry.getKey(), generateTimeStamp(calType, "-", number));
					}
			} else if (entry.getValue().contains("$") && entry.getValue().contains(":") && (entry.getValue().startsWith("$input") || entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String jsonFileName = keys[0];
				String fieldName = keys[1];
				String val = TestDataUtil.getCurrTestDataDic().get(jsonFileName).get(fieldName);
				returnMap.put(entry.getKey(), val);
			}
	        else if (entry.getValue().contains("$") && entry.getValue().contains(":") && (entry.getValue().startsWith("$RANDOM"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String type=keys[1];
				String digit=keys[2];
				if(type.equals("N"))
					returnMap.put(entry.getKey(),randomize(Integer.parseInt(digit)));
				if(type.equals("AN"))
					returnMap.put(entry.getKey(),randomize(Integer.parseInt(digit)));
			}
			else
				returnMap.put(entry.getKey(), entry.getValue());
		}
		return returnMap;
		
	}
	private String randomize(int digit){
        Random r = new Random();
        List<Integer> digits=  new ArrayList<Integer>();
        String number = "";
        for (int i = 0; i < digit; i++) {
            digits.add(i);
        }
        for (int i = digit; i > 0; i--) {
            int randomDigit = r.nextInt(i);
            number+=digits.get(randomDigit);
            digits.remove(randomDigit);
        }
        return number;
    }

	private String generateCurrentTimeStamp()
	{
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		return dateFormatter.format(cal.getTime()) + "05:30";
	}
	
	private String generateTimeStamp(String calendarType, String addsub, int number) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		if (calendarType.equals("HOUR") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.HOUR, append);
		} else if (calendarType.equals("HOUR") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.HOUR, number);
		} else if (calendarType.equals("MINUTE") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.MINUTE, append);
		} else if (calendarType.equals("MINUTE") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.MINUTE, number);
		} else if (calendarType.equals("SECOND") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.SECOND, append);
		} else if (calendarType.equals("SECOND") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.SECOND, number);
		}
		return dateFormatter.format(cal.getTime()) + "05:30";
	}

}
