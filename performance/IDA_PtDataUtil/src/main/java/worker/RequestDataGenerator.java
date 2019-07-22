package worker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;

import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.Key;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.YMLUtil;

public class RequestDataGenerator {

	public RequestDataGenerator() {
	}

	public AuthEntity generateRequestData(EncryptionResponse encData) {
		YMLUtil ymlUtil = new YMLUtil();
		AuthEntity authE = null;
		try {
			authE = ymlUtil.loadRequestData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		authE.setReqTime(getCurrDate());
		Key key = authE.getKey();
		// key.setPublicKeyCert(publicKeyCert);
		key.setSessionKey(encData.getEncryptedSessionKey());
		authE.setKey(key);
		authE.setRequest(encData.getEncryptedIdentity());
		// System.out.println("auth entity is");
		// System.out.println((new Gson()).toJson(authE));
		return authE;

	}

	private String getCurrDate() {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS+05:30";
		Calendar now = Calendar.getInstance();
		Date date = now.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate = dateFormat.format(date);
		// System.out.println(strDate);
		return strDate;
	}

}
