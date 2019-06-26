package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.dbdto.CryptomanagerDto;
import io.mosip.dbdto.CryptomanagerRequestDto;
import io.mosip.dbdto.DecrypterDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
/**
 * 
 * @author M1047227
 *
 */

public class EncrypterDecrypter extends BaseTestCase {
	private static Logger logger = Logger.getLogger(EncrypterDecrypter.class);
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	private final String decrypterURL="/v1/cryptomanager/decrypt";
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	private String applicationId="REGISTRATION";	
	InputStream outstream = null;
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	String validToken="";
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	public void generateHash(byte[] fileByte) {
		if (fileByte != null) {
			HMACUtils.update(fileByte);
		}	
	}
	
	/**
	 * 
	 * @param file
	 * @param destinationPath
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 * @throws ParseException 
	 */
	public File decryptFile(JSONObject decryptDto,String destinationPath,String fileName) throws IOException, ZipException, ParseException {
		logger.info(destinationPath);
		destinationPath=destinationPath+"//TemporaryValidPackets";
		File folder=new File(destinationPath);
		folder.mkdirs();
		destinationPath=destinationPath+"//"+fileName;
		//Response response=applnMethods.postRequestToDecrypt(decryptDto, decrypterURL);
		validToken = getToken("syncTokenGenerationFilePath");
		boolean tokenStatus=apiRequests.validateToken(validToken);
		while(!tokenStatus) {
			validToken = getToken("syncTokenGenerationFilePath");
			tokenStatus=apiRequests.validateToken(validToken);
		}
		Response response = apiRequests.postRequestToDecrypt(decrypterURL, decryptDto,MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, validToken);
		JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
		JSONObject responseObject=(JSONObject) data.get("response");
		byte[] decryptedPacket = CryptoUtil.decodeBase64(responseObject.get("data").toString());
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
	public File extractFromDecryptedPacket(String destinationPath,String fileName) {
		String temporaryPath=destinationPath+"//"+fileName;
		destinationPath=destinationPath+"//TemporaryValidPackets";
		File folder=new File(destinationPath);
		folder.mkdirs();
		destinationPath=destinationPath+"//"+fileName;
		try {
			FileUtils.copyFile(new File(temporaryPath), new File(destinationPath));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 ZipFile zipFile=null;
		try {
			zipFile = new ZipFile(destinationPath);
			logger.info("Path : "+destinationPath);
			logger.info("zip : "+zipFile.isValidZipFile());
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			
			zipFile.extractAll(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         File extractedFile=new File(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		 return extractedFile;
	} 
	public void encryptFile(File f,String sourcePath,String destinationPath,String fileName) throws ZipException, FileNotFoundException, IOException {
		validToken = getToken("syncTokenGenerationFilePath");
		boolean tokenStatus=apiRequests.validateToken(validToken);
		while(!tokenStatus) {
			validToken = getToken("syncTokenGenerationFilePath");
			tokenStatus=apiRequests.validateToken(validToken);
		}
		sourcePath=sourcePath+"//TemporaryValidPackets";
		File folder = new File(destinationPath);
		folder.mkdirs();
		 org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath+"/"+f.getName()),new File(destinationPath+"/"+fileName+".zip"));
		  File file1=new File(destinationPath+"/"+fileName+".zip");
		  JSONObject decryptedFileBody=new JSONObject();
		  decryptedFileBody=generateCryptographicDataEncryption(file1);
		  logger.info("encrypt request packet  : "+decryptedFileBody);
		  Response response=apiRequests.postRequestToDecrypt(encrypterURL, decryptedFileBody, MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,validToken);
		  
		  try {
			  JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
			  JSONObject responseObject=(JSONObject) data.get("response");
			//  String encryptedPacketString= CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
			byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
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
	
	public void encryptFile(File f,String sourcePath,String destinationPath,String fileName, String token) throws ZipException, FileNotFoundException, IOException {
		sourcePath=sourcePath+"//TemporaryValidPackets";
		File folder = new File(destinationPath);
		folder.mkdirs();
		 org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath+"/"+f.getName()),new File(destinationPath+"/"+fileName+".zip"));
		  File file1=new File(destinationPath+"/"+fileName+".zip");
		  JSONObject decryptedFileBody=new JSONObject();
		  decryptedFileBody=generateCryptographicDataEncryption(file1);
		  logger.info("encrypt request packet  : "+decryptedFileBody);
		  
		//  String validtoken = getToken("syncTokenGenerationFilePath");
		  boolean tokenStatus=apiRequests.validateToken(validToken);
			while(!tokenStatus) {
				validToken = getToken("syncTokenGenerationFilePath");
				tokenStatus=apiRequests.validateToken(validToken);
			}
		  Response response=apiRequests.postRequestToDecrypt(encrypterURL, decryptedFileBody, MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,validToken);
		  try {
			  JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
			  JSONObject responseObject=(JSONObject) data.get("response");
			//  String encryptedPacketString= CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
			byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
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
	
	public void destroyFiles(String filePath) throws IOException {
		logger.info("Destroying Files");
		filePath=filePath+"//TemporaryValidPackets";
		File file=new File(filePath);
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
	
/*	public void revertPacketToValid(String filePath) throws FileNotFoundException, IOException {
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
	}*/
	@SuppressWarnings("unchecked")
	public JSONObject generateCryptographicData(File file) {
		JSONObject cryptographicRequest=new JSONObject();
		JSONObject decryptionRequest=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket=null;
		DecrypterDto decrypterDto=new DecrypterDto();
		CryptomanagerRequestDto cryptoRequest=new CryptomanagerRequestDto();
		CryptomanagerDto request=new CryptomanagerDto();
		String centerId=file.getName().substring(0,5);
		String machineId=file.getName().substring(5,10);
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
			Date currentDate=new Date();
			LocalDateTime requestTime=LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
			
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(centerId+"_"+machineId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			request.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			cryptographicRequest.put("referenceId", centerId+"_"+machineId);
			cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			decryptionRequest.put("id","");
			decryptionRequest.put("metadata","");
			decryptionRequest.put("request",cryptographicRequest);
			decryptionRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			decryptionRequest.put("version","");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.info("Could Not ");
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptionRequest;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject generateCryptographicDataEncryption(File file) {
		JSONObject encryptRequest=new JSONObject();
		CryptomanagerDto request=new CryptomanagerDto();
		JSONObject cryptographicRequest=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket=null;
		DecrypterDto decrypterDto=new DecrypterDto();
		//String centerId=file.getName().substring(0,5);
		String refId = file.getName().substring(0, 5)+"_"+file.getName().substring(5, 10);
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
			Date currentDate=new Date();
			LocalDateTime requestTime=LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(refId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			request.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			System.out.println("encrypter request data : "+encryptedPacketString);
			cryptographicRequest.put("referenceId", refId);
			cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("id","");
			encryptRequest.put("metadata","");
			encryptRequest.put("request",cryptographicRequest);
			encryptRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("version","");
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
		return encryptRequest;
	}
	public byte[] generateCheckSum(File[] listOfFiles) throws FileNotFoundException, IOException, ParseException {
		JSONArray hashSequence1;
		byte[] hashCodeGenerated = null;
		for(File f : listOfFiles){
			if(f.getName().contains("packet_meta_info.json")){
				FileReader metaFileReader = new FileReader(f.getPath());
				JSONObject objectData = (JSONObject) new JSONParser().parse(metaFileReader);
				JSONObject identity = (JSONObject) objectData.get("identity");
				metaFileReader.close();
				hashSequence1 = (JSONArray) identity.get("hashSequence1");
				logger.info("hashSequence1....... : "+hashSequence1);
				for(Object obj : hashSequence1){
					JSONObject label = (JSONObject) obj;
					logger.info("obj : "+label.get("label"));
					if(label.get("label").equals("applicantBiometricSequence")){
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: "+ docs);
						generateBiometricsHash(docs,listOfFiles);
					}else if(label.get("label").equals("introducerBiometricSequence")){
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: "+ docs);
						generateBiometricsHash(docs,listOfFiles);
					}else if(label.get("label").equals("applicantDemographicSequence")){
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: "+ docs);
						generateDemographicsHash(docs,listOfFiles);
					}
				}	
				hashCodeGenerated = HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
			}
		}
		return hashCodeGenerated;
	}
	private void generateBiometricsHash(List<String> docs,File[] listOfFiles ) {
		byte[] fileByte=null;
	for(File file:listOfFiles) {
		if(file.getName().equalsIgnoreCase("Biometric")) {
			File [] demographicFiles=file.listFiles();
			for(File demoFiles: demographicFiles) {
				for(String fileName: docs) {
					if(fileName.equals(demoFiles.getName().substring(0,demoFiles.getName().lastIndexOf('.')))) {
				try {
					FileInputStream inputStream= new FileInputStream(demoFiles);
					fileByte=IOUtils.toByteArray(inputStream);
					generateHash(fileByte);
					inputStream.close();
				} catch ( IOException e) {
					e.printStackTrace();
				}
				}
			}
			}
		}
	}
	}
	
	private void generateDemographicsHash(List<String> docs,File[] listOfFiles) {
		byte[] fileByte=null;
		for(File file:listOfFiles) {
			if(file.getName().equalsIgnoreCase("Demographic")) {
				File [] demographicFiles=file.listFiles();
				for(File demoFiles: demographicFiles) {
					for(String fileName: docs) {
						if(fileName.equals(demoFiles.getName().substring(0,demoFiles.getName().lastIndexOf('.')))) {
					try {
						FileInputStream inputStream= new FileInputStream(demoFiles);
						fileByte=IOUtils.toByteArray(inputStream);
						generateHash(fileByte);
						inputStream.close();
					} catch ( IOException e) {
						e.printStackTrace();
					}
					}
				}
				}
			}
		}
	}
		/*@SuppressWarnings("unchecked")
		public JSONObject generateCryptographicDataEncryption(JSONObject requestJson) throws IOException {
			JSONObject encryptRequest=new JSONObject();
			CryptomanagerDto request=new CryptomanagerDto();
			JSONObject cryptographicRequest=new JSONObject();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
			InputStream encryptedPacket=null;
			DecrypterDto decrypterDto=new DecrypterDto();
			String registrationId = null;
			
			try {
				//encryptedPacket=new FileInputStream(file);
				byte [] fileInBytes= requestJson.toString().getBytes();
				String encryptedPacketString= Base64.getEncoder().encodeToString(fileInBytes);

				logger.info("encryptedPacketString : "+encryptedPacketString);
				JSONArray requestData = (JSONArray) requestJson.get("request");
				JSONObject obj = (JSONObject) requestData.get(0);
				registrationId = obj.get("registrationId").toString();
				
				String refId=registrationId.substring(0,5)+"_"+registrationId.substring(5,10);
				String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
				int n = 100 + new Random().nextInt(900);
				String milliseconds = String.valueOf(n);
				//encryptedPacket.close();
				Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6)+milliseconds);
				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				Date currentDate=new Date();
				LocalDateTime requestTime=LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
				decrypterDto.setApplicationId(applicationId);
				decrypterDto.setReferenceId(refId);
				decrypterDto.setData(encryptedPacketString);
				decrypterDto.setTimeStamp(ldt);
				request.setRequesttime(requestTime);
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				cryptographicRequest.put("applicationId", applicationId);
				cryptographicRequest.put("data", "ew0KCSJpZCI6ICJtb3NpcC5yZWdpc3RyYXRpb24uc3luYyIsDQoJInJlcXVlc3R0aW1lIjogIjIwMTktMDMtMDJUMDY6Mjk6NDEuMDExWiIsDQoJInZlcnNpb24iOiAiMS4wIiwNCgkicmVxdWVzdCI6IFt7DQoJCSJsYW5nQ29kZSI6ICJlbmciLA0KCQkicmVnaXN0cmF0aW9uSWQiOiAiMTAwMTExMDAxMTAwMDE5MjAxOTAzMjUxMjAzMTAiLA0KCQkicmVnaXN0cmF0aW9uVHlwZSI6ICJORVciLA0KCQkicGFja2V0SGFzaFZhbHVlIjogIkQ3Qzg3REM1RDNBNzU5RDc3NDMzQjAyQjgwNDM1Q0ZBQjUwODdGMUE5NDI1NDNGNTFBNTA3NUJDNDQxQkY3RUIiLA0KCQkicGFja2V0U2l6ZSI6IDUyNDI4ODAsDQoJCSJzdXBlcnZpc29yU3RhdHVzIjogIkFQUFJPVkVEIiwNCgkJInN1cGVydmlzb3JDb21tZW50IjogIkFwcHJvdmVkLCBhbGwgZ29vZCIsDQoJCSJvcHRpb25hbFZhbHVlcyI6IFt7DQoJCQkia2V5IjogIkNOSUUiLA0KCQkJInZhbHVlIjogIjEyMjIyMzQ1NiINCgkJfV0NCgl9XQ0KfQ==");
				cryptographicRequest.put("referenceId", refId);
				//cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
				cryptographicRequest.put("timeStamp",request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
				encryptRequest.put("id","");
				encryptRequest.put("metadata","");
				encryptRequest.put("request",cryptographicRequest);
				encryptRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
				encryptRequest.put("version","1.0");
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return encryptRequest;
		}*/
		
		/*public Map<String,Object> encryptJson(JSONObject  requestJson) throws IOException {
			String encryptedPacket =null;
			String responseTime = null;
			Map<String,Object> demo = new HashMap<>();
			logger.info("requestJson : "+requestJson);
			  JSONObject decryptedFileBody=new JSONObject();
			  decryptedFileBody=generateCryptographicDataEncryption(requestJson);
			  Response response=apiRequests.postRequestToDecrypt(encrypterURL, decryptedFileBody, MediaType.APPLICATION_JSON,
					  MediaType.APPLICATION_JSON, validToken);
			  try {
				  JSONObject data= (JSONObject) new JSONParser().parse(response.asString());
				  JSONObject responseObject=(JSONObject) data.get("response");
				//  String encryptedPacketString= CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
				encryptedPacket = responseObject.get("data").toString();
				responseTime = data.get("responsetime").toString();
				logger.info("RESPONSE TIME : "+responseTime);
				
				demo.put("data", encryptedPacket);
				demo.put("responsetime", responseTime);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return demo;
			  
		}*/
	
}
