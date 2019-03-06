/**
 * 
 */
package io.mosip.registration.processor.print.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.print.service.dto.PrintQueueDTO;

/**
 * @author Ranjitha Siddegowda
 *
 */
@Service
public class PrintPostServiceImpl {
	
	@Value("${registration.processor.queue.username}")
	private String username;

	@Value("${registration.processor.queue.password}")
	private String password;

	@Value("${registration.processor.queue.url}")
	private String url;

	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;

	@Value("${registration.processor.queue.address}")
	private String address;
	
	@Value("$registration.processor.PRINT_POSTAL_SERVICE")
	private String printPostServiceDirectory;
	
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;
	
	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@SuppressWarnings("unchecked")
	public boolean generatePrintandPostal(String regId, MosipQueue queue) {

		JSONObject response = null;

		String uin = packetInfoManager.getUINByRid(regId).get(0);

		checkFromTrusted(queue);

		if (!uin.isEmpty()) {
			response = new JSONObject();
			response.put("UIN", uin);
			response.put("Status", "Success");
		} else {
			response = new JSONObject();
			response.put("UIN", uin);
			response.put("Status", "Failure");
		}

		boolean isPdfAddedtoQueue = false;
		try {
			isPdfAddedtoQueue = mosipQueueManager.send(queue, response.toString().getBytes("UTF-8"),
					"provider-response");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return isPdfAddedtoQueue;
	}

	public boolean checkFromTrusted(MosipQueue queue) {
		boolean result = false;

		try {
			byte[] isPdfAddedtoQueue = mosipQueueManager.consume(queue, address);

			ByteArrayInputStream in = new ByteArrayInputStream(isPdfAddedtoQueue);
			ObjectInputStream is = new ObjectInputStream(in);
			PrintQueueDTO printQueueDTO = (PrintQueueDTO) is.readObject();

			OutputStream out;
			OutputStream out1;
			String seperator = "\\";
	
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
			result = true;

		} catch (IOException | ClassNotFoundException exp) {
			exp.getStackTrace();
		}
		return result;
	}

}
