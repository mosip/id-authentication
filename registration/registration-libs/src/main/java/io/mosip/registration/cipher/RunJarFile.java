package io.mosip.registration.cipher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RunJarFile {

	public static void main(String[] args) throws Exception {
		String filePath = "D:/registration-client/registration-client.jar";

		String encryptedFilePAth = CryptoUtil.encrypt(filePath);

		String decryptedFilePAtrh = CryptoUtil.decrypt(encryptedFilePAth);

		String decryptedFileFolder = decryptedFilePAtrh.substring(0, decryptedFilePAtrh.lastIndexOf("/"));
		System.out.println(decryptedFileFolder);
		System.out.println(decryptedFilePAtrh);
		//FileUtils.copyDirectoryToDirectory(new File("D:/reg/"), new File(decryptedFileFolder));

		ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", decryptedFilePAtrh);
		processBuilder.directory(new File(decryptedFileFolder));
		Process process = processBuilder.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String runMsg = "";
		while ((runMsg = in.readLine()) != null) {
			System.out.println(runMsg);
		}
		int status = process.waitFor();
		if (status == 0) {
			System.out.println("Registration Client stopped with the status: " + status);
			process.destroy();
			System.out.println(decryptedFileFolder);
			//FileUtils.deleteDirectory(new File(decryptedFileFolder.substring(0, decryptedFilePAtrh.lastIndexOf("/"))));
		}
	}
}
