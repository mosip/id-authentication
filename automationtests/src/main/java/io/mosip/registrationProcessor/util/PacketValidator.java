package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import io.mosip.util.ConnectionUtils;
import io.mosip.util.EncrypterDecrypter;
import io.mosip.util.FileSystemAdapter;
import io.mosip.util.HDFSAdapterImpl;
import io.mosip.util.HMACUtils;
import net.lingala.zip4j.exception.ZipException;

/**
 * This class is used for packet validator stage validations
 * 
 * @author Sayeri Mishra
 *
 */
public class PacketValidator {

	RegProcApiRequests apiRequests = new RegProcApiRequests();
	private static Logger logger = Logger.getLogger(PacketValidator.class);
	//hardcorded folder name for running this class separately
	//it will be replaced after packet creation
	final String configPath= apiRequests.getResourcePath()+"regProc/StageValidation";
	final String fileName = "/DummyDecryptedPacket/10011100110002020190326090045";

	public FileSystemAdapter establishHDFSConnection(){
		/*ConnectionUtils connectionUtil = new ConnectionUtils();
		FileSystemAdapter adapter = new HDFSAdapterImpl(connectionUtil);*/
		return adapter;
	}

	final FileSystemAdapter adapter = establishHDFSConnection();

	/**
	 * This method is used for validation of packet validation stage, which
	 * contains file validation, document validation and checksum validation
	 * 
	 * @param packet
	 * @return boolean true, if packet is validates, else false
	 */
	public boolean packetValidatorStage(File packet){
		String regId = packet.getName();	

		boolean isPacketValidated = false;
		//using decrypt api to extract files present inside the packet
		File[] listOfFiles = packet.listFiles();
		List<String> docListInPacketInfo = new ArrayList<String>();
		try{
			for(File f : listOfFiles){

				// extracting file names present in hashsequence1 and hashsequence2 of
				//packet_meta_info.json
				if(f.getName().contains("packet_meta_info.json")){
					JSONObject objectData;
					objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					docListInPacketInfo = getHashSequenceFiles("hashSequence1",docListInPacketInfo, objectData);
					docListInPacketInfo = getHashSequenceFiles("hashSequence2", docListInPacketInfo, objectData);
					logger.info("refactored docList: "+docListInPacketInfo);
				}

				//Extracting folders/files from the decrpyted packet
				List<String> documents = new ArrayList<>();
				List<String> listOfIDDocs = new ArrayList<>();
				File[] folders = packet.listFiles();
				for (int j = 0; j < folders.length; j++) {
					if (folders[j].isDirectory()) {
						File[] listOfDocs = folders[j].listFiles();
						for (File d : listOfDocs){
							documents.add(folders[j].getName()+"\\"+ d.getName());	
						}

						//read ID.json for document verification
						if(folders[j].getName().matches("Demographic")){
							File[] listOfDocsInDemographics = folders[j].listFiles();
							for (File d : listOfDocsInDemographics){
								if(d.getName().matches("ID.json")){
									JSONObject idData = (JSONObject) new JSONParser().parse(new FileReader(d.getPath()));
									JSONObject identity = (JSONObject) idData.get("identity");
									JSONObject proofOfAddress = (JSONObject) identity.get("proofOfAddress");
									String poa = proofOfAddress.get("value").toString();
									listOfIDDocs.add(poa);
									JSONObject proofOfIdentity = (JSONObject) identity.get("proofOfIdentity");
									String poi = proofOfIdentity.get("value").toString();
									listOfIDDocs.add(poi);
									JSONObject proofOfRelationship = (JSONObject) identity.get("proofOfRelationship");
									String por = proofOfRelationship.get("value").toString();
									listOfIDDocs.add(por);
									/*JSONObject proofOfDateOfBirth = (JSONObject) identity.get("proofOfDateOfBirth");
									String pob = proofOfDateOfBirth.get("value").toString();
									listOfIDDocs.add(pob);*/
									String poib = null;
									if(identity.get("parentOrGuardianBiometrics")!=null){
										JSONObject individualBiometrics = (JSONObject) identity.get("parentOrGuardianBiometrics");
										poib = individualBiometrics.get("value").toString();
									}else {
										JSONObject individualBiometrics = (JSONObject) identity.get("individualBiometrics");
										poib = individualBiometrics.get("value").toString();
									}		
									listOfIDDocs.add(poib);
									logger.info("listOfIDDocs : "+listOfIDDocs);
								}
							}
						}

					}else if(folders[j].isFile() && !folders[j].getName().matches("packet_meta_info.json")&& !folders[j].getName().matches("packet_data_hash.txt")
							&& !folders[j].getName().matches("packet_osi_hash.txt"))
						documents.add(folders[j].getName());
				}


				logger.info("documents : "+documents);

				//file validation
				List<String> modifiedList = new ArrayList<>();
				//documents contains all the files present in decrypted packet
				for(String file : documents){
					if(file.contains("\\"))
						file =file.substring(file.indexOf("\\")+1);
					file = '"'+ file.substring(0,file.indexOf("."))+'"';
					modifiedList.add(file);
				}

				//comparing files present in hashsequences and files present in decrypted packet
				modifiedList.removeAll(docListInPacketInfo);
				logger.info("files not matched between documents and  docListInPacketInfo: "+modifiedList);

				int noFilesPresent = 0;
				int noOfDocuments =0;
				boolean isPresent = false;
				boolean isFileValidated = false;
				//if modifiedList is empty, files present in hashsequences and files present 
				//in decrypted packet is same
				if(modifiedList.isEmpty()){
					for(String file : documents){
						noOfDocuments=+1;
						//checking files present in HDFS
						isPresent = adapter.checkFileExistence(regId, file.substring(0,file.indexOf(".")));
						if(isPresent){
							noFilesPresent=+1;
							logger.info("file present in file system : "+file);
						}else
							logger.info("file not present in file system");						
					}	
					if(noFilesPresent==noOfDocuments)
						isFileValidated = true;
					logger.info("ALL FILES ARE MATCHING..............");	
				}

				//Document validation
				boolean isDocumentValidated = false;
				for(String file : listOfIDDocs){
					file = '"'+ file+'"';
					modifiedList.add(file);
				}
				//modifiedList = listOfIDDocs;
				modifiedList.removeAll(docListInPacketInfo);
				logger.info("files not matched between listOfIDDocs and  docListInPacketInfo : "+modifiedList);
				if(modifiedList.isEmpty()){
					isDocumentValidated = true;
				}

				//CheckSum generation
				byte[] hashCodeGenerated = checksumGeneration(listOfFiles, regId, adapter);
				//CheckSum Validation
				InputStream packetDataHashStream = adapter.getFile(regId, "PACKET_DATA_HASH");
				byte[] packetDataHashByte = IOUtils.toByteArray(packetDataHashStream);

				Boolean isChecksumValidated = Arrays.equals(hashCodeGenerated, packetDataHashByte);
				if(isChecksumValidated){
					logger.info("Checksum validated.........");
				}else
					logger.info("checksum validation failed......");

				//Packet validation
				if(isFileValidated && isDocumentValidated && isChecksumValidated){
					isPacketValidated = true ;
				}
			}
		} catch (IOException | ParseException e) {
			logger.error("Exception occurred in PacketValidator class in packetValidatorStage method "+e);
		}
		logger.info("isPacketValidated : "+isPacketValidated);
		return isPacketValidated;
	}




