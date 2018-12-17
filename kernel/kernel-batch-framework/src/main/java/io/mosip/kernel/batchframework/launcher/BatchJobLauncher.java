package io.mosip.kernel.batchframework.launcher;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.batchframework.constant.BatchExceptionConstant;
import io.mosip.kernel.batchframework.constant.BatchPropertyConstant;
import io.mosip.kernel.batchframework.exception.ClientErrorException;
import io.mosip.kernel.batchframework.exception.EmptyJobDescriptionException;
import io.mosip.kernel.batchframework.exception.InvalidFileUriException;
import io.mosip.kernel.batchframework.exception.InvalidJobDescriptionException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;


/**
 * This class registers and launches the batch job jars in cloud data flow
 * server.
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Component
public class BatchJobLauncher {

	@Value("${mosip.kernel.batch.server.port}")
	String port;
	@Value("${mosip.kernel.batch.server.host}")
	String host;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	/**
	 * This method register and launch batch jobs in cloud data flow server which
	 * are mentioned in file whose uri is provided.
	 * 
	 * @param file
	 *            uniform resource identifier of file contains (task names and uri
	 *            of batch jobs) which need to be register and launch.
	 */
	public void registerJobs(File file) {
		List<String> jobDescription = null;

		List<String> taskNames = new ArrayList<>();

		String serverUrl = urlBuilder(host, port);

		RestTemplate restTemplate = restTemplateBuilder.build();
		try {
			jobDescription = FileUtils.readLines(file, Charset.forName(BatchPropertyConstant.CHARSET.getProperty()));

		} catch (IOException e) {
			throw new InvalidFileUriException(BatchExceptionConstant.INVALID_URI.getErrorCode(),
					BatchExceptionConstant.INVALID_URI.getErrorMessage(), e.getCause());
		}

		jobDescription.removeAll(Arrays.asList(BatchPropertyConstant.EMPYT_LINES.getProperty()));

		if (jobDescription.isEmpty()) {
			throw new EmptyJobDescriptionException(BatchExceptionConstant.EMPTY_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstant.EMPTY_JOB_DESCRIPTION.getErrorMessage());
		}

		try {
			UriComponentsBuilder register = UriComponentsBuilder
					.fromHttpUrl(serverUrl + BatchPropertyConstant.REGISTER_JOBS.getProperty())
					.queryParam(BatchPropertyConstant.REGISTER_PARAM.getProperty(), file.toURI().toString());

			restTemplate.postForEntity(register.toUriString(), HttpMethod.POST, Object.class);
		} catch (HttpServerErrorException e) {
			throw new InvalidJobDescriptionException(BatchExceptionConstant.INVALID_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstant.INVALID_JOB_DESCRIPTION.getErrorMessage());
		}

		taskCreater(jobDescription, taskNames);

		jobLauncher(taskNames);

	}

	private String urlBuilder(String host, String port) {

		return BatchPropertyConstant.PROTOCALL.getProperty() + host
				+ BatchPropertyConstant.ADDRESS_PORT_SEPARATOR.getProperty() + port;
	}

	/**
	 * This methods create task on cloud data flow server for registered batch jobs.
	 */
	private void taskCreater(List<String> jobDescription, List<String> taskNames) {
		String serverUrl = urlBuilder(host, port);
		RestTemplate restTemplate = restTemplateBuilder.build();
		jobDescription.forEach(jobDetails -> {
			int startIndex = jobDetails.indexOf((int) '.');
			int mid = jobDetails.indexOf((int) ':');
			taskNames.add(jobDetails.substring(startIndex + 1, mid));

		});
		try {
			taskNames.forEach(jobName -> {
				UriComponentsBuilder taskCreater = UriComponentsBuilder
						.fromHttpUrl(serverUrl + BatchPropertyConstant.TASK_CREATER.getProperty());
				taskCreater
						.queryParam(BatchPropertyConstant.TASK_CREATER_FIRST_PARAM.getProperty(),
								BatchPropertyConstant.TASK_CREATER_FIRST_PARAM_VALUE.getProperty() + jobName)
						.queryParam(BatchPropertyConstant.TASK_CREATER_SECOND_PARAM.getProperty(), jobName);

				restTemplate.postForEntity(taskCreater.toUriString(), HttpMethod.POST, Object.class);

			});

		} catch (HttpClientErrorException e) {
			throw new ClientErrorException(BatchExceptionConstant.DUPLICATE_JOB.getErrorCode(),
					BatchExceptionConstant.DUPLICATE_JOB.getErrorMessage(), e.getCause());
		}

	}

	/**
	 * This method launches the created task on cloud data flow server.
	 */
	private void jobLauncher(List<String> taskNames) {
		String serverUrl = urlBuilder(host, port);
		RestTemplate restTemplate = restTemplateBuilder.build();
		try {
			taskNames.forEach(jobName -> {
				UriComponentsBuilder taskLauncher = UriComponentsBuilder
						.fromHttpUrl(serverUrl + BatchPropertyConstant.TASK_LAUNCHER.getProperty());
				taskLauncher.queryParam("name", "task-" + jobName);
				restTemplate.postForEntity(taskLauncher.toUriString(), HttpMethod.POST, String.class);

			});
		} catch (HttpServerErrorException e) {
			throw new InvalidJobDescriptionException(BatchExceptionConstant.INVALID_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstant.INVALID_JOB_DESCRIPTION.getErrorMessage());
		}
	}

}
