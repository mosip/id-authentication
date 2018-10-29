package io.mosip.kernel.batchframework.configuration;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import io.mosip.kernel.batchframework.constants.BatchPropertyConstant;
import io.mosip.kernel.batchframework.impl.BatchJobLauncher;
import io.mosip.kernel.core.util.FileUtils;

/**
 * This configuration class reads the property value from class path and start
 * cloud data flow server.
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 */

@Configuration
public class BatchConfigReader {

	/**
	 * reference to MosipBatch.
	 */
	@Autowired
	BatchJobLauncher batch;

	/**
	 * Batch Job uri.
	 */
	@Value("${mosip.batch.uri}")
	String jobUri;

	/**
	 * This method reads property value from class path and start cloud data flow
	 * server,register and launch batch jobs.
	 */
	@EventListener(classes = { ApplicationReadyEvent.class })
	public void propertiesSet() throws Exception {

		String[] fileText = jobUri.split(BatchPropertyConstant.BATCH_KEY_SEPARATOR.getProperty());

		File file = new File(System.getProperty(BatchPropertyConstant.TEMPORARY_DIRECTORY.getProperty())
				+ BatchPropertyConstant.BATCH_JOB_FILE.getProperty());
		FileUtils.writeLines(file, Arrays.asList(fileText));

		batch.registerJobs(file);

	}

}