	/**
	 * This method is generating checksum value using file list present in hash sequence1 and regId
	 * 
	 * @param listOfFiles
	 * @param regId
	 * @return byte[] which is the hash code
	 */
	public byte[] checksumGeneration(File[] listOfFiles, String regId, FileSystemAdapter adapter){
		JSONArray hashSequence1;
		byte[] hashCodeGenerated = null;
		for(File f : listOfFiles){
			if(f.getName().contains("packet_meta_info.json")){
				JSONObject objectData;
				try {
					objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

					JSONObject identity = (JSONObject) objectData.get("identity");
					hashSequence1 = (JSONArray) identity.get("hashSequence1");
					logger.info("hashSequence1....... : "+hashSequence1);
					for(Object obj : hashSequence1){
						JSONObject label = (JSONObject) obj;
						logger.info("obj : "+label.get("label"));
						if(label.get("label").equals("applicantBiometricSequence")){
							List<String> docs = (List<String>) label.get("value");
							generateBiometricsHash(docs, regId, adapter);
						}else if(label.get("label").equals("introducerBiometricSequence")){
							List<String> docs = (List<String>) label.get("value");
							generateBiometricsHash(docs, regId, adapter);
						}else if(label.get("label").equals("applicantDemographicSequence")){
							List<String> docs = (List<String>) label.get("value");
							generateDemographicHash(docs, regId, adapter);
						}
					}	
					hashCodeGenerated = HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
				}catch (IOException | ParseException e) {
					logger.error("Exception occurred in PacketValidator class in checksumGeneration method "+e);
				}
			}	
		}
		return hashCodeGenerated;
	}

