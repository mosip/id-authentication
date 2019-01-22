/**
 * 
 */
package io.mosip.kernel.cbeffutil.abandoned;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import io.mosip.kernel.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SBInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.jaxbclasses.VersionType;

/**
 * @author M1049825
 *
 */
public class CbeffCreator {

	private static final int FORMAT_IDENTIFIER = 0x46495200;

	private int captureDeviceId;
	private int acquisitionLevel;
	private int scaleUnits;
	private int scanResolutionHorizontal, scanResolutionVertical;
	private int imageResolutionHorizontal, imageResolutionVertical;
	private int depth;
	private int compressionAlgorithm, version, count;
	private long recordLength;

	public static void main(String[] args) throws Exception {
		CbeffCreator cbeff = new CbeffCreator();
		byte[] bdb = cbeff.readISO();
		VersionType versionType = new VersionType();
		versionType.setMajor(1);
		versionType.setMinor(1);
		VersionType cbeffVersion = new VersionType();
		cbeffVersion.setMajor(1);
		cbeffVersion.setMinor(1);
		BIRType bir = new BIRType();
		BIRInfoType birInfo = new BIRInfoType();
		bir.setBIRInfo(birInfo);
		bir.setVersion(versionType);
		bir.setCBEFFVersion(cbeffVersion);
		bir.getBIR().add(getObject1());
		//bir.getBIR().add(getObject2());
		//bir.getBIR().add(getObject3());
		

		//System.out.println("XML Validation : " + CbeffValidator.validateXMLSchema("C://Users/M1049825/Documents/img/cbeff.xsd",
		//		"C://Users/M1049825/Documents/img/cb3.xml"));

	}

	private static BIRType getObject3() throws Exception {
		VersionType versionType = new VersionType();
		versionType.setMajor(1);
		versionType.setMinor(1);
		VersionType cbeffVersion = new VersionType();
		cbeffVersion.setMajor(1);
		cbeffVersion.setMinor(1);
		BIRType bir = new BIRType();
		bir.setVersion(versionType);
		bir.setCBEFFVersion(cbeffVersion);
		BIRInfoType birInfo = new BIRInfoType();
		birInfo.setCreationDate(new Date());
		birInfo.setCreator("8876ff54");
		birInfo.setIndex("54ba8dbc-3d01-437a-8b24-28619fd47e0b");
		birInfo.setIntegrity(true);
		birInfo.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		birInfo.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bir.setBIRInfo(birInfo);
		BDBInfoType bDBInfoType = new BDBInfoType();
		bDBInfoType.setCreationDate(new Date());
		bDBInfoType.setQuality(90);
		bDBInfoType.setPurpose(PurposeType.ENROLL);
		bDBInfoType.setLevel(ProcessedLevelType.INTERMEDIATE);
		bDBInfoType.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		bDBInfoType.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bDBInfoType.getType().add(SingleType.FACE);
		SBInfoType sBInfoType = new SBInfoType();
		bir.setBDBInfo(bDBInfoType);
		bir.setSBInfo(sBInfoType);
		CbeffCreator cbeff = new CbeffCreator();
		bir.setBDB(cbeff.readISO());
		bir.setSB(new String("Signature").getBytes());
		return bir;
	}

	private static BIRType getObject2() throws Exception {
		VersionType versionType = new VersionType();
		versionType.setMajor(1);
		versionType.setMinor(1);
		VersionType cbeffVersion = new VersionType();
		cbeffVersion.setMajor(1);
		cbeffVersion.setMinor(1);
		BIRType bir = new BIRType();
		bir.setVersion(versionType);
		bir.setCBEFFVersion(cbeffVersion);
		BIRInfoType birInfo = new BIRInfoType();
		birInfo.setCreationDate(new Date());
		birInfo.setCreator("6246246");
		birInfo.setIndex("54ba8dbc-3d01-437a-8b24-28619fd47e0b");
		birInfo.setIntegrity(true);
		birInfo.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		birInfo.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bir.setBIRInfo(birInfo);
		BDBInfoType bDBInfoType = new BDBInfoType();
		bDBInfoType.setCreationDate(new Date());
		bDBInfoType.setQuality(80);
		bDBInfoType.setPurpose(PurposeType.ENROLL);
		bDBInfoType.setLevel(ProcessedLevelType.RAW);
		bDBInfoType.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		bDBInfoType.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bDBInfoType.getType().add(SingleType.FINGER);
		bDBInfoType.getSubtype().add("Left");
		bDBInfoType.getSubtype().add("Right");
		bDBInfoType.getSubtype().add("Thumb");
		bir.setBDBInfo(bDBInfoType);
		CbeffCreator cbeff = new CbeffCreator();
		bir.setBDB(cbeff.readISO());
		bir.setSB(new String("Signature").getBytes());
		return bir;
	}

