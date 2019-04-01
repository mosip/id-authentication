package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mosip.dbdto.DecrypterDto;
import net.lingala.zip4j.exception.ZipException;

/**
 * 
 * @author M1047227
 *
 */
public class TweakRegProcPackets {
	private static Logger logger = Logger.getLogger(TweakRegProcPackets.class);

	static String filesToBeDestroyed = null;
	String centerId = "";
	String machineId = "";
	String packetName = "";
	EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
	DecrypterDto decrypterDto = new DecrypterDto();
	PacketValidator validate = new PacketValidator();

	/**
	 * 
	 * @param testCaseName
	 * @param parameterToBeChanged
	 * @param parameterValue
	 * @throws IOException
	 * @throws ZipException
	 * @throws              java.text.ParseException
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
				JSONObject jsonObject = encryptDecrypt.generateCryptographicData(f);

				decryptedFile = encryptDecrypt.decryptFile(jsonObject, configPath, f.getName());
				filesToBeDestroyed = configPath;
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
			}
		}
		logger.info("packetName :: ======================" + packetName);
		encryptDecrypt.encryptFile(decryptedFile, configPath, invalidPacketsPath, packetName);
		// encryptDecrypt.revertPacketToValid(filesToBeDestroyed);
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
	 * @throws                       java.text.ParseException
	 */
	public void invalidPacketGenerator(String propertyFiles) throws FileNotFoundException, IOException, ZipException,
			InterruptedException, java.text.ParseException, ParseException {
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

		encryptDecrypt.destroyFiles(filesToBeDestroyed);
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

	public void generateInvalidPacketForPacketValidator(String regId, String testCaseName) {
		File decryptedPacket = null;
		String configPath = System.getProperty("user.dir") + "/" + "src/test/resources/regProc/Packets/ValidPackets/";
		File file = new File(configPath);
		File[] listOfFiles = file.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains(".zip")) {
				JSONObject requestBody = encryptDecrypt.generateCryptographicData(f);
				try {
					decryptedPacket = encryptDecrypt.decryptFile(requestBody, configPath, f.getName());
					for (File info : decryptedPacket.listFiles()) {

					}
				} catch (IOException | ZipException | ParseException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void generatInvalidPacketForDemoDedupe(String testCaseName, String property) {
		JSONObject metaInfo = null;
		File decryptedPacket = null;
		JSONObject identity = null;
		String configPath = System.getProperty("user.dir") + "/" + "src/test/resources/regProc/Packets/ValidPackets/";
		String invalidPacketsPath = System.getProperty("user.dir") + "/"
				+ "src/test/resources/regProc/Packets/InvalidPackets/" + testCaseName;
		filesToBeDestroyed = configPath;
		File file = new File(configPath);
		File[] listOfFiles = file.listFiles();
		for (File f : listOfFiles) {

			if (f.getName().contains(".zip")) {
				centerId = f.getName().substring(0, 5);
				machineId = f.getName().substring(5, 10);
				String regId = generateRegID(centerId, machineId);
				JSONObject requestBody = encryptDecrypt.generateCryptographicData(f);
				try {

					decryptedPacket = encryptDecrypt.decryptFile(requestBody, configPath, f.getName());

					byte[] checksum = encryptDecrypt.generateCheckSum(decryptedPacket.listFiles());

					String str = new String(checksum, StandardCharsets.UTF_8);
					logger.info("checksum is  :: " + str);
					for (File info : decryptedPacket.listFiles()) {
						if (info.getName().equals("Demographic")) {
							File[] demographic = info.listFiles();
							for (File metaFile : demographic) {
								if (metaFile.getName().equals("ID.json")) {
									FileReader metaFileReader = new FileReader(metaFile.getPath());
									metaInfo = (JSONObject) new JSONParser().parse(metaFileReader);
									metaFileReader.close();
									identity = (JSONObject) metaInfo.get("identity");
									identity.put("dateOfBirth", property);
									logger.info("Identity is :: " + identity.get("dateOfBirth"));
									try (FileWriter updatedFile = new FileWriter(metaFile.getAbsolutePath())) {
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
						}
						if (info.getName().equals("packet_data_hash.txt")) {
							PrintWriter writer = new PrintWriter(info);
							writer.print("");
							writer.print(str);
							writer.close();
						}
					}
					encryptDecrypt.encryptFile(decryptedPacket, configPath, invalidPacketsPath, regId);
				} catch (IOException | ZipException | ParseException e) {

					e.printStackTrace();
				}
			}
		}

	}

	public void demoDedupePropertyFileReader(String propertyFiles) {
		Properties prop = new Properties();
		TweakRegProcPackets e = new TweakRegProcPackets();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/" + propertyFiles;
		try {
			FileReader readFile = new FileReader(new File(propertyFilePath));
			prop.load(readFile);
			for (String property : prop.stringPropertyNames()) {
				logger.info("invalid" + property);
				logger.info(prop.getProperty(property));

				e.generatInvalidPacketForDemoDedupe(property, prop.getProperty(property));
			}
			readFile.close();
		} catch (IOException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
		try {
			encryptDecrypt.destroyFiles(filesToBeDestroyed);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args)
			throws IOException, ZipException, InterruptedException, java.text.ParseException, ParseException {
		TweakRegProcPackets e = new TweakRegProcPackets();
		e.demoDedupePropertyFileReader("IDjson.properties");
		// e.generatInvalidPacketForDemoDedupe(e.generateRegID("10011",
		// "10011"),"PotentialMatch");
	}
}