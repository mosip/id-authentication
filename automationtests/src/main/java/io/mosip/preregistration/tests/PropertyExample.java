package io.mosip.preregistration.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import io.mosip.util.CommonLibrary;

public class PropertyExample 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream( "src/config/IDRepo.properties" ));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> mapProp = prop.entrySet().stream().collect(
		    Collectors.toMap(
		        e -> (String) e.getKey(),
		        e -> (String) e.getValue()
		    ));
		
		//System.out.println("mapProp::"+mapProp);
		
		//System.out.println("map Entry::"+mapProp.get("preReg_CreateApplnURI")); 

	CommonLibrary commonLibrary = new CommonLibrary();
		String Document_request=commonLibrary.fetch_IDRepo().get("req.Documentrequest");
		System.out.println("Document_request::"+Document_request); 
		
	}

}
