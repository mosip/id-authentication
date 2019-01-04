package io.mosip.authentication.service.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.service.factory.IDAMappingFactory;
import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class IDAMappingConfig.
 *
 * @author Dinesh Karuppiah.T
 */

@Configuration
@PropertySource(value = "classpath:ida-mapping.json", factory = IDAMappingFactory.class)
@ConfigurationProperties

/**
 * Instantiates a new IDA mapping config.
 */
@Data
public class IDAMappingConfig implements MappingConfig {

	/** The name. */
	private List<String> name;
	
	/** The dob. */
	private List<String> dob;
	
	/** The dob type. */
	private List<String> dobType;
	
	/** The age. */
	private List<String> age;
	
	/** The gender. */
	private List<String> gender;
	
	/** The phone number. */
	private List<String> phoneNumber;
	
	/** The email id. */
	private List<String> emailId;
	
	/** The address line 1. */
	private List<String> addressLine1;
	
	/** The address line 2. */
	private List<String> addressLine2;
	
	/** The address line 3. */
	private List<String> addressLine3;
	
	/** The location 1. */
	private List<String> location1;
	
	/** The location 2. */
	private List<String> location2;
	
	/** The location 3. */
	private List<String> location3;
	
	/** The pin code. */
	private List<String> pinCode;
	
	/** The full address. */
	private List<String> fullAddress;
	
	/** The otp. */
	private List<String> otp;
	
	/** The pin. */
	private List<String> pin;
	
	/** The left index. */
	private List<String> leftIndex;
	
	/** The left little. */
	private List<String> leftLittle;
	
	/** The left middle. */
	private List<String> leftMiddle;
	
	/** The left ring. */
	private List<String> leftRing;
	
	/** The left thumb. */
	private List<String> leftThumb;
	
	/** The right index. */
	private List<String> rightIndex;
	
	/** The right little. */
	private List<String> rightLittle;
	
	/** The right middle. */
	private List<String> rightMiddle;
	
	/** The right ring. */
	private List<String> rightRing;
	
	/** The right thumb. */
	private List<String> rightThumb;
	
	/** The left eye. */
	private List<String> leftEye;
	
	/** The right eye. */
	private List<String> rightEye;
	
	/** The iris. */
	private List<String> iris;
	
	/** The face. */
	private List<String> face;
	
	/** The fingerprint. */
	private List<String> fingerprint;

}
