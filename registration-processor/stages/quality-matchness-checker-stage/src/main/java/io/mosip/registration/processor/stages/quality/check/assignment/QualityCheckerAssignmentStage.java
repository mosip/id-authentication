/**
 * 
 */
package io.mosip.registration.processor.stages.quality.check.assignment;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;

/**
 * @author Jyoti Prakash Nayak M1030448
 *
 */
@RefreshScope
@Component
public class QualityCheckerAssignmentStage extends MosipVerticleManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(QualityCheckerAssignmentStage.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	QualityCheckManager<String, QCUserDto> qualityCheckManager;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/**
	 * Method to consume quality check address bus and receive the packet details
	 * that needs to be checked for quality
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consume(mosipEventBus, MessageBusAddress.QUALITY_CHECK_BUS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		QCUserDto qcUserDto=qualityCheckManager.assignQCUser(object.getRid());
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.REGISTRATIONID.toString(),object.getRid()+" Qc User ID Is : "+qcUserDto.getQcUserId(),"packet assigned to qcuser successfully");
		return null;
	}

}
