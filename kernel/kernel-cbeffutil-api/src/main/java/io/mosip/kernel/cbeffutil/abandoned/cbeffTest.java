package io.mosip.kernel.cbeffutil.abandoned;

import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ObjectFactory;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.jaxbclasses.TestBiometrics;
import io.mosip.kernel.cbeffutil.service.impl.CbeffImpl;

public class cbeffTest {

	public static void main(String[] args) throws Exception {
	/*	BIR bir = new BIR.BIRBuilder().withBdb(new String("Sample Bytes").getBytes()).withBdbInfo(new BDBInfo.BDBInfoBuilder()
				.withFormatOwner(new Long(257)).withFormatType(new Long(7)).withQuality(90).withType(Arrays.asList(SingleType.FINGER))
				.withPurpose(PurposeType.ENROLL)
				.withLevel(ProcessedLevelType.RAW).withCreationDate(new Date()).build()).build();
		List<BIR> birList = new ArrayList<>();
		birList.add(bir);
		CbeffImpl cbeff = new CbeffImpl();
		byte[] fileBytes = cbeff.createXML(birList);
		System.out.println(new String(fileBytes));
		List<BIR> birList1 = new ArrayList<>();
		BIR bir1 = new BIR.BIRBuilder().withBdb(new String("Sample Bytes1").getBytes()).withBdbInfo(new BDBInfo.BDBInfoBuilder()
				.withFormatOwner(new Long(257)).withFormatType(new Long(8)).withQuality(90).withType(Arrays.asList(SingleType.FACE)).withPurpose(PurposeType.ENROLL)
				.withLevel(ProcessedLevelType.RAW).build()).build();
		BIR bir2 = new BIR.BIRBuilder().withBdb(new String("Sample Bytes1").getBytes()).withBdbInfo(new BDBInfo.BDBInfoBuilder()
				.withFormatOwner(new Long(257)).withFormatType(new Long(2)).withQuality(90).withType(Arrays.asList(SingleType.FINGER)).withPurpose(PurposeType.ENROLL)
				.withLevel(ProcessedLevelType.RAW).build()).build();
		birList1.add(bir1);
		birList1.add(bir2);
		byte[] updatedFile = cbeff.updateXML(birList1, fileBytes);
		System.out.println("------------------");
		System.out.println(new String(updatedFile));*/
		CbeffImpl cbeff = new CbeffImpl();
		getTestElementDetails(readCreatedXML("TestCbeff"), readXSD("cbeff"));
		//System.out.println(String.join(" ", Arrays.asList("Test1","Test2")));
		//cbeff.validateXML(readCreatedXML("createCbeff"), readXSD("cbeff"));		
		//cbeff.getBDBBasedOnType(readCreatedXML("UpdateCbeff"),"FMR",null);
	}
	
	public static String getTestElementDetails(byte[] xmlBytes, byte[] xsdBytes) throws Exception {
		//BIRType bir = CbeffValidator.getBIRFromXML(xmlBytes);
		JAXBContext jc = JAXBContext.newInstance(BIRType.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		JAXBElement<BIRType> jaxBir =  (JAXBElement<BIRType>) unmarshaller.unmarshal(new File("C:\\Users\\M1049825\\workspace-new\\CbeffUtil\\src\\io\\mosip\\kernel\\cbeffutil\\test\\resources\\schema\\TestCbeff.xml"));
		System.out.println(jaxBir.getValue().getAny());
		return null;
	}
	
	private static byte[] readCreatedXML(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get("C:\\Users\\M1049825\\workspace-new\\CbeffUtil\\src\\io\\mosip\\kernel\\cbeffutil\\test\\resources" + "\\schema\\" + name + ".xml"));
		return fileContent;
	}
	
	private static byte[] readXSD(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get("C:\\Users\\M1049825\\workspace-new\\CbeffUtil\\src\\io\\mosip\\kernel\\cbeffutil\\test\\resources" + "\\schema\\" + name + ".xsd"));
		return fileContent;
	}
	
	private static void createXMLFile(byte[] updatedXmlBytes) throws Exception {
		File tempFile = new File("C://Users/M1049825/Documents/img/cbeffcreate.xml");
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(updatedXmlBytes);
		fos.close();
	}

}


