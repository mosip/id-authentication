package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.util.List;

import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.util.CSVUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class RegPacketProcessor {

	public void processValidPacket() {
		String filePath = PropertiesUtil.TEST_DATA_CSV_FILE_PATH;
		List<RegDataCSVDto> demographicDataList = CSVUtil.loadObjectsFromCSV(filePath);
		// RegDataCSVDto regdata = demographicDataList.get(1);
		for (RegDataCSVDto regdata : demographicDataList)
			try {
				processAPacketToGenerateNewpacket(regdata);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void processAPacketToGenerateNewpacket(RegDataCSVDto regData) {

//		Properties folderPath = new Properties();
//		try {
//			FileReader reader = new FileReader(
//					new File(System.getProperty("user.dir") + "/src/configProperties/folderPaths.properties"));
//			folderPath.load(reader);
//			reader.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		String newPacketFolderPath = folderPath.getProperty("newPacketFolderPath");
//		String validPacketForPacketGeneration = folderPath.getProperty("validPacketForPacketGeneration");
//		String checksumslogFilePath = folderPath.getProperty("checksumslogFilePath");
		String newPacketFolderPath = PropertiesUtil.NEW_PACKET_FOLDER_PATH;
		String validPacketForPacketGeneration = PropertiesUtil.VALID_PACKET_PATH_FOR_PACKET_GENERATION;
		String checksumslogFilePath = PropertiesUtil.CHECKSUM_LOGFILE_PATH;

		TweakRegProcPackets packetCreater = new TweakRegProcPackets();
		/*
		 * Decrypt a valid encrypted packet
		 */
		packetCreater.decryptPacket(validPacketForPacketGeneration);
		/*
		 * decrypted packet will be present at decryptedPacket path
		 */
		packetCreater.copyDecryptedpacketToNewLocation(newPacketFolderPath);
		/*
		 * Modify the content of ID.json file present in the decrypted packet
		 */
		packetCreater.modifyDemodataOfDecryptedPacket(newPacketFolderPath, regData);

		// Copy encrypted packet to a new directory
		String packetGenPath = newPacketFolderPath + "Generated" + File.separator;
		new File(packetGenPath).mkdirs();
		packetCreater.copyEncryptedpacketToDestination(packetGenPath);

		/*
		 * Log the checksum for registration ID to a file, this file will be lated used
		 * to create request data for packet sync API
		 */
		packetCreater.logRegIdCheckSumToFile(packetGenPath,checksumslogFilePath);
	}

}
