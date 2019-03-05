/**
 * 
 */
package io.mosip.registration.processor.print.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

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
	
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;
	
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;
	
	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@SuppressWarnings("unchecked")
	public boolean generatePrintandPostal(String regId) {

		JSONObject response = null;

		String uin = packetInfoManager.getUINByRid(regId).get(0);

		checkFromTrusted();

		if (!uin.isEmpty()) {
			response = new JSONObject();
			response.put("UIN", uin);
			response.put("Status", "Success");
		} else {
			response = new JSONObject();
			response.put("UIN", uin);
			response.put("Status", "Failure");
		}

		MosipQueue queue = mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);

		boolean isPdfAddedtoQueue = false;
		try {
			isPdfAddedtoQueue = mosipQueueManager.send(queue, response.toString().getBytes("UTF-8"),
					"provider-response");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return isPdfAddedtoQueue;
	}

	public boolean checkFromTrusted() {
		boolean result = false;
		MosipQueue queue = mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);

		try {
			byte[] isPdfAddedtoQueue = mosipQueueManager.consume(queue, address);
			OutputStream out;
			out = new FileOutputStream("consumed.pdf");
			out.write(isPdfAddedtoQueue);
			out.close();
			result = true;
		} catch (IOException exp) {
			exp.getStackTrace();
		}
		return result;
	}

}
