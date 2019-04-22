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
	
	public static HashMap<String, String> config() throws IOException {
		List<String> reqParams = new ArrayList<>();
		
		uiConfigParams=commonLibrary.fetch_IDRepo().get("ui.config.params");
		
		String[] uiParams = uiConfigParams.split(",");
		for (int i = 0; i < uiParams.length; i++) {
			reqParams.add(uiParams[i]);
		}
		RestTemplate restTemplate = new RestTemplate();
	
		String s=restTemplate.getForObject("http://104.211.212.28:51000/pre-registration/qa/0.10.0/pre-registration-qa.properties", String.class);
		final Properties p = new Properties();
		p.load(new StringReader(s));
		for (Entry<Object, Object> e : p.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
			System.out.println(String.valueOf(e.getKey()) +" ---"+e.getValue().toString());
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
				
			}
			
		}
		return (HashMap<String, String>) configParamMap;
	}
	public static void main(String[] args) throws IOException
	{
		HashMap<String, String> a = config();
		System.out.println("==========="+a);
		
	}	
}
