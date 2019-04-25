package io.mosip.preregistration.tests;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import io.mosip.util.CommonLibrary;
public class Sample {
	private static CommonLibrary commonLibrary = new CommonLibrary();
	HashMap<String, String>  parm =new HashMap<>();
	static Map<String, String> configParamMap= new HashMap<>();
	
	@Value("${ui.config.params}")
	private static String uiConfigParams;
	
	public  HashMap<String, String> readConfigProperty(String url,String configParameter) {
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParamMap= new HashMap<>();
		uiConfigParams=commonLibrary.fetch_IDRepo().get(configParameter);
		String[] uiParams = uiConfigParams.split(",");
		for (int i = 0; i < uiParams.length; i++) {
			reqParams.add(uiParams[i]);
		}
		RestTemplate restTemplate = new RestTemplate();
	
		String s=restTemplate.getForObject(url, String.class);
		final Properties p = new Properties();
		try {
			p.load(new StringReader(s));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (Entry<Object, Object> e : p.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}
			
		}
		return (HashMap<String, String>) configParamMap;
	}
	public static void main(String[] args) throws IOException
	{
		String uiConfigParameter = "ui.config.params";
		String url = "http://104.211.212.28:51000/pre-registration/qa/0.10.0/pre-registration-qa.properties";
		Map<String, String> configParams = new HashMap<>();
		List<String> reqParams = new ArrayList<>();
		System.out.println("===========");
		
	}	
}
