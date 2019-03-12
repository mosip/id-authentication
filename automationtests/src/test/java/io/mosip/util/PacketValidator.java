package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;

public class PacketValidator {
	RegProcTransactionDb packetTransaction=new RegProcTransactionDb();
	final String configPath= "src/test/resources/regProc/Stagevalidation";
	final String url = "/registrationstatus/v0.1/registration-processor/registration-status/decryptPacket";
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	static Response actualResponse = null;
	private static final String ACCESS_KEY = "P9OJLQY2WS4GZLOEF8LH";
	private static final String SECRET_KEY = "jAx8v9XejubN42Twe0ooBakXd1ihM2BvTiOMiC2M";
	
	public void packetValidatorStage(String testcaseName)throws AmazonServiceException, SdkClientException, IOException, ParseException{

			// read file names from filesystem for a reg id
			AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			ClientConfiguration clientConfig = new ClientConfiguration();
			String files = null;
			List<String> filesInFileSystem = new ArrayList<>();
			clientConfig.setProtocol(Protocol.HTTP);
			@SuppressWarnings("deprecation")
			AmazonS3 conn = new AmazonS3Client(credentials, clientConfig);
			conn.setEndpoint("http://104.211.219.29:7480");

			ObjectListing objects = conn.listObjects(getRegID(testcaseName));	
			for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
				files = objectSummary.getKey();
				filesInFileSystem.add(files);
				System.out.println("files: "+files.toString());
				/*for(String file : files)
					System.out.println("file : "+file);	*/
			}
			//		List<String> filesInFileSystem = Arrays.asList(files.split("\\r?\\n"));
			System.out.println("filesInFileSystem : "+filesInFileSystem);

			// read .zip file from the folder
			File file = new File(configPath + "/" + testcaseName);
			File[] listOfFile = file.listFiles();
			File zipFile = null;
			for (File f : listOfFile) {
				if(f.getName().contains(".zip")){
					zipFile = f;
				}
			}
			System.out.println("file .zip: "+zipFile.getName());

			//using decrypt api to extract files present inside the packet


			/*actualResponse = applicationLibrary.putMultipartFile(zipFile, url);
			System.out.println("Response================ : "+actualResponse.asString());*/


			File dummyDecryptFile = new File("src/test/resources/regProc/Stagevalidation/DummyDecryptedPacket/10031100110025720190228141424");
			File[] listOfFiles = dummyDecryptFile.listFiles();
			//		List<String> filesPresent = new ArrayList<>();
			List<String> docListInPacketInfo = new ArrayList<String>();
			for(File f : listOfFiles){
				//String name =f.getName().replace("%5", "\\").toUpperCase().substring(0, (f.getName()).length()-5);
				//filesPresent.add(f.getName());
				if(f.getName().contains("packet_meta_info.json")){
					JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					System.out.println("objectData....... : "+objectData);
					JSONObject identity = (JSONObject) objectData.get("identity");
					System.out.println("identity....... : "+identity);
					JSONArray hashSequence1 = (JSONArray) identity.get("hashSequence1");
					System.out.println("hashSequence1....... : "+hashSequence1);
					for(Object obj : hashSequence1){
						JSONObject value = (JSONObject) obj;
						System.out.println("obj.........."+value.get("value"));
						String docs = value.get("value").toString();
						//docs = docs.replace("[]","").replace("[", "").replace("]", "");
						docs=docs.replaceAll("[\\[\\]]", "");
						System.out.println("docs.........."+docs);
						if(docs.equals(",")&& !docs.isEmpty()){
							docListInPacketInfo = Arrays.asList(docs.split(","));
						}else
							docListInPacketInfo.add(docs);	
					}
					JSONArray hashSequence2 = (JSONArray) identity.get("hashSequence2");
					System.out.println("hashSequence2....... : "+hashSequence2);
					System.out.println("size : "+hashSequence2.size());
					for(Object obj : hashSequence2){
						String s = obj.toString();
						s=s.replaceAll("[\\[\\]]", "");
						JSONObject value = (JSONObject) new JSONParser().parse(s);
						System.out.println("obj.........."+value.get("value"));
						String docs = value.get("value").toString();
						//docs = docs.replace("[]","").replace("[", "").replace("]", "");
						docs=docs.replaceAll("[\\[\\]]", "");
						System.out.println("docs.........."+docs);
						if(docs.equals(",")&& !docs.isEmpty()){
							docListInPacketInfo = Arrays.asList(docs.split(","));
						}else
							docListInPacketInfo.add(docs);	
					}

					docListInPacketInfo.removeAll(Arrays.asList("",null));
					System.out.println("docs present=========: "+docListInPacketInfo);
				}
			}

			//Comparing file names 
			System.out.println(Arrays.asList(docListInPacketInfo).containsAll(Arrays.asList(filesInFileSystem))); //not yet checked

			//Extracting folders/files from the decrpyted packet
			List<String> documents = new ArrayList<>();
			List<String> listOfIDDocs = new ArrayList<>();
			File[] folders = dummyDecryptFile.listFiles();
			//System.out.println("f : "+f);
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
								JSONObject proofOfDateOfBirth = (JSONObject) identity.get("proofOfDateOfBirth");
								String pob = proofOfDateOfBirth.get("value").toString();
								listOfIDDocs.add(pob);
								JSONObject parentOrGuardianBiometrics = (JSONObject) identity.get("parentOrGuardianBiometrics");
								String poib = parentOrGuardianBiometrics.get("value").toString();
								listOfIDDocs.add(poib);
								
								System.out.println("listOfIDDocs : "+listOfIDDocs);
								
								
								
								
								
							}
						}
					}

				}else if(folders[j].isFile() && !folders[j].getName().matches("packet_meta_info.json")&& !folders[j].getName().matches("packet_data_hash.txt")
						&& !folders[j].getName().matches("packet_osi_hash.txt"))
					documents.add(folders[j].getName());
			}
			System.out.println("documents : "+documents);
			
		
			
		}





		public String getRegID(String testCaseName) {
			String reg_ID = "";
			File file = new File(configPath + "/" + testCaseName);
			File[] listOfFile = file.listFiles();
			for (File f : listOfFile) {
				reg_ID = f.getName().substring(0, f.getName().lastIndexOf('.'));
			}
			return reg_ID;
		}
		/*@Test
		public void testMethod() {
			getStatusList("ValidPacketSmoke");
			try {
				packetValidator("ValidPacketSmoke");
			} catch (SdkClientException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

		public static void main(String[] args) {
			PacketValidator m = new PacketValidator();
			try {
				try {
					m.packetValidatorStage("ValidPacketSmoke");
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (AmazonServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SdkClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
