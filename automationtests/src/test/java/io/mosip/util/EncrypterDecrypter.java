package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.zip.ZipInputStream;


import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.dbdto.DecrypterDto;
import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;
import javassist.bytecode.ByteArray;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
/**
 * 
 * @author M1047227
 *
 */

public class EncrypterDecrypter {
	private static Logger logger = Logger.getLogger(EncrypterDecrypter.class);
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	private final String decrypterURL="https://qa.mosip.io/cryptomanager/v1.0/decrypt";
	private final String encrypterURL="https://qa.mosip.io/cryptomanager/v1.0/encrypt";
	private String applicationId="REGISTRATION";	
	InputStream outstream = null;
	/**
	 * 
	 * @param file
	 * @param destinationPath
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 * @throws ParseException 
	 */
	public File decryptFile(JSONObject decryptDto,String destinationPath) throws IOException, ZipException, ParseException {
		logger.info(destinationPath);
		
		Response response=applnMethods.postRequestToDecrypt(decryptDto, decrypterURL);
		logger.info("Response is :: "+response.asString());
		JSONObject data= (JSONObject) new JSONParser().parse(response.asString());

		byte[] decryptedPacket = CryptoUtil.decodeBase64(data.get("data").toString());
		outstream = new ByteArrayInputStream(decryptedPacket); 
		logger.info("Outstream is "+ outstream);
		FileOutputStream fos= new FileOutputStream(destinationPath);
		fos.write(decryptedPacket);
		fos.close();
		outstream.close();
		 ZipFile zipFile = new ZipFile(destinationPath);
		 zipFile.extractAll(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
         File extractedFile=new File(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		 return extractedFile;
	}
	
	public void encryptFile(File f,String sourcePath,String destinationPath,String fileName) throws ZipException, FileNotFoundException, IOException {
	
		File folder = new File(destinationPath);
		folder.mkdirs();
		 org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath+"/"+f.getName()),new File(destinationPath+"/"+fileName+".zip"));
		  File file1=new File(destinationPath+"/"+fileName+".zip");
		  JSONObject decryptedFileBody=new JSONObject();
		  decryptedFileBody=generateCryptographicDataEncryption(file1);
		  Response response=applnMethods.postRequestToDecrypt(decryptedFileBody, encrypterURL);
		  try {

			  
			  
			  JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
			byte[] encryptedPacket = CryptoUtil.decodeBase64(data.get("data").toString());
			outstream = new ByteArrayInputStream(encryptedPacket); 
			logger.info("Outstream is "+ outstream);
			FileOutputStream fos= new FileOutputStream(destinationPath+"/"+fileName+".zip");
			fos.write(encryptedPacket);
			fos.close();
			outstream.close();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  
	}
	
	public void destroyFiles(File file) throws IOException {
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
		}
	
	public void revertPacketToValid(String filePath) throws FileNotFoundException, IOException {
		File zipFile = new File(filePath+".zip");
		JSONObject cryptographicRequest=new JSONObject();
		cryptographicRequest=generateCryptographicDataEncryption(zipFile);
		 Response response=applnMethods.postRequestToDecrypt(cryptographicRequest, encrypterURL);
		 try {
				JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
				byte[] encryptedPacket = CryptoUtil.decodeBase64(data.get("data").toString());
				outstream = new ByteArrayInputStream(encryptedPacket); 
				logger.info("Outstream is "+ outstream);
				FileOutputStream fos= new FileOutputStream(filePath+".zip");
				fos.write(encryptedPacket);
				fos.close();
				outstream.close();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public JSONObject generateCryptographicData(File file) {
		JSONObject cryptographicRequest=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket=null;
		DecrypterDto decrypterDto=new DecrypterDto();
		String centerId=file.getName().substring(0,5);
		try {
			encryptedPacket=new FileInputStream(file);
			byte [] fileInBytes=FileUtils.readFileToByteArray(file);
			//String encryptedPacketString= Base64.getEncoder().encodeToString(fileInBytes);
			String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			encryptedPacketString=encryptedPacketString.replaceAll("\\s+","");
			String registrationId=file.getName().substring(0,file.getName().lastIndexOf('.'));
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			encryptedPacket.close();
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6)+milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(centerId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			cryptographicRequest.put("referenceId", centerId);
			cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cryptographicRequest;
	}
	
	public JSONObject generateCryptographicDataEncryption(File file) {
		JSONObject cryptographicRequest=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket=null;
		DecrypterDto decrypterDto=new DecrypterDto();
		String centerId=file.getName().substring(0,5);
		try {
			encryptedPacket=new FileInputStream(file);
			byte [] fileInBytes=FileUtils.readFileToByteArray(file);
			String encryptedPacketString= Base64.getEncoder().encodeToString(fileInBytes);
			//String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			encryptedPacketString=encryptedPacketString.replaceAll("\\s+","");
			String registrationId=file.getName().substring(0,file.getName().lastIndexOf('.'));
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			encryptedPacket.close();
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6)+milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(centerId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			cryptographicRequest.put("referenceId", centerId);
			cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cryptographicRequest;
	}
}
