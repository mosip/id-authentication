package io.mosip.util;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

/**
 * This class is ActiveMQ implementation for Mosip Queue
 * 
 * @author Mukul Puspam
 * 
 * @since 0.8.0
 */
public class MosipActiveMqImpl implements MosipQueueManager<MosipQueue, byte[]> {

	/** The reg proc logger. */
	private static Logger logger = Logger.getLogger(MosipActiveMqImpl.class);

	private Connection connection;
	private Session session;
	private Destination destination;

	/**
	 * The method to set up session and destination
	 * 
	 * @param mosipActiveMq
	 *            The Mosip ActiveMq instance
	 */
	private void setup(MosipActiveMq mosipActiveMq) {
		if (connection == null) {
			try {
				this.connection = mosipActiveMq.getActiveMQConnectionFactory().createConnection();
				if (session == null) {
					connection.start();
					this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				}
			} catch (JMSException e) {
				logger.error("connection failed...");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.queue.MosipQueueManager#send(java.
	 * lang.Object, java.lang.Object, java.lang.String)
	 */
	@Override
	public Boolean send(MosipQueue mosipQueue, byte[] message, String address) {
		boolean flag = false;
		MosipActiveMq mosipActiveMq = (MosipActiveMq) mosipQueue;
		ActiveMQConnectionFactory activeMQConnectionFactory = mosipActiveMq.getActiveMQConnectionFactory();
		if (activeMQConnectionFactory == null) {
			logger.info("Invalid connection");
		}
		if (destination == null) {
			setup(mosipActiveMq);
		}
		try {
			destination = session.createQueue(address);
			MessageProducer messageProducer = session.createProducer(destination);
			BytesMessage byteMessage = session.createBytesMessage();
			byteMessage.writeObject(message);
			messageProducer.send(byteMessage);
			flag = true;
		} catch (JMSException e) {
			logger.error("connection unavailable");
		}
		return flag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.queue.MosipQueueManager#consume(java
	 * .lang.Object, java.lang.String)
	 */
	@Override
	public byte[] consume(MosipQueue mosipQueue, String address) {
		MosipActiveMq mosipActiveMq = (MosipActiveMq) mosipQueue;
		ActiveMQConnectionFactory activeMQConnectionFactory = mosipActiveMq.getActiveMQConnectionFactory();
		if (activeMQConnectionFactory == null) {
			logger.error("connection failed");
		}
		if (destination == null) {
			setup(mosipActiveMq);
		}
		MessageConsumer consumer;
		try {
			destination = session.createQueue(address);
			consumer = session.createConsumer(destination);
			BytesMessage message = (BytesMessage) consumer.receive(5000);
			if (message != null) {
				byte[] data = new byte[(int) message.getBodyLength()];
				message.readBytes(data);
				consumer.close();
				return data;
			} else {
				logger.info("no file found in the queue");
			}
		} catch (JMSException e) {
			logger.error("not able to consume from queue");
		}
		return null;
	}

}
