package io.mosip.kernel.batchframework.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.batchframework.constants.BatchExceptionConstants;
import io.mosip.kernel.batchframework.constants.BatchPropertyConstant;
import io.mosip.kernel.batchframework.exceptions.EmptyJobDescriptionException;
import io.mosip.kernel.batchframework.exceptions.ClientErrorException;
import io.mosip.kernel.batchframework.exceptions.InvalidFileUriException;
import io.mosip.kernel.batchframework.exceptions.InvalidJobDescriptionException;
import io.mosip.kernel.batchframework.response.Embedded;
import io.mosip.kernel.batchframework.response.TaskCreater;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;

/**
 * This class registers and launches the batch job jars in cloud data flow
 * server.
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Configuration
public class BatchJobLauncher {

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	static List<String> jobDescription = null;

	static List<String> taskNames = new ArrayList<>();

	@Value("${mosip.batch.server.port}")
	String port;
	@Value("${mosip.batch.server.host}")
	String host;

	/**
	 * This method register and launch batch jobs in cloud data flow server which
	 * are mentioned in file whose uri is provided.
	 * 
	 * @param uri
	 *            uniform resource identifier of file contains (task names and uri
	 *            of batch jobs) which need to be register and launch.
	 */
	public void registerJobs(File file) {
		String serverUrl = urlBuilder(host, port);

		RestTemplate restTemplate = restTemplateBuilder.build();
		try {
			jobDescription = FileUtils.readLines(file, Charset.forName(BatchPropertyConstant.CHARSET.getProperty()));

		} catch (MosipIOException e) {
			throw new InvalidFileUriException(BatchExceptionConstants.INVALID_URI.getErrorCode(),
					BatchExceptionConstants.INVALID_URI.getErrorMessage(), e.getCause());
		}

		jobDescription.removeAll(Arrays.asList(BatchPropertyConstant.EMPYT_LINES.getProperty()));

		if (jobDescription.isEmpty()) {
			throw new EmptyJobDescriptionException(BatchExceptionConstants.EMPTY_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstants.EMPTY_JOB_DESCRIPTION.getErrorMessage());
		}

		try {
			UriComponentsBuilder register = UriComponentsBuilder
					.fromHttpUrl(serverUrl + BatchPropertyConstant.REGISTER_JOBS.getProperty())
					.queryParam(BatchPropertyConstant.REGISTER_PARAM.getProperty(), file.toURI().toString());

			restTemplate.postForEntity(register.toUriString(), HttpMethod.POST, Embedded.class);
		} catch (HttpServerErrorException e) {
			System.out.println(e.getResponseBodyAsString());
			throw new InvalidJobDescriptionException(BatchExceptionConstants.INVALID_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstants.INVALID_JOB_DESCRIPTION.getErrorMessage());
		}

		taskCreater();

		jobLauncher();

	}

	private String urlBuilder(String host, String port) {
		String serverUrl = BatchPropertyConstant.PROTOCALL.getProperty() + host
				+ BatchPropertyConstant.ADDRESS_PORT_SEPARATOR.getProperty() + port;
		return serverUrl;
	}

	/**
	 * This methods create task on cloud data flow server for registered batch jobs.
	 */
	private void taskCreater() {
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

				restTemplate.postForEntity(taskCreater.toUriString(), HttpMethod.POST, TaskCreater.class);

			});

		} catch (HttpClientErrorException e) {
			throw new ClientErrorException(BatchExceptionConstants.DUPLICATE_JOB.getErrorCode(),
					BatchExceptionConstants.DUPLICATE_JOB.getErrorMessage(), e.getCause());
		}

	}

	/**
	 * This method launches the created task on cloud data flow server.
	 */
	private void jobLauncher() {
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
			throw new InvalidJobDescriptionException(BatchExceptionConstants.INVALID_JOB_DESCRIPTION.getErrorCode(),
					BatchExceptionConstants.INVALID_JOB_DESCRIPTION.getErrorMessage());
		}
	}

}
