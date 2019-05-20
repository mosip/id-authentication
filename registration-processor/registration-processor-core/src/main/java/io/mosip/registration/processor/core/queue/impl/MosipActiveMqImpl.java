package io.mosip.registration.processor.core.queue.impl;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.queue.factory.MosipActiveMq;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.queue.factory.QueueListenerFactory;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.core.queue.impl.exception.InvalidConnectionException;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;

/**
 * This class is ActiveMQ implementation for Mosip Queue
 * 
 * @author Mukul Puspam
 * 
 * @since 0.8.0
 */
public class MosipActiveMqImpl implements MosipQueueManager<MosipQueue, byte[]> {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipActiveMqImpl.class);

	private Connection connection;
	private Session session;
	private Destination destination;

	/**
	 * The method to set up session and destination
	 * 
	 * @param mosipActiveMq The Mosip ActiveMq instance
	 */
	private void setup(MosipActiveMq mosipActiveMq) {
		try {
			this.connection = mosipActiveMq.getActiveMQConnectionFactory().createConnection();
			if (session == null) {
				connection.start();
				this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			}
		} catch (JMSException e) {
			throw new ConnectionUnavailableException(PlatformErrorMessages.RPR_MQI_CONNECTION_UNAVAILABLE.getMessage(),
					e);
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
			throw new InvalidConnectionException(PlatformErrorMessages.RPR_MQI_INVALID_CONNECTION.getMessage());
		}
		setup(mosipActiveMq);
		try {
			destination = session.createQueue(address);
			MessageProducer messageProducer = session.createProducer(destination);
			BytesMessage byteMessage = session.createBytesMessage();
			byteMessage.writeObject(message);
			messageProducer.send(byteMessage);
			flag = true;
		} catch (JMSException e) {
			throw new ConnectionUnavailableException(
					PlatformErrorMessages.RPR_MQI_UNABLE_TO_SEND_TO_QUEUE.getMessage());
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
	public byte[] consume(MosipQueue mosipQueue, String address, QueueListener object) {

		MosipActiveMq mosipActiveMq = (MosipActiveMq) mosipQueue;
		ActiveMQConnectionFactory activeMQConnectionFactory = mosipActiveMq.getActiveMQConnectionFactory();
		if (activeMQConnectionFactory == null) {
			throw new InvalidConnectionException(PlatformErrorMessages.RPR_MQI_INVALID_CONNECTION.getMessage());
		}
		if (destination == null) {
			setup(mosipActiveMq);
		}
		MessageConsumer consumer;
		try {
			destination = session.createQueue(address);
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(QueueListenerFactory.getListener(mosipQueue.getQueueName(), object));
		} catch (JMSException e) {
			throw new ConnectionUnavailableException(
					PlatformErrorMessages.RPR_MQI_UNABLE_TO_CONSUME_FROM_QUEUE.getMessage());
		}
		return null;
	}

}
