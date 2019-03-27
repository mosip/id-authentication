package io.mosip.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.dbdto.DecrypterDto;
import net.lingala.zip4j.exception.ZipException;
/**
 * 
 * @author M1047227
 *
 */
public class TweakRegProcPackets {
	private static Logger logger = Logger.getLogger(TweakRegProcPackets.class);
	
	private String applicationId="REGISTRATION";	
	static String filesToBeDestroyed = null;
	String centerId = "";
	String machineId = "";
	String packetName="";
	EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
	DecrypterDto decrypterDto=new DecrypterDto();
/**
 * 
 * @param testCaseName
 * @param parameterToBeChanged
 * @param parameterValue
 * @throws IOException
 * @throws ZipException
 * @throws java.text.ParseException 
 */
	@SuppressWarnings("unchecked")
	public void tweakFile(String testCaseName, String parameterToBeChanged, String parameterValue)
			throws IOException, ZipException, java.text.ParseException, ParseException {
		File decryptedFile = null;
		JSONObject metaInfo = null;
		String configPath = System.getProperty("user.dir") + "/" + "src/test/resources/regProc/Packets/ValidPackets/";
		String invalidPacketsPath = System.getProperty("user.dir") + "/"
				+ "src/test/resources/regProc/Packets/InvalidPackets/" + testCaseName;
		File file = new File(configPath);
		File[] listOfFiles = file.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains(".zip")) {
				JSONObject jsonObject=encryptDecrypt.generateCryptographicData(f);
			
				decryptedFile = encryptDecrypt.decryptFile(jsonObject, f.getPath());
				filesToBeDestroyed = configPath + "/" + decryptedFile.getName();
				File[] packetFiles = decryptedFile.listFiles();
				for (File info : packetFiles) {

					if (info.getName().toLowerCase().equals("packet_meta_info.json")) {
						try {
							FileReader metaFileReader = new FileReader(info.getPath());
							metaInfo = (JSONObject) new JSONParser().parse(metaFileReader);
							metaFileReader.close();
						} catch (IOException | ParseException e) {
							e.printStackTrace();
						}
						JSONObject identity = (JSONObject) metaInfo.get("identity");
						JSONArray metaData = (JSONArray) identity.get("metaData");
						JSONArray updatedData = tweakFile(metaData, parameterToBeChanged, parameterValue);
						metaInfo.put("identity", identity);
						try (FileWriter updatedFile = new FileWriter(info.getAbsolutePath())) {
							try {
								updatedFile.write(metaInfo.toString());
								updatedFile.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							logger.info("Successfully updated json object to file...!!");

						} catch (IOException e1) {

							e1.printStackTrace();
						}
					}

				}
			}else {
				continue;
			}
		}
		logger.info("packetName :: ======================" + packetName);
		encryptDecrypt.encryptFile(decryptedFile, configPath, invalidPacketsPath, packetName);
		encryptDecrypt.revertPacketToValid(filesToBeDestroyed);
	}
/**
 * 
 * @param metaData
 * @param parameter
 * @param value
 * @return
 */
	public JSONArray tweakFile(JSONArray metaData, String parameter, String value) {
		for (int i = 0; i < metaData.size(); i++) {
			JSONObject labels = (JSONObject) metaData.get(i);
			if (labels.get("label").equals("centerId")) {
				centerId = labels.get("value").toString();
				logger.info("centreId :: " + centerId);
			} else if (labels.get("label").equals("machineId")) {
				machineId = labels.get("value").toString();
				logger.info("machineId :: " + machineId);
			}
			if (labels.get("label").equals(parameter)) {
				labels.put("value", value);
			}
		}
		for (int i = 0; i < metaData.size(); i++) {
			JSONObject labels = (JSONObject) metaData.get(i);
			if (labels.get("label").equals("registrationId")) {
				packetName = generateRegID(centerId, machineId);
				labels.put("value", packetName);
			}
		}
		logger.info("Meta Data Updated Is  ::  ===================" + metaData);
		return metaData;
	}
/**
 * 
 * @param propertyFiles
 * @throws FileNotFoundException
 * @throws IOException
 * @throws ZipException
 * @throws InterruptedException
 * @throws java.text.ParseException 
 */
	public void invalidPacketGenerator(String propertyFiles)
			throws FileNotFoundException, IOException, ZipException, InterruptedException, java.text.ParseException, ParseException {
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		TweakRegProcPackets e = new TweakRegProcPackets();
		Properties prop = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/" + propertyFiles;
		prop.load(new FileReader(new File(propertyFilePath)));
		for (String property : prop.stringPropertyNames()) {
			logger.info("invalid" + property);
			logger.info(prop.getProperty(property));
			e.tweakFile("invalid" + property, property, prop.getProperty(property));
		}
		File decryptedPacket = new File(filesToBeDestroyed);
		encryptDecrypt.destroyFiles(decryptedPacket);
	}
/**
 * 
 * @param centerId
 * @param machineId
 * @return
 */
	public String generateRegID(String centerId, String machineId) {
		String regID = "";
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		timeStamp.replaceAll(".", "");
		int n = 10000 + new Random().nextInt(90000);
		String randomNumber = String.valueOf(n);
		regID = centerId + machineId + randomNumber + timeStamp;
		return regID;
	}

	public static void main(String[] args) throws IOException, ZipException, InterruptedException, java.text.ParseException, ParseException {
		TweakRegProcPackets e = new TweakRegProcPackets();
		e.invalidPacketGenerator("packetProperties.properties");
	}
}