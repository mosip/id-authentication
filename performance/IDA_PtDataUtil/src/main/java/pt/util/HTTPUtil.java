package pt.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTPUtil {

	private final static String USER_AGENT = "Mozilla/5.0";

	public HTTPUtil() {

	}

	public static String sendHttpRequest(String url, String parameters, String type) throws IOException {

		StringBuffer response = null;

		URL url1 = null;

		if (type == "GET") {
			if (parameters.isEmpty()) {
				url1 = new URL(url);
			} else {
				url1 = new URL(url + "?" + parameters);
			}

		} else if (type == "POST") {
			url1 = new URL(url);
		}

		HttpURLConnection con = (HttpURLConnection) url1.openConnection();

		con.setRequestMethod(type);
		if (type == "POST") {
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append('\n');
		}
		in.close();
		return response.toString();
	}

	public static String sendHttpsRequest(String strUrl, String parameters, String type) throws IOException {
		StringBuffer response = new StringBuffer();
		URL url = null;
		if (type == "GET") {
			if (parameters.isEmpty()) {
				url = new URL(strUrl);
			} else {
				url = new URL(strUrl + "?" + parameters);
			}

		} else if (type == "POST") {
			url = new URL(strUrl);
		}
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod(type);

		if (type == "POST") {
			//System.out.println("parameters being sent is ");
			//System.out.println(parameters);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String input;
		while ((input = br.readLine()) != null) {
			//System.out.println(input);
			response.append(input);
			response.append('\n');
		}
		br.close();
		return response.toString();
	}

}
