package io.mosip.registration.processor.packet.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;

/**
 * The Class PacketInfoManagerApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.auditmanager", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client" })

public class PacketInfoManagerApplication implements CommandLineRunner{

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	@Autowired
	PacketInfoManagerImpl packetInfoManagerImpl;
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		
		//packetInfoManagerImpl.
		String filePath="C:\\Users\\M1049387\\Desktop\\ID.json";
		//Scanner sc=new Scanner(new File("C:\\Users\\M1049387\\Desktop\\ID.json"));
		//System.out.println(" JSON  DATA ::   "+usingBufferedReader(filePath));
		IndividualDemographicDedupe demographicData =packetInfoManagerImpl.getIdentityKeysAndFetchValuesFromJSON(usingBufferedReader(filePath));
		
		 JsonValue[] name=demographicData.getName();
		 
		// System.out.println("How many names types "+name.size());
			
			 for (JsonValue jsonValue : name) {
				
			
				 System.out.println(jsonValue.getLanguage());
				 System.out.println(jsonValue.getValue());
			}
		
		 
		 
 String dob=demographicData.getDateOfBirth();
		 
		 System.out.println("How many dob types "+dob);
		 
			
				/*List<FieldValue> metaData=new ArrayList<>();

				FieldValue regId = new FieldValue();
				regId.setLabel("registrationId");
				regId.setValue("27847657360002520181208094033");
				
				
				FieldValue preId = new FieldValue();
				preId.setLabel("preRegistrationId");
				preId.setValue(null);
				metaData.add(regId);
				metaData.add(preId);

			 File file = new File("C:\\Users\\M1049387\\Desktop\\ID.json");
				InputStream inputStream = new FileInputStream(file);
			 packetInfoManagerImpl.saveDemographicInfoJson(inputStream,metaData);*/
		 
		 
	}
	
	private static String usingBufferedReader(String filePath)
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
	    {
	 
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null)
	        {
	            contentBuilder.append(sCurrentLine).append("\n");
	        }
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}

}