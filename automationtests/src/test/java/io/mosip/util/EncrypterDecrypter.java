package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class EncrypterDecrypter {
	private static Logger logger = Logger.getLogger(EncrypterDecrypter.class);
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	private final String decrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/decryptPacket";
	private final String encrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/encryptPacket";
	
	public File decryptFile(File f,String path) throws IOException, ZipException {
		logger.info(path);
		Response response=applnMethods.putFile(f,decrypterURL);
		
		if(response.contentType().equals("application/zip")) {
		
		try (FileOutputStream fileOuputStream = new FileOutputStream(path)){
			    fileOuputStream.write(response.asByteArray());
			    fileOuputStream.close();
			 }
		 ZipFile zipFile = new ZipFile(path);
         zipFile.extractAll(path.substring(0, path.lastIndexOf('.')));
         File extractedFile=new File(path.substring(0, path.lastIndexOf('.')));
         
		return extractedFile;
		} else {
			logger.info("Utility Service Is Not Running");
		}
		return null;
	}
	
	public void encryptFile(File f,String sourcePath,String destinationPath) throws ZipException, FileNotFoundException, IOException {
	
		File folder = new File(destinationPath);
		folder.mkdirs();
		logger.info("File name is :: "+ sourcePath+"/"+f.getName());
		logger.info("Inside EncryptFile Method");
		logger.info("File Path Is :: "+destinationPath+"/"+f.getName()+".zip");
		 org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath+"/"+f.getName()),new File(destinationPath+"/"+f.getName()+".zip"));
		  File file1=new File(destinationPath+"/"+f.getName()+".zip");
		  Response response=applnMethods.putDecryptedFile(file1,encrypterURL);
		 logger.info("Response from encrypter URL is  :: "+response.contentType());
		  try (FileOutputStream fileOuputStream = new FileOutputStream(destinationPath+"/"+f.getName()+".zip")){
			    fileOuputStream.write(response.asByteArray());
			    fileOuputStream.close();
			    logger.info("Successfully Wrote The file");
			 }
		  
	}
	public void destroyFiles(File file) throws IOException {
//		if(!file.canWrite()) {
		logger.info("Destroying Files");

		File[] listOfFiles=file.listFiles();
		
		for(File f: listOfFiles) {
			if(f.isFile()) {
				try {
					FileDeleteStrategy.FORCE.delete(f);
					logger.info("File Was Deleted");
				} catch (Exception e) {
					logger.info(f.getName()+" Was Not Deleted");
					e.printStackTrace();
				}
			} else if(f.isDirectory()) {
				try {
					FileUtils.deleteDirectory(f);
					logger.info("Folder Was Deleted");
				} catch (Exception e) {
					logger.info(f.getName()+"  Was Not Deleted");
				}
			}
		}
		try {
			FileUtils.deleteDirectory(file);
			logger.info("Decrypted File Was Deleted");
		} catch (Exception e) {
			logger.info("Decrypted File Has Some Files In It");
		}
		}/*else {
			logger.info("File Is Being Used By"+file.setWritable(true));
		}*/
	
	public void revertPacketToValid(String filePath) throws FileNotFoundException, IOException {
		File zipFile = new File(filePath+".zip");
		 Response response=applnMethods.putDecryptedFile(zipFile,encrypterURL);
		 logger.info("Response from encrypter URL is  :: "+response.contentType());
		  try (FileOutputStream fileOuputStream = new FileOutputStream(filePath+".zip")){
			    fileOuputStream.write(response.asByteArray());
			    fileOuputStream.close();
			  logger.info("Successfully Wrote The file");
			 }
	}
}
