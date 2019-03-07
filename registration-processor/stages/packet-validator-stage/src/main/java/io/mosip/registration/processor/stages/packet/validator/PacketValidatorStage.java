/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.IdentityJsonValues;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.stages.utils.MasterDataValidation;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class PacketValidatorStage.
 *
 * @author M1022006
 * @author Girish Yarru
 */

@RefreshScope
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	/** Paacket validate Processor */
	@Autowired
	PacketValidateProcessor packetvalidateprocessor;

	// @Autowired
	// MasterDataValidation masterDataValidation;

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;
	/** The secs. */

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		// mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		// this.consumeAndSend(mosipEventBus
		// ,MessageBusAddress.PACKET_VALIDATOR_BUS_IN,MessageBusAddress.PACKET_VALIDATOR_BUS_OUT);

		IdentityJsonValues name = new IdentityJsonValues();
		IdentityJsonValues gender = new IdentityJsonValues();
		IdentityJsonValues region = new IdentityJsonValues();
		IdentityJsonValues province = new IdentityJsonValues();
		IdentityJsonValues city = new IdentityJsonValues();
		IdentityJsonValues postalcode = new IdentityJsonValues();

		gender.setValue("femle");
		region.setValue("Rabat Sale Kenitra");
		province.setValue("Rabat");
		city.setValue("bng-south");
		postalcode.setValue("10000");

		RegistrationProcessorIdentity registrationProcessorIdentity = new RegistrationProcessorIdentity();
		Identity identity = new Identity();
		identity.setGender(gender);
		identity.setRegion(region);
		identity.setProvince(province);
		identity.setCity(city);
		identity.setPostalCode(postalcode);

		registrationProcessorIdentity.setIdentity(identity);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		MasterDataValidation masterDataValidation = new MasterDataValidation(registrationStatusDto, env,
				registrationProcessorRestService);
		masterDataValidation.validateMasterData(registrationProcessorIdentity);
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
		return packetvalidateprocessor.process(object);
	}

}
