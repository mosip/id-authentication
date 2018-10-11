package io.mosip.registration.processor.camel.bridge;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import io.mosip.registration.processor.camel.bridge.processor.DemographicValidationProcessor;
import io.mosip.registration.processor.camel.bridge.processor.StructureValidationProcessor;
import io.mosip.registration.processor.camel.bridge.statuscode.MessageEnum;
import io.mosip.registration.processor.camel.bridge.util.BridgeUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;

/**
 * This class specifies the routes for MOSIP stages
 * 
 * @author Mukul Puspam
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
public class MosipBridgeRoutes extends RouteBuilder {

	private static Processor validateStructure = new StructureValidationProcessor();
	private static Processor validateDemographic = new DemographicValidationProcessor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception {

		errorHandler(deadLetterChannel(BridgeUtil.getEndpoint(MessageBusAddress.ERROR.getAddress())));

		// Decryption to Structure Validation routing
		from(BridgeUtil.getEndpoint(MessageBusAddress.BATCH_BUS.getAddress())).choice()
				.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(true))
				.to(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_IN.getAddress()));

		// Structure Validation to Demographic Validation routing
		from(BridgeUtil.getEndpoint(MessageBusAddress.STRUCTURE_BUS_OUT.getAddress())).process(validateStructure)
				.choice().when(header(MessageEnum.INTERNAL_ERROR.getParameter()).isEqualTo(true))
				.to(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS.getAddress()))
				.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(true))
				.to(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_IN.getAddress()))
				.when(header(MessageEnum.IS_VALID.getParameter()).isEqualTo(false))
				.to(BridgeUtil.getEndpoint(MessageBusAddress.ERROR.getAddress()));

		 // Demographic validation to Biometric validation routing
		 from(BridgeUtil.getEndpoint(MessageBusAddress.DEMOGRAPHIC_BUS_OUT.getAddress())).process(validateDemographic)
		 .choice().when(header(MessageEnum.INTERNAL_ERROR.getParameter()).isEqualTo(true))
		 .to(BridgeUtil.getEndpoint(MessageBusAddress.RETRY_BUS.getAddress())).choice()
		 .when(header("hasValidDemographic").isEqualTo(true))
		 .to(BridgeUtil.getEndpoint(MessageBusAddress.BIOMETRIC_BUS_IN.getAddress()))
		 .when(header("hasValidDemographic").isEqualTo(false))
		 .to(BridgeUtil.getEndpoint(MessageBusAddress.ERROR.getAddress()));

	}
}
