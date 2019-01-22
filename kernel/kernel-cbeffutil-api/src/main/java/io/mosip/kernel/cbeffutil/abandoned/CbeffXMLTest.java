/**
 * 
 */
package io.mosip.kernel.cbeffutil.abandoned;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.service.impl.CbeffImpl;

/**
 * @author M1049825
 *
 */
public class CbeffXMLTest {/*
	*//**
	 * @param args
	 *//*
	public static void main(String[] args) throws Exception{
		List<CbeffPack> cbeffPack =  getTestObject();
		CbeffImpl CbeffImpl = new CbeffImpl();
		//byte[] xmlBytes = CbeffImpl.createXML(cbeffPack);
		List<CbeffPack> updatedList = getUpdatedObject();
		 Path path = Paths.get("C://Users/M1049825/Documents/img/cbeffupdate.xml");
	     byte[] xmlData = Files.readAllBytes(path);
		byte[] updatedXmlBytes = CbeffImpl.updateXML(updatedList,xmlData);
		createXMLFile(updatedXmlBytes);
		 Path path1 = Paths.get("C://Users/M1049825/Documents/img/cbeffupload.xml");
		 byte[] updateXmlData = Files.readAllBytes(path1);
		CbeffImpl.getBDBBasedOnType(updateXmlData, SingleType.FINGER);
		CbeffImpl.getBDBBasedOnSubType(updateXmlData, SingleAnySubtypeType.LEFT);
		boolean valid = CbeffXSDValidator.validateXMLSchema("C://Users/M1049825/Documents/img/cbeff.xsd",
				xmlBytes);
		if(valid)
		{
			System.out.println("XML is Valid");
		}
		else
			System.out.println("XML is InValid");	

	}
	private static List<CbeffPack> getUpdatedObject() throws Exception {
		List<CbeffPack> updatedList = new ArrayList<>();
		CbeffPack CbeffPack = new CbeffPack();
		CbeffPack.setIntegrity(false);
		CbeffPack.setBDBCreationDate(new Date());
		CbeffPack.setBDBFormatOwner(257);
		CbeffPack.setBDBFormatType(8);
		CbeffPack.setQuality(90);
		CbeffPack.setPurpose(PurposeType.ENROLL);
		CbeffPack.setLevel(ProcessedLevelType.INTERMEDIATE);
		CbeffPack.getType().add(SingleType.HAND_GEOMETRY);
		CbeffPack.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/ISOImage.iso", "Face"));
		updatedList.add(CbeffPack);
		
		CbeffPack CbeffPack1 = new CbeffPack();
		CbeffPack1.setIntegrity(false);
		CbeffPack1.setBDBCreationDate(new Date());
		CbeffPack1.setBDBFormatOwner(257);
		CbeffPack1.setBDBFormatType(7);
		CbeffPack1.setQuality(100);
		CbeffPack1.setPurpose(PurposeType.ENROLL);
		CbeffPack1.setLevel(ProcessedLevelType.INTERMEDIATE);
		CbeffPack1.getType().add(SingleType.FINGER);
		CbeffPack1.getSubtype().add("Left");
		CbeffPack1.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/1.jpg", "Finger"));
		
		
		updatedList.add(CbeffPack);
		updatedList.add(CbeffPack1);
		
		return updatedList;
	}
	private static void createXMLFile(byte[] updatedXmlBytes) throws Exception {
		File tempFile = new File("C://Users/M1049825/Documents/img/cbeffupdate2.xml");
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(updatedXmlBytes);
		fos.close();
	}
	private static List<CbeffPack> getTestObject() throws Exception {
		List<CbeffPack> cbeffvoList = new ArrayList<>();
		CbeffPack CbeffPack = new CbeffPack();
		CbeffPack.setIntegrity(false);
		CbeffPack.setBDBCreationDate(new Date());
		CbeffPack.setBDBFormatOwner(257);
		CbeffPack.setBDBFormatType(8);
		CbeffPack.setQuality(90);
		CbeffPack.setPurpose(PurposeType.ENROLL);
		CbeffPack.setLevel(ProcessedLevelType.INTERMEDIATE);
		CbeffPack.getType().add(SingleType.FACE);
		CbeffPack.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/ISOImage.iso", "Face"));
		//CbeffPack.setBdb(new String("Face").getBytes());
		
		CbeffPack CbeffPack1 = new CbeffPack();
		CbeffPack1.setIntegrity(false);
		CbeffPack1.setBDBCreationDate(new Date());
		CbeffPack1.setBDBFormatOwner(257);
		CbeffPack1.setBDBFormatType(7);
		CbeffPack1.setQuality(80);
		CbeffPack1.setPurpose(PurposeType.ENROLL);
		CbeffPack1.setLevel(ProcessedLevelType.RAW);
		CbeffPack1.getType().add(SingleType.FINGER);
		CbeffPack1.getSubtype().add("Left");
		CbeffPack1.getSubtype().add("IndexFinger");
		CbeffPack1.getSubtype().add("MiddleFinger");
		CbeffPack1.getSubtype().add("RingFinger");
		CbeffPack1.getSubtype().add("LittleFinger");
		CbeffPack1.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/ISOImage.iso", "Finger"));
		//CbeffPack1.setBdb(new String("Right IndexFinger MiddleFinger RingFinger LittleFinger").getBytes());
		
		CbeffPack CbeffPack11 = new CbeffPack();
		CbeffPack11.setIntegrity(false);
		CbeffPack11.setBDBCreationDate(new Date());
		CbeffPack11.setBDBFormatOwner(257);
		CbeffPack11.setBDBFormatType(7);
		CbeffPack11.setQuality(80);
		CbeffPack11.setPurpose(PurposeType.ENROLL);
		CbeffPack11.setLevel(ProcessedLevelType.RAW);
		CbeffPack11.getType().add(SingleType.FINGER);
		CbeffPack11.getSubtype().add("Right");
		CbeffPack11.getSubtype().add("IndexFinger");
		CbeffPack11.getSubtype().add("MiddleFinger");
		CbeffPack11.getSubtype().add("RingFinger");
		CbeffPack11.getSubtype().add("LittleFinger");
		CbeffPack11.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/ISOImage.iso", "Finger"));
		//CbeffPack11.setBdb(new String("Left IndexFinger MiddleFinger RingFinger LittleFinger").getBytes());
		
		CbeffPack CbeffPack12 = new CbeffPack();
		CbeffPack12.setIntegrity(false);
		CbeffPack12.setBDBCreationDate(new Date());
		CbeffPack12.setBDBFormatOwner(257);
		CbeffPack12.setBDBFormatType(7);
		CbeffPack12.setQuality(80);
		CbeffPack12.setPurpose(PurposeType.ENROLL);
		CbeffPack12.setLevel(ProcessedLevelType.RAW);
		CbeffPack12.getType().add(SingleType.FINGER);
		CbeffPack12.getSubtype().add("Left");
		CbeffPack12.getSubtype().add("Right");
		CbeffPack12.getSubtype().add("Thumb");
		CbeffPack12.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/ISOImage.iso", "Finger"));
		//CbeffPack12.setBdb(new String("Left Right Thumb").getBytes());
		
		CbeffPack CbeffPack2 = new CbeffPack();
		CbeffPack2.setIntegrity(false);
		CbeffPack2.setBDBCreationDate(new Date());
		CbeffPack2.setBDBFormatOwner(257);
		CbeffPack2.setBDBFormatType(9);
		CbeffPack2.setQuality(80);
		CbeffPack2.setPurpose(PurposeType.ENROLL);
		CbeffPack2.setLevel(ProcessedLevelType.RAW);
		CbeffPack2.getType().add(SingleType.IRIS);
		CbeffPack2.getSubtype().add("Right");
		CbeffPack2.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/Sample_IRIS.iso", "Iris"));
		//CbeffPack2.setBdb(new String("Right Iris").getBytes());
		
		CbeffPack CbeffPack21 = new CbeffPack();
		CbeffPack21.setIntegrity(false);
		CbeffPack21.setBDBCreationDate(new Date());
		CbeffPack21.setBDBFormatOwner(257);
		CbeffPack21.setBDBFormatType(9);
		CbeffPack21.setQuality(80);
		CbeffPack21.setPurpose(PurposeType.ENROLL);
		CbeffPack21.setLevel(ProcessedLevelType.RAW);
		CbeffPack21.getType().add(SingleType.IRIS);
		CbeffPack21.getSubtype().add("Left");
		CbeffPack21.setBdb(CbeffISOReader.readISOImage("C://Users/M1049825/Documents/img/Sample_IRIS.iso", "Iris"));
		//CbeffPack21.setBdb(new String("Left Iris").getBytes());
		
		cbeffvoList.add(CbeffPack);
		cbeffvoList.add(CbeffPack1);
		cbeffvoList.add(CbeffPack11);
		cbeffvoList.add(CbeffPack12);
		cbeffvoList.add(CbeffPack2);
		cbeffvoList.add(CbeffPack21);
		return cbeffvoList;
	}


*/}
