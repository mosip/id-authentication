package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class EncrypterDecrypter {
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	private final String decrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/decryptPacket";
	private final String encrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/encryptPacket";
	
	public File decryptFile(File f,String path) throws IOException, ZipException {
		System.out.println(path);
		Response response=applnMethods.putFile(f,decrypterURL);
		System.out.println(response.contentType());
		if(response.contentType().equals("application/zip")) {
		System.out.println(response.asByteArray());
		try (FileOutputStream fileOuputStream = new FileOutputStream(path)){
			    fileOuputStream.write(response.asByteArray());
			 }
		 ZipFile zipFile = new ZipFile(path);
         zipFile.extractAll(path.substring(0, path.lastIndexOf('.')));
         File extractedFile=new File(path.substring(0, path.lastIndexOf('.')));
		return extractedFile;
		} else {
			System.out.println("Utility Service Is Not Running");
		}
		return null;
	}
	
	public void encryptFile(File f,String sourcePath,String destinationPath) throws ZipException, FileNotFoundException, IOException {
	
		File folder = new File(destinationPath);
		folder.mkdirs();
		System.out.println("File name is :: "+ sourcePath+"/"+f.getName());
		System.out.println("Inside EncryptFile Method");
		System.out.println("File Path Is :: "+destinationPath+"/"+f.getName()+".zip");
		 org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath+"/"+f.getName()),new File(destinationPath+"/"+f.getName()+".zip"));
		  File file1=new File(destinationPath+"/"+f.getName()+".zip");
		  Response response=applnMethods.putDecryptedFile(file1,encrypterURL);
		  System.out.println("Response from encrypter URL is  :: "+response.contentType());
		  try (FileOutputStream fileOuputStream = new FileOutputStream(destinationPath+"/"+f.getName()+".zip")){
			    fileOuputStream.write(response.asByteArray());
			    System.out.println("Successfully Wrote The file");
			 }
		  
	}
	public void destroyFiles(String filePath) throws ZipException, FileNotFoundException, IOException {
		System.out.println("Destroying Files");
		File file = new File(filePath);
		File zipFile=new File(filePath+".zip");
		System.out.println(filePath);
		System.out.println(filePath+".zip");
		file.delete();
		 Response response=applnMethods.putDecryptedFile(zipFile,encrypterURL);
		  System.out.println("Response from encrypter URL is  :: "+response.contentType());
		  try (FileOutputStream fileOuputStream = new FileOutputStream(filePath+".zip")){
			    fileOuputStream.write(response.asByteArray());
			    System.out.println("Successfully Wrote The file");
			 }
	}
}
