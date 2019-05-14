package io.mosip.kernel.core.bioapi.model;

import lombok.Data;

/**
 * The Class BiometricRecord.
 * 
 * @author Sanjay Murali
 */
@Data
public class BiometricRecord {
	private VersionType version ;
	private VersionType cbeffVersion ;
	private Object [] other ;
	private BIRInfo birInfo ;
	private BDBInfo bdbInfo ;
	private SBInfo sbInfo ;
	private String bdb ; // Base64 Binary
	private String sb ; // Base64 Binary
	private String extractedTemplate; //Base64 Binary
}
