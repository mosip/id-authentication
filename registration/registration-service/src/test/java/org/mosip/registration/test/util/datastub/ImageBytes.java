package org.mosip.registration.test.util.datastub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ImageBytes {

	public static void main(String args[]) throws IOException {
		File image = new File(new ImageBytes().getClass().getResource("test.jpg").getFile());
		File zipFile = new File("D:\\image.zip");
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(arrayOutputStream);
		FileInputStream fileInputStream = new FileInputStream(image);
		byte[] img = new byte[fileInputStream.available()];
		fileInputStream.read(img);
		ZipEntry entry = new ZipEntry("Applicant/Demographic/img/img.jpg");
		zip.putNextEntry(entry);
		zip.write(img);
		zip.flush();
		entry = new ZipEntry("img/img1.jpg");
		zip.putNextEntry(entry);
		zip.write(img);
		zip.flush();
		zip.close();
		fileInputStream.close();
		FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		fileOutputStream.write(arrayOutputStream.toByteArray());
		fileOutputStream.close();
	}
}
