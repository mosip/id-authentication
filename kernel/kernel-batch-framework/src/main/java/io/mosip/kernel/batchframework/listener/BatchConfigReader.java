package io.mosip.kernel.batchframework.listener;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.mosip.kernel.batchframework.config.LoggerConfiguration;
import io.mosip.kernel.batchframework.constant.BatchExceptionConstant;
import io.mosip.kernel.batchframework.constant.BatchPropertyConstant;
import io.mosip.kernel.batchframework.launcher.BatchJobLauncher;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
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

@Component
public class BatchConfigReader {

	/**
	 * Batch Job uri.
	 */
	@Value("${mosip.kernel.batch.uri}")
	String jobUri;
	/**
	 * reference to MosipBatch.
	 */
	@Autowired
	BatchJobLauncher batch;

	/**
	 * This method reads property value from class path and start cloud data flow
	 * server,register and launch batch jobs.
	 */
	@EventListener(classes = { ApplicationReadyEvent.class })
	public void propertiesSet() {

		String[] fileText = jobUri.split(BatchPropertyConstant.BATCH_KEY_SEPARATOR.getProperty());

		File file = new File(System.getProperty(BatchPropertyConstant.TEMPORARY_DIRECTORY.getProperty())
				+ BatchPropertyConstant.BATCH_JOB_FILE.getProperty());
		try {
			FileUtils.writeLines(file, Arrays.asList(fileText));
		} catch (IOException e) {
			Logger logger = LoggerConfiguration.logConfig(BatchConfigReader.class);
			logger.error(BatchPropertyConstant.EMPTY_STRING.getProperty(),
					BatchPropertyConstant.ERROR_CODE.getProperty(), BatchExceptionConstant.INPUT_OUTPUT.getErrorCode(),
					e.getMessage());
		}

		batch.registerJobs(file);

	}

}