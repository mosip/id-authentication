package io.mosip.registration.processor.biodedupe;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.biodedupe.stage.BioDedupeStage;

/*@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.biodedupe",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.rest.client",
		"io.mosip.registration.processor.packet.storage", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.bio.dedupe"})*/
public class BioDedupeApplication {
	/** The validatebean. */
	/*@Autowired
	private BioDedupeStage validatebean;
*/
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		//SpringApplication.run(BioDedupeApplication.class, args);
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.biodedupe.config",
				"io.mosip.registration.processor.status.config",
				"io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.packet.storage.config",
				"io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				"io.mosip.registration.processor.core.kernel.beans"/*,
				"io.mosip.registration.processor.bio.dedupe"*/);
		
		configApplicationContext.refresh();

		BioDedupeStage bioDedupeStage = configApplicationContext.getBean(BioDedupeStage.class);

		bioDedupeStage.deployVerticle();
	}

	/**
	 * Deploy verticle.
	 *//*
	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}*/

}
