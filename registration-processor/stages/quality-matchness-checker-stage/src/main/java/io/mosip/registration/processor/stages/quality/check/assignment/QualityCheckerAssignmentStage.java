/**
 * 
 */
package io.mosip.registration.processor.stages.quality.check.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;


/**
 * @author Jyoti Prakash Nayak M1030448
 *
 */
@RefreshScope
@Component
public class QualityCheckerAssignmentStage extends MosipVerticleManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(QualityCheckerAssignmentStage.class);

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	@Autowired
	QualityCheckManager<String, QCUserDto> qualityCheckManager;

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/**
	 * Method to consume quality check address bus and receive the packet details
	 * that needs to be checked for quality
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
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

		LOGGER.info(LOGDISPLAY, qcUserDto.getQcUserId(),object.getRid()+"  packet assigned to qcuser successfully");
		return null;
	}

}
