package io.mosip.registration.processor.manual.verification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class CustomEnvironment {

	public String getClusterManagerUrl() {
		return clusterManagerUrl;
	}

	public void setClusterManagerUrl(String clusterManagerUrl) {
		this.clusterManagerUrl = clusterManagerUrl;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;
	
	@Value("${server.port}")
	private int serverPort;
	
}