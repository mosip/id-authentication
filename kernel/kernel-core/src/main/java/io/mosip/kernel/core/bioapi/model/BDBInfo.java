package io.mosip.kernel.core.bioapi.model;

import java.util.Date;

import lombok.Data;

/**
 * The Class BDBInfo.
 * 
 * @author Sanjay Murali
 */
@Data
public class BDBInfo {
	private String challengeResponse ; // Base64 Binary
	private String index ; // UUID with pattern [a-fA-F0-9]{8}\-([a-fA-F0-9]{4}\-){3}[a-fA-F0-9]{12}
	private RegistryInfo format ;
	private Boolean encryption ;
	private Date creationDate ;
	private Date notValidBefore ;
	private Date notValidAfter ;
	private BiometricType [] type ;
	private BiometricSubType subtype ;
	private ProcessedLevelType level ;
	private RegistryInfo product ;
	private RegistryInfo captureDevice ;
	private RegistryInfo featureExtractionAlgorithm ;
	private RegistryInfo comparisonAlgorithm ;
	private RegistryInfo compressionAlgorithm ;
	private PurposeType purpose ;
	private QualityType quality ;
}
