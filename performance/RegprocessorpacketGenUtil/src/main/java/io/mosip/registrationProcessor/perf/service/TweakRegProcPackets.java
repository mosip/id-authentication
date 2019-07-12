package io.mosip.registrationProcessor.perf.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.regPacket.dto.Identity;
import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.perf.util.EncrypterDecrypter;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import net.lingala.zip4j.exception.ZipException;

public class TweakRegProcPackets {

	private PacketDemoDataUtil packetDataUtil;;

	private EncrypterDecrypter encryptDecrypt;

	private File decryptedPacket;

	private String regId;

	String tempDecryptedpacketContentPath;

	private String newPacketTempPath;

	private String checksumStr;

	private String encryptedTempFile;

	public TweakRegProcPackets() {
		encryptDecrypt = new EncrypterDecrypter();
		packetDataUtil = new PacketDemoDataUtil();
	}

	public void decryptPacket(String folderhavingEncryptedpacket) {

		// String folderhavingEncryptedpacket = validPacketForPacketGeneration;
		File filePath = new File(folderhavingEncryptedpacket);
		File[] listOfFiles = filePath.listFiles();

		for (File file : listOfFiles) {
			String fileName = file.getName();
			System.out.println(file.getName());
			if (fileName.contains(".zip")) {
				String centerId = fileName.substring(0, 5);
				String machineId = fileName.substring(5, 10);
				writepacketDetailsToPropertyFile(centerId, machineId);
				JSONObject requestBody = encryptDecrypt.generateCryptographicData(file);
				try {
					decryptedPacket = encryptDecrypt.decryptFile(requestBody, folderhavingEncryptedpacket, fileName);
					System.out.println("decryptedPacket " + decryptedPacket);
				} catch (IOException | ZipException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void writePropertiesToPropertyFile(String propertyFile, Map<String, String> properties) {
		try (OutputStream output = new FileOutputStream(propertyFile);) {
			Properties prop = new Properties();
			Set<Entry<String, String>> entries = properties.entrySet();
			Iterator<Entry<String, String>> iterator = entries.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	private void writepacketDetailsToPropertyFile(String centerId, String machineId) {

		// String propertiespath = System.getProperty("user.dir") +
		// "/src/configProperties/packetInfo.properties";
		String propertiespath = System.getProperty("user.dir") + "/packetInfo.properties";
		try (OutputStream output = new FileOutputStream(propertiespath);) {
			Properties prop = new Properties();
			prop.setProperty("centerId", centerId);
			prop.setProperty("machineId", machineId);
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		}

	}

	public void copyDecryptedpacketToNewLocation(String newPacketFolderPath) {
		// copy decrypted packets from decryptedPacket to newPacketFolderPath
		// String temp = newPacketFolderPath +"/"
		String decryptedpacketPath = decryptedPacket.getAbsolutePath();
		String[] pathComponents = decryptedpacketPath.split("\\\\");
		for (String s : pathComponents) {
			System.out.println(s);
		}
		String folderName = pathComponents[pathComponents.length - 1];
		// String propertiespath = System.getProperty("user.dir") +
		// "/src/configProperties/packetFolderName.properties";
		String propertiespath = System.getProperty("user.dir") + "/packetFolderName.properties";
		Map<String, String> propertiesmap = new HashMap<>();
		propertiesmap.put("folderName", folderName);
		writePropertiesToPropertyFile(propertiespath, propertiesmap);
		System.out.println(folderName);
		File newPath = new File(newPacketFolderPath + "/" + folderName);
		newPath.mkdirs();
		System.out.println(decryptedpacketPath);

		try {
			FileUtils.copyDirectory(decryptedPacket, newPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void modifyDemodataOfDecryptedPacket(String newPacketFolderPath, RegDataCSVDto regData) {
		Gson gson = new Gson();
		// generate new reg id
		System.out.println("processDecryptedPacket");
		Properties properties = new Properties();
		String propertiespath = System.getProperty("user.dir") + "/src/configProperties/packetInfo.properties";
		try {
			FileReader reader = new FileReader(new File(propertiespath));
			properties.load(reader);
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String centerId = properties.getProperty("centerId");
		String machineId = properties.getProperty("machineId");
		regId = packetDataUtil.generateRegId(centerId, machineId);
		String tempPath = newPacketFolderPath + "/temp";
		new File("tempPath").mkdirs();
		tempDecryptedpacketContentPath = tempPath + "/" + regId;
		File tempPacketFile = new File(tempDecryptedpacketContentPath);

		System.out.println(tempPacketFile.getAbsolutePath());
		tempPacketFile.mkdirs();
		String originalRegId = "";
		propertiespath = System.getProperty("user.dir") + "/src/configProperties/packetFolderName.properties";
		try {
			FileReader reader = new FileReader(new File(propertiespath));
			properties.load(reader);
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		originalRegId = properties.getProperty("folderName");
		File srcDir = new File(newPacketFolderPath + "/" + originalRegId);
		File packetDir = new File(tempDecryptedpacketContentPath);
		try {
			FileUtils.copyDirectory(srcDir, packetDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Modify JSON data and write to ID.json inside the Demographic directory
		RegProcIdDto updatedPacketDemoDto = packetDataUtil.modifyDemographicdata(regData);
		String demoJsonPath = tempDecryptedpacketContentPath + "/Demographic/ID.json";
		JSONUtil.writeJsonToFile(gson.toJson(updatedPacketDemoDto), demoJsonPath);
		System.out.println(demoJsonPath);

		String packetMetaInfoFile = tempDecryptedpacketContentPath + "/packet_meta_info.json";
		packetDataUtil.modifyPacketMetaInfo(packetMetaInfoFile,  regId,  centerId,machineId);

		try {
			byte[] checkSum = encryptDecrypt.generateCheckSum(packetDir.listFiles());
			checksumStr = new String(checkSum, StandardCharsets.UTF_8);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// packetDataUtil.logRegIdCheckSumToFile(regId, checksumStr);

		try {
			packetDataUtil.writeChecksumToFile(tempDecryptedpacketContentPath + "/packet_data_hash.txt", checksumStr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			encryptedTempFile = encryptDecrypt.encryptFile(packetDir, tempPath, newPacketFolderPath + "/zipped", regId);
		} catch (ZipException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void copyEncryptedpacketToDestination(String destDir) {

		File sourceFile = new File(encryptedTempFile);
		if (!sourceFile.exists()) {
			System.out.println(sourceFile.getAbsolutePath() + " does not exists");
			return;
		}
		String name = sourceFile.getName();
		File targetFile = new File(destDir + name);
		try {
			FileUtils.copyFile(sourceFile, targetFile);
			System.out.println("Copying file : " + sourceFile.getAbsolutePath() + " to (" + destDir + name + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void logRegIdCheckSumToFile(String packetGenPath, String logFilePath) {
		String generatedPacket = packetGenPath + regId + ".zip";
		File f = new File(generatedPacket);
		if (f.exists() && f.isFile()) {
			long sizeInBytes = f.length();
			String center_machine_refID = regId.substring(0, 5) + "_" + regId.substring(5, 10);
			packetDataUtil.logRegIdCheckSumToFile(logFilePath, regId, checksumStr, sizeInBytes, center_machine_refID);
			new SyncRequestCreater().createSyncRequest(regId, checksumStr, sizeInBytes);
		}

	}

}
