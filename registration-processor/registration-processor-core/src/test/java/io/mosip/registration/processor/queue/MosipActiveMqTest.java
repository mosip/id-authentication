/*package io.mosip.registration.processor.queue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.queue.factory.MosipActiveMq;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.impl.MosipActiveMqImpl;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.core.queue.impl.exception.InvalidConnectionException;

@RunWith(SpringRunner.class)
public class MosipActiveMqTest {
	
	@InjectMocks
	MosipActiveMqImpl mosipActiveMqImpl;
	
	@Mock
	MosipActiveMq mosipActiveMq;
	
	@Mock
	ActiveMQConnectionFactory factory;
	
	MosipQueue mosipQueue;
	
	@Test
	public void testSendSuccess() throws JMSException {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false");
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		assertTrue(mosipActiveMqImpl.send(mosipQueue, bytes, "address"));
	}
	
	@Test(expected = ConnectionUnavailableException.class)
	public void testJMSException() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false") {
			@Override
			public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
				return getCustomActiveMQConnectionFactory();
			}
		};
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		mosipActiveMqImpl.send(mosipQueue, bytes, "address");
	}
	
	@Test(expected = ConnectionUnavailableException.class)
	public void testConsumeFailure() throws JMSException {

		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false") {
			@Override
			public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
				return getCustomActiveMQConnectionFactory();
			}
		};
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		mosipActiveMqImpl.consume(mosipQueue, "address");
	}
	
	@Test(expected = InvalidConnectionException.class)
	public void invalidConnectionSendExceptionTest() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false") {
			@Override
			public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
				return null;
			}
		};
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		assertTrue(mosipActiveMqImpl.send(mosipQueue, bytes, "address"));
	}
	
	@Test(expected = InvalidConnectionException.class)
	public void invalidConnectionConsumeExceptionTest() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false") {
			@Override
			public ActiveMQConnectionFactory getActiveMQConnectionFactory() {
				return null;
			}
		};
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		assertNotNull(mosipActiveMqImpl.consume(mosipQueue,"address"));
	}
	
	@Test
	public void testConsumeSuccess() {
		mosipQueue = new MosipActiveMq("admin", "admin","vm://localhost?broker.persistent=false");
		byte[] bytes = "message".getBytes();
		when(mosipActiveMq.getActiveMQConnectionFactory()).thenReturn(factory);
		mosipActiveMqImpl.send(mosipQueue, bytes, "address");
		assertNotNull(mosipActiveMqImpl.consume(mosipQueue, "address"));
	}
	
	private Session getSession() {
		
		return new Session() {

			@Override
			public void close() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void commit() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public QueueBrowser createBrowser(Queue arg0) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public QueueBrowser createBrowser(Queue arg0, String arg1) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BytesMessage createBytesMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MessageConsumer createConsumer(Destination arg0) throws JMSException {
				throw new JMSException(null);
			}

			@Override
			public MessageConsumer createConsumer(Destination arg0, String arg1) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MessageConsumer createConsumer(Destination arg0, String arg1, boolean arg2) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1, String arg2, boolean arg3)
					throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MapMessage createMapMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Message createMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ObjectMessage createObjectMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ObjectMessage createObjectMessage(Serializable arg0) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MessageProducer createProducer(Destination arg0) throws JMSException {
				throw new JMSException(null);
			}

			@Override
			public Queue createQueue(String arg0) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StreamMessage createStreamMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TemporaryQueue createTemporaryQueue() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TemporaryTopic createTemporaryTopic() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TextMessage createTextMessage() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TextMessage createTextMessage(String arg0) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Topic createTopic(String arg0) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getAcknowledgeMode() throws JMSException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public MessageListener getMessageListener() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean getTransacted() throws JMSException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void recover() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void rollback() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setMessageListener(MessageListener arg0) throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void unsubscribe(String arg0) throws JMSException {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	private Connection getconnection() {
		return new Connection() {

			@Override
			public void close() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public ConnectionConsumer createConnectionConsumer(Destination arg0, String arg1, ServerSessionPool arg2,
					int arg3) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConnectionConsumer createDurableConnectionConsumer(Topic arg0, String arg1, String arg2,
					ServerSessionPool arg3, int arg4) throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Session createSession(boolean arg0, int arg1) throws JMSException {
				return getSession();
			}

			@Override
			public String getClientID() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ExceptionListener getExceptionListener() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConnectionMetaData getMetaData() throws JMSException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setClientID(String arg0) throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setExceptionListener(ExceptionListener arg0) throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void start() throws JMSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stop() throws JMSException {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	private ActiveMQConnectionFactory getCustomActiveMQConnectionFactory() {
		return new ActiveMQConnectionFactory() {
			@Override
			public Connection createConnection() {
				return getconnection();
			}
		};
	};

}
*/