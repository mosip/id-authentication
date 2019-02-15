/**
 * 
 */
package io.mosip.kernel.fsadapter.hdfs;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class HDFSDemoApplication {

	@Autowired
	private FileSystemAdapter hdfsAdapterImpl;

	public static void main(String[] args) {
		SpringApplication.run(HDFSDemoApplication.class, args);
	}

	@PostConstruct
	public void demo() throws IOException {
		System.out.println(hdfsAdapterImpl);
//
//		hdfsAdapterImpl.storeFile("2018782130000120112018104200", "Biometric/Applicant/BothThumbs",
//				FileUtils.openInputStream(new File(
//						"D:/hdfstest/testfolder/2018782130000120112018104200/Biometric/Applicant/BothThumbs.jpg")));
//
//		hdfsAdapterImpl.storePacket("91001984930000120190110193915",
//				FileUtils.openInputStream(new File("D:/hdfstest/testfolder/91001984930000120190110193915.zip")));
//
//		hdfsAdapterImpl.storePacket("10101010101", new File("D:/hdfstest/testfolder/10101010101.zip"));
//
//		hdfsAdapterImpl.unpackPacket("91001984930000120190110193915");
//		FileUtils.copyInputStreamToFile(
//				hdfsAdapterImpl.getFile("91001984930000120190110193915", "DEMOGRAPHIC/APPLICANTPHOTO"),
//				new File("D:/hdfstest/testfolder2/file.jpg"));
//		FileUtils.copyInputStreamToFile(hdfsAdapterImpl.getPacket("91001984930000120190110193915"),
//				new File("D:/hdfstest/testfolder2/packetid010101.zip"));
//
//		System.out.println(hdfsAdapterImpl.isPacketPresent("91001984930000120190110193915"));
//
//		System.out.println(
//				hdfsAdapterImpl.checkFileExistence("91001984930000120190110193915", "DEMOGRAPHIC/POR_LICENCE"));
//
//		System.out.println(hdfsAdapterImpl.deletePacket("10101010101"));
//
//		System.out.println(hdfsAdapterImpl.deleteFile("10101010101", "DEMO/NESTED_DEMO/NESTED-DEMO_FILE"));
//
//		System.out.println(hdfsAdapterImpl.copyFile("10101010101", "DEMO/DEMO_FILE", "202020202", "DEMO/DEMO_FILE"));
		System.out.println("Done");
	}
}
