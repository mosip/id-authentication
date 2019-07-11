package worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;

import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.PersonalIdAuthEntity;
import pt.dto.idRepo.ResponseEntity;
import pt.util.JSONUtil;
import pt.util.JdbcUtil;
import pt.util.PropertiesUtil;

public class NameAuthDataUpdater {

	public NameAuthDataUpdater() {

	}

	public void updateNameAuthData() throws FileNotFoundException {

		Gson gson = new Gson();
		Util util = new Util();
		// String basePath = PropertiesUtil.BASE_PATH;
		String authDataPath = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
				+ "personalIdAuth" + File.separator + "authRequests";
		File[] authJsons = util.getAllFilesInDirectory(authDataPath);
		String originalResponseDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
				+ "responses";
		int index = 1;
		String newAuthDir = PropertiesUtil.BASE_PATH + File.separator + "Updated" + File.separator + "nameAuthRequests";
		File f = new File(newAuthDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		for (File file : authJsons) {
			String requestPath = file.getAbsolutePath();
			String filename = extractFilename(requestPath);
			String responseFilePath = originalResponseDir + File.separator + filename;
			ResponseEntity response = null;
			try {
				response = JSONUtil.mapExternalJsonToObject1(responseFilePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String entityUrl = response.getResponse().getEntity();
			String[] arr = entityUrl.split("/");
			String uin = arr[arr.length - 1];
			String vid = JdbcUtil.fetchVidForUin(uin);
			String filepath = newAuthDir + File.separator + "authdata" + index + ".json";
			PersonalIdAuthEntity authEntity = JSONUtil.mapExternalJsonToPersonalIdAuthEntity(requestPath);
			authEntity.setReqTime(getCurrTime());
			authEntity.setIdvId(vid);
			authEntity.setIdvIdType("V");
			JSONUtil.writeJSONToFile(gson.toJson(authEntity), filepath);
			index++;
			if (index == 30)
				break;
		}

	}

	private String extractFilename(String requestPath) {

		String separator = "\\\\";
		String[] pathSections = requestPath.split(separator);
		System.out.println(pathSections[pathSections.length - 1]);
		return pathSections[pathSections.length - 1];
	}

	private String getCurrTime() {
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
