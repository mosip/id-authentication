package io.mosip.registration.processor.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ActiveMQBridge {

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String BROKERURL = "tcp://localhost:61616";

	ConnectionFactory connectionFactory;

	Connection connection = null;

	Session session;

	Destination destination;

	MessageConsumer consumer;

	MessageProducer producer;

	String queueAddress;

	private void setup(String address) {
		queueAddress = address;
		connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKERURL);

		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(queueAddress);

		} catch (JMSException e) {
			e.printStackTrace();
 		}

	}

	public byte[] receiveFromQueue(String address) throws JMSException {
		if (session == null || !address.equals(queueAddress)) {
			setup(address);
		}
		consumer = session.createConsumer(destination);
		BytesMessage message = (BytesMessage) consumer.receive(5000);
		if (message != null) {
			byte[] data = new byte[(int) message.getBodyLength()];
			message.readBytes(data);
			return data;
		} else {
			System.out.println("no file");
		}
		return null;

	}

	public void sendToQueue(String address, byte[] bytes) throws JMSException {
		if (session == null || !address.equals(queueAddress)) {
			setup(address);
		}
		producer = session.createProducer(destination);

		producer.setDeliveryMode(DeliveryMode.PERSISTENT);

		try {
			BytesMessage x = session.createBytesMessage();
			x.writeObject(bytes);
			producer.send(x);
			System.out.println("----------Message Sent--------------");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Message failed to be sent: " + e.getLocalizedMessage());
		}
	}
}