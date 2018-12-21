package io.mosip.registration.processor.retry.verticle.stages;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

/**
 * Retry stage verticle class for re processing different stages in case of
 * internal/system erroe
 *
 * @author Jyoti Prakash Nayak
 */
@RefreshScope
@Component
public class RetryStage extends MosipVerticleManager {
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MosipVerticleManager.class);
	
	@Value("${registration.processor.wait.period}")
	private int waitPeriod;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	private MosipEventBus mosipEventBus;

	/**
	 * method to deploy retry-stage.
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consume(this.mosipEventBus, MessageBusAddress.RETRY_BUS);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO dto) {
		int retrycount = (dto.getRetryCount() == null) ? 0 : dto.getRetryCount() + 1;
		dto.setRetryCount(retrycount);
		Timer timer = new Timer();
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"retry count  ","is : "+retrycount);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (dto.getRetryCount() < 5) {
					RetryStage.this.send(mosipEventBus, dto.getMessageBusAddress(), dto);

				} else {
					RetryStage.this.send(mosipEventBus, MessageBusAddress.ERROR, dto);

				}
			}
		}, waitPeriod * 60000l);
		return null;
	}

}
