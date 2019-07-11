package io.mosip.registrationProcessor.perf.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class GoogleTranslate {

	private String key;

	public GoogleTranslate(String apiKey) {
		key = apiKey;
	}

//	static {
//		System.setProperty("http.proxyHost", "172.22.218.218");
//		System.setProperty("http.proxyPort", "8085");
//		System.setProperty("https.proxyHost", "172.22.218.218");
//		System.setProperty("https.proxyPort", "8085");
//	}

	String translate(String text, String from, String to) {
		StringBuilder result = new StringBuilder();
		try {
			String encodedText = URLEncoder.encode(text, "UTF-8");
			String urlStr = "https://www.googleapis.com/language/translate/v2?key=" + key + "&q=" + encodedText
					+ "&target=" + to + "&source=" + from;

			URL url = new URL(urlStr);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			InputStream stream;
			if (conn.getResponseCode() == 200) // success
			{
				System.out.println("HTTP response code while connecting to Google translate is 200");
				stream = conn.getInputStream();
			} else {
				System.out.println("Response code is " + conn.getResponseCode());
				System.out.println("HTTP response code while connecting to Google translate is not 200");
				stream = conn.getErrorStream();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			JsonParser parser = new JsonParser();

			JsonElement element = parser.parse(result.toString());

			if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				if (obj.get("error") == null) {
					String translatedText = obj.get("data").getAsJsonObject().get("translations").getAsJsonArray()
							.get(0).getAsJsonObject().get("translatedText").getAsString();
					return translatedText;

				}
			}

			if (conn.getResponseCode() != 200) {
				System.err.println(result);
			}

		} catch (IOException | JsonSyntaxException ex) {
			System.err.println(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
