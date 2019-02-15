package io.mosip.registration.processor.core.queue.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import io.mosip.registration.processor.core.queue.factory.MosipActiveMq;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;

public class MosipActiveMqImpl implements MosipQueueManager<MosipActiveMq, byte[]> {

	private Connection connection;
	private Session session;
	private Destination destination;

	public void setup(MosipActiveMq mosipActiveMq, String address) {
		if (connection == null) {
			try {
				this.connection = mosipActiveMq.getActiveMQConnectionFactory().createConnection();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (session == null) {
			try {
				connection.start();
				this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (this.destination == null) {
			try {
				this.session.createQueue(address);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Boolean send(MosipActiveMq mosipActiveMq, byte[] message, String address) {
		boolean flag = false;
		ActiveMQConnectionFactory activeMQConnectionFactory = mosipActiveMq.getActiveMQConnectionFactory();
		if(activeMQConnectionFactory==null) {
			System.out.println("Problem");
		}
		else {
			setup(mosipActiveMq, address);
		}
		if (destination == null) {
			setup(mosipActiveMq, address);
		} else {
			try {
				MessageProducer messageProducer = session.createProducer(destination);
				BytesMessage byteMessage = session.createBytesMessage();
				byteMessage.writeObject(message);
				messageProducer.send(byteMessage);
				flag = true;
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	@Override
	public byte[] consume(MosipActiveMq mosipActiveMq, String address) {
		if (destination == null) {
			setup(mosipActiveMq, address);
		}
		MessageConsumer consumer;
		try {
			consumer = session.createConsumer(destination);
			BytesMessage message = (BytesMessage) consumer.receive(5000);
			if (message != null) {
				byte[] data = new byte[(int) message.getBodyLength()];
				message.readBytes(data);
				return data;
			} else {
				System.out.println("no file");
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
