package io.mosip.registration.processor.core.queue.factory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MosipActiveMq extends MosipQueue{

	private String queueName;
	private String username;
	private String password;
	private String brokerUrl;
	private ActiveMQConnectionFactory activeMQConnectionFactory;

	public MosipActiveMq(String queueName, String username, String password, String brokerUrl) {
		this.queueName = queueName;
		this.username = username;
		this.password = password;
		this.brokerUrl = brokerUrl;
		createConnection(username, password, brokerUrl);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
		return activeMQConnectionFactory;
	}

	@Override
	public void createConnection(String username, String password, String brokerUrl) {
		this.activeMQConnectionFactory = new ActiveMQConnectionFactory(username, password, brokerUrl);
	}

	@Override
	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
