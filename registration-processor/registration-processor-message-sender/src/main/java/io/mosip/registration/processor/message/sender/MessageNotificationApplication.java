package io.mosip.registration.processor.message.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.message.sender.utility.NotificationTemplateType;
import io.mosip.registration.processor.message.sender.utility.TriggerNotification;

/**
 * The Class MessageNotificationApplication.
 *
 * @author Alok Ranjan
 * @author Ayush Keer
 */

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.message.sender",
		"io.mosip.registration.processor.message.sender.test", "io.mosip.registration.processor.core.constant",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.filesystem.ceph.adapter.impl" })

public class MessageNotificationApplication implements CommandLineRunner{
	
	@Autowired
	TriggerNotification tr;

	/**
	 * Main method to instantiate the spring boot application.
	 *
	 * @param args
	 *            the command line arguments
	 */

	public static void main(String[] args) {
		SpringApplication.run(MessageNotificationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		tr.triggerNotification("85425022110000120190117110505", NotificationTemplateType.DUPLICATE_UIN);
	}

}