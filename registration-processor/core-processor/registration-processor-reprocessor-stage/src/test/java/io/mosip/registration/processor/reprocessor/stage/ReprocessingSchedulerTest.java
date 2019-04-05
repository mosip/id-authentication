package io.mosip.registration.processor.reprocessor.stage;

import static org.junit.Assert.assertNotNull;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.eventbus.SendContext;

@RunWith(MockitoJUnitRunner.class)
public class ReprocessingSchedulerTest {

	@Mock
	public Vertx vertx;

	@Mock
	AsyncResult<String> res;

	@Mock
	Environment env;

	private Logger fooLogger;

	private ListAppender<ILoggingEvent> listAppender;

	@Before
	public void setup() {
		fooLogger = (Logger) LoggerFactory.getLogger(ReprocessorStage.class);
		listAppender = new ListAppender<>();
	}

	@InjectMocks
	ReprocessorStage reprocessorStage = new ReprocessorStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl) {
			return new MosipEventBus(vertx);
		}
	};

	@Test
	public void testDeploySuccess() {
		reprocessorStage.deployVerticle();
		assertNotNull(reprocessorStage.mosipEventBus);
	}

	@Test
	public void testDeploySchedulerTest() {
		listAppender.start();
		fooLogger.addAppender(listAppender);
		Mockito.when(res.succeeded()).thenReturn(true);
		Mockito.when(vertx.eventBus()).thenReturn(getMockEventBus());
		reprocessorStage.schedulerResult(res);
		Assertions.assertThat(listAppender.list).extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
				.contains(Tuple.tuple(Level.DEBUG,
						"SESSIONID - REGISTRATIONID -  - ReprocessorStage::schedular()::deployed"));
	}

	public EventBus getMockEventBus() {
		return new EventBus() {

			@Override
			public boolean isMetricsEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public EventBus unregisterDefaultCodec(Class clazz) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus unregisterCodec(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void start(Handler<AsyncResult<Void>> completionHandler) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T> MessageProducer<T> sender(String address, DeliveryOptions options) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageProducer<T> sender(String address) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> EventBus send(String address, Object message, DeliveryOptions options,
					Handler<AsyncResult<Message<T>>> replyHandler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus send(String address, Object message, DeliveryOptions options) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> EventBus send(String address, Object message, Handler<AsyncResult<Message<T>>> replyHandler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus send(String address, Object message) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus removeInterceptor(Handler<SendContext> interceptor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> EventBus registerDefaultCodec(Class<T> clazz, MessageCodec<T, ?> codec) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus registerCodec(MessageCodec codec) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageProducer<T> publisher(String address, DeliveryOptions options) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageProducer<T> publisher(String address) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus publish(String address, Object message, DeliveryOptions options) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventBus publish(String address, Object message) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> handler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageConsumer<T> localConsumer(String address) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> MessageConsumer<T> consumer(String address) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void close(Handler<AsyncResult<Void>> completionHandler) {
				// TODO Auto-generated method stub

			}

			@Override
			public EventBus addInterceptor(Handler<SendContext> interceptor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
