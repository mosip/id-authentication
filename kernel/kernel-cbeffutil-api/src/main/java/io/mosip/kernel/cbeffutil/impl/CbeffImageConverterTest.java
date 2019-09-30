package io.mosip.kernel.cbeffutil.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CbeffImageConverterTest {

	public static void main(String[] args) throws Exception {	
	byte[] fileContent = getImageAsBytes();
	FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\M1049825\\Documents\\Biometric-Data-Samples\\Biometric-Data-Samples\\ISOImage", "LeftIndexFingerImage.iso"));
	
	int dataLength = fileContent.length;
	long headerLength = 32;
	
	long recordLength = headerLength + dataLength;
	
	DataOutputStream dataOut = new DataOutputStream(fos);
	dataOut.writeInt(0x46495200);			/* 4 */
	dataOut.writeInt(0x30313000);				/* + 4 = 8 */

	writeLong(recordLength, dataOut, 6);			/* + 6 = 14 */
	dataOut.writeShort(0);			/* + 2 = 16 */
	dataOut.writeShort(31);			/* +2 = 18 */
	dataOut.writeByte(1);		/* + 1 = 19 */
	dataOut.writeByte(1);					/* + 1 = 20 */
	dataOut.writeShort(500);	/* + 2 = 22 */
	dataOut.writeShort(500);		/* + 2 = 24 */
	dataOut.writeShort(500);	/* + 2 = 26 */
	dataOut.writeShort(500);	/* + 2 = 28 */
	dataOut.writeByte(8);						/* + 1 = 29 */

	dataOut.writeByte(2);		/* + 1 = 30 */
	dataOut.writeShort(1);
	writeObject(dataOut);
	System.out.println(recordLength);

	}

	private static byte[] getImageAsBytes() throws Exception {
		return Files.readAllBytes(Paths.get("C:\\Users\\M1049825\\Documents\\Biometric-Data-Samples\\Biometric-Data-Samples\\Sample-1\\Finger\\LeftIndexFingerImage.bmp"));
	}

	private static void writeLong(long value, OutputStream out, int byteCount) throws IOException {
		if (byteCount <= 0) { return; }
		for (int i = 0; i < (byteCount - 8); i++) {
			out.write(0);
		}
		if (byteCount > 8) { byteCount = 8; }
		for (int i = (byteCount - 1); i >= 0; i--) {
			long mask = (long)(0xFFL << (i * 8));
			byte b = (byte)((value & mask) >> (i * 8));
			out.write(b);
		}		
	}
	
	protected static void writeObject(OutputStream out) throws Exception {		
		ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
		writeImage(imageOut);
		imageOut.flush();
		byte[] imageBytes = imageOut.toByteArray();
		imageOut.close();
		
		long fingerDataBlockLength = imageBytes.length + 14;

		DataOutputStream dataOut = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);

		/* Finger Information (14) */
		dataOut.writeInt((int)(fingerDataBlockLength & 0xFFFFFFFFL));
		dataOut.writeByte(2);
		dataOut.writeByte(1);
		dataOut.writeByte(1);
		dataOut.writeByte(69);
		dataOut.writeByte(0);
		dataOut.writeShort(275);
		dataOut.writeShort(400);
		dataOut.writeByte(1); /* RFU */

		dataOut.write(imageBytes);
		dataOut.flush();
	}
	
	protected static void writeImage(OutputStream outputStream) throws Exception {
		outputStream.write(getImageAsBytes());
	}

}