	/**
	 * This method generate demographic hash using demographic documents and regId
	 * 
	 * @param docs
	 * @param regId2
	 */
	private void generateDemographicHash(List<String> docs, String regId2, FileSystemAdapter adapter) {
		docs.forEach(document -> {
			byte[] filebyte = null;
			InputStream fileStream = adapter.getFile(regId2,
					"DEMOGRAPHIC\\" + document.toUpperCase());
			try {
				filebyte = IOUtils.toByteArray(fileStream);
				fileStream.close();
			} catch (Exception e) {
				logger.error("Exception occurred in PacketValidator class in "
						+ "generateDemographicHash method "+e);
			}
			generateHash(filebyte);
		});
	}

	/**
	 * This method generates biometric hash code using biometric documents and regId
	 * 
	 * @param docs
	 * @param regId
	 */
	private void generateBiometricsHash(List<String> docs, String regId, FileSystemAdapter adapter) {
		docs.forEach(file -> {
			byte[] filebyte = null;
			InputStream fileStream = adapter.getFile(regId,
					"BIOMETRIC\\"+ file.toUpperCase());

			try {
				filebyte = IOUtils.toByteArray(fileStream);
				fileStream.close();
			} catch (Exception e) {
				logger.error("Exception occurred in PacketValidator class in "
						+ "generateBiometricsHash method "+e);
			}
			generateHash(filebyte);
		});
	}

	/**
	 * This method generates hash code using byte[]
	 * 
	 * @param fileByte
	 */
	private void generateHash(byte[] fileByte) {
		if (fileByte != null) {
			HMACUtils.update(fileByte);
		}	
	}

	/**
	 * This method fetch all the file names present in hashsequence of packet_meta_info.json
	 * 
	 * @param hashSeqValue
	 * @param docListInPacketInfo
	 * @param objectData
	 * @return List<String> which contains file names 
	 */
	private List<String> getHashSequenceFiles(String hashSeqValue, List<String> docListInPacketInfo, JSONObject objectData) {
		JSONArray hashSequence;
		List<String> splittedList;
		JSONObject identity = (JSONObject) objectData.get("identity");
		hashSequence = (JSONArray) identity.get(hashSeqValue);
		logger.info("hashSequence....... : "+hashSequence);
		for(Object obj : hashSequence){
			JSONObject value = (JSONObject) obj;
			String docs = value.get("value").toString();
			docs=docs.replaceAll("[\\[\\]]", "");
			if(!docs.isEmpty() && docs.contains(",")){
				splittedList = Arrays.asList(docs.split(","));
				docListInPacketInfo.addAll(splittedList);
			}else if (!docs.isEmpty())
				docListInPacketInfo.add(docs);
		}
		return docListInPacketInfo;
	}

	@Test
	public void testMethod(){
		/*File dummyDecryptFile = new File(configPath+fileName);
		packetValidatorStage(dummyDecryptFile);*/
		File decrpytedFile = null;
		File invalidFiles = new File("src/test/resources/regProc/Packets/InvalidPackets/PacketValidator");
		File[] directoryFiles = invalidFiles.listFiles();
		try {
			for (File file : directoryFiles){
				if(file.isDirectory()){
					File[] zipFile = file.listFiles();
					
					for(File packet : zipFile){
						//String packetName = packet.getName();
						if(packet.getName().contains(".zip")){
							String packetName = packet.getName();
							logger.info("inside file ========= : "+packetName);
							EncrypterDecrypter decrypt = new EncrypterDecrypter();
							/*File encryptedPacket = new File(invalidFiles+"/"+file.getCanonicalFile().getName()
							+"/"+packet);
							logger.info("encryptedPacket : "+encryptedPacket.getName());*/
							JSONObject data = decrypt.generateCryptographicData(packet);
							logger.info("data : "+data);
							decrpytedFile = decrypt.decryptFile(data,invalidFiles+file.getName()+packetName, packetName);
							logger.info("decrypted file generated : "+decrpytedFile);
							packetValidatorStage(decrpytedFile);
						}
						
					}
					
				}
			}
		} catch (IOException | ZipException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}