	private static BIRType getObject1() throws Exception {
		VersionType versionType = new VersionType();
		versionType.setMajor(1);
		versionType.setMinor(1);
		VersionType cbeffVersion = new VersionType();
		cbeffVersion.setMajor(1);
		cbeffVersion.setMinor(1);
		BIRType bir = new BIRType();
		bir.setVersion(versionType);
		bir.setCBEFFVersion(cbeffVersion);
		BIRInfoType birInfo = new BIRInfoType();
		birInfo.setCreationDate(new Date());
		birInfo.setCreator("887654");
		birInfo.setIndex("54ba8dbc-3d01-437a-8b24-28619fd47e0b");
		birInfo.setIntegrity(true);
		birInfo.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		birInfo.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bir.setBIRInfo(birInfo);
		BDBInfoType bDBInfoType = new BDBInfoType();
		bDBInfoType.setCreationDate(new Date());
		bDBInfoType.setQuality(80);
		bDBInfoType.setPurpose(PurposeType.ENROLL);
		bDBInfoType.setLevel(ProcessedLevelType.RAW);
		bDBInfoType.setNotValidAfter(new Date(new Date().getTime() + 30 * 60 * 60));
		bDBInfoType.setNotValidBefore(new Date(new Date().getTime() - 30 * 60 * 60));
		bDBInfoType.getType().add(SingleType.IRIS);
		bDBInfoType.getSubtype().add("Left");
		SBInfoType sBInfoType = new SBInfoType();
		bir.setBDBInfo(bDBInfoType);
		bir.setSBInfo(sBInfoType);
		CbeffCreator cbeff = new CbeffCreator();
		bir.setBDB(cbeff.readISO());
		bir.setSB(new String("Signature").getBytes());
		return bir;
	}

	

	private byte[] readISO() throws Exception {
		String path = "C://Users/M1049825/Documents/img/testimg1.iso";
		File testFile = new File(path);
		DataInputStream in = new DataInputStream(new FileInputStream(testFile));
		int fir0 = in.readInt();
		/* header (e.g. "FIR", 0x00) (4) */
		if (fir0 != FORMAT_IDENTIFIER) {
			throw new IllegalArgumentException("'FIR' marker expected! Found " + Integer.toHexString(fir0));
		}
		version = in.readInt(); /* version in ASCII (e.g. "010" 0x00) (4) */
		recordLength = readUnsignedLong(in, 6); // & 0xFFFFFFFFFFFFL;
		captureDeviceId = in
				.readUnsignedShort(); /*
										 * all zeros means 'unreported', only
										 * lower 12-bits used, see 7.1.4 ISO/IEC
										 * 19794-4.
										 */
		acquisitionLevel = in.readUnsignedShort();
		count = in.readUnsignedByte();
		scaleUnits = in.readUnsignedByte(); /* 1 -> PPI, 2 -> PPCM */
		scanResolutionHorizontal = in.readUnsignedShort();
		scanResolutionVertical = in.readUnsignedShort();
		imageResolutionHorizontal = in
				.readUnsignedShort(); /* should be <= scanResH */
		imageResolutionVertical = in
				.readUnsignedShort(); /* should be <= scanResV */
		depth = in
				.readUnsignedByte(); /*
										 * 1 - 16 bits, i.e. 2 - 65546 gray levels
										 */
		
		
		compressionAlgorithm = in.readUnsignedByte();
		int RFU = in.readUnsignedShort();
		System.out.println("RFU : "+RFU);
		long imageLength = recordLength - 14;
		System.out.println("Image Length :" + imageLength);

		byte[] result = new byte[(int) testFile.length()];
		FileInputStream fileIn = new FileInputStream(testFile);
		int bytesRead = 0;
		while (bytesRead < result.length) {
			bytesRead += fileIn.read(result, bytesRead, result.length - bytesRead);
		}
		System.out.println("Metadata captured from ISO file [captureDeviceId=" + captureDeviceId + ", acquisitionLevel="
				+ acquisitionLevel + ", scaleUnits=" + scaleUnits + ", scanResolutionHorizontal="
				+ scanResolutionHorizontal + ", scanResolutionVertical=" + scanResolutionVertical
				+ ", imageResolutionHorizontal=" + imageResolutionHorizontal + ", imageResolutionVertical="
				+ imageResolutionVertical + ", depth=" + depth + ", compressionAlgorithm=" + compressionAlgorithm
				+ ", version=" + version + ", count=" + count + ", recordLength=" + recordLength + "]");
		return result;
	}

	private static long readUnsignedLong(InputStream in, int byteCount) throws IOException {
		DataInputStream dataIn = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
		byte[] buf = new byte[byteCount];
		dataIn.readFully(buf);
		long result = 0L;
		for (int i = 0; i < byteCount; i++) {
			result <<= 8;
			result += (int) (buf[i] & 0xFF);
		}
		return result;
	}

}
