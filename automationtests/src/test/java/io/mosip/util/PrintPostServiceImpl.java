/**
* 
 */
package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.dbdto.PrintQueueDTO;

/**
 * @author Ranjitha Siddegowda
 *
 */
@Service
public class PrintPostServiceImpl {

	//@Value("${registration.processor.queue.username}")
	private String username = "admin";

	//@Value("${registration.processor.queue.password}")
	private String password = "admin";

	//@Value("${registration.processor.queue.url}")
	private String url = "tcp://104.211.200.46:61616";

	//@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue = "ACTIVEMQ";

	//@Value("${registration.processor.queue.address}")
	private String address = "print-service-qa";

	//@Value("${registration.processor.PRINT_POSTAL_SERVICE}")
	private String printPostServiceDirectory;

	/** The print & postal service provider address. */
	private String printPostalAddress = "postal-service";

//	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager = new MosipActiveMqImpl();

	@SuppressWarnings("unchecked")
	public boolean generatePrintandPostal(String regId, MosipQueue queue, MosipQueueManager<MosipQueue, byte[]> mosipQueueManager) {

		JSONObject response = null;

		PrintQueueDTO result = checkFromTrusted(queue, mosipQueueManager);

		if (!result.getUin().isEmpty()) {
			response = new JSONObject();
			response.put("UIN", result.getUin());
			response.put("Status", "Success");
		} else {
			response = new JSONObject();
			response.put("UIN", result.getUin());
			response.put("Status", "Failure");
		}

		boolean isPdfAddedtoQueue = false;
		try {
			isPdfAddedtoQueue = mosipQueueManager.send(queue, response.toString().getBytes("UTF-8"),
					printPostalAddress);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return isPdfAddedtoQueue;
	}

	public PrintQueueDTO checkFromTrusted(MosipQueue queue, MosipQueueManager<MosipQueue, byte[]> mosipQueueManager) {
		PrintQueueDTO printQueueDTO = new PrintQueueDTO();

		try {
			byte[] isPdfAddedtoQueue = mosipQueueManager.consume(queue, address);

			ByteArrayInputStream in = new ByteArrayInputStream(isPdfAddedtoQueue);
			ObjectInputStream is = new ObjectInputStream(in);
			printQueueDTO = (PrintQueueDTO) is.readObject();

			OutputStream out;
			OutputStream out1;
			String seperator = File.separator;

			Path dirPathObj = Paths.get(printPostServiceDirectory + seperator + printQueueDTO.getUin());
			boolean dirExists = Files.exists(dirPathObj);
			if (dirExists) {

				out = new FileOutputStream(dirPathObj + seperator + printQueueDTO.getUin() + ".pdf");
				out.write(printQueueDTO.getPdfBytes());
				out.close();

				out1 = new FileOutputStream(dirPathObj + seperator + printQueueDTO.getUin() + ".txt");
				out1.write(printQueueDTO.getTextBytes());
				out1.close();

			} else {
				try {
					// Creating The New Directory Structure
					Files.createDirectories(dirPathObj);

					out = new FileOutputStream(dirPathObj + seperator + printQueueDTO.getUin() + ".pdf");
					out.write(printQueueDTO.getPdfBytes());
					out.close();

					out1 = new FileOutputStream(dirPathObj + seperator + printQueueDTO.getUin() + ".txt");
					out1.write(printQueueDTO.getTextBytes());
					out1.close();

				} catch (IOException ioExceptionObj) {
					System.out.println(
							"Problem Occured While Creating The Directory Structure= " + ioExceptionObj.getMessage());
				}
			}

		} catch (IOException | ClassNotFoundException exp) {
			exp.getStackTrace();
		}
		return printQueueDTO;
	}

}
