package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.mosip.service.BaseTestCase;

public class AbisMiddlewareTests extends BaseTestCase {
	@DataProvider(name="bioDedupe")
	public File[] getBioDedupePackets() {
		File file = new File(System.getProperty("user.dir") + "/src/test/resources/regProc/Packets/InvalidPackets/BioDedupe");
		File[] listOfPackets = file.listFiles();
		List<File> insideFiles=new ArrayList<File>();
	
		for(File file1:listOfPackets) {
			insideFiles.add(file1);
		}
		File [] objArray = new File[insideFiles.size()];
		for(int i=0;i< insideFiles.size();i++){
		    objArray[i] = insideFiles.get(i);
		 } 
		return objArray;
	}

@Test(dataProvider="bioDedupe")
public void noDemoAndBioMatchPkt(File[] bioDedupePackets) {
	File file=new File(bioDedupePackets[0].getAbsolutePath());
	System.out.println(file.getName());
}
}