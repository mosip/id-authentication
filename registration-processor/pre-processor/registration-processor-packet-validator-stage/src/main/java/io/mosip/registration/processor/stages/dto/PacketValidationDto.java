package io.mosip.registration.processor.stages.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class PacketValidationDto {
	private boolean isSchemaValidated = false;
	private boolean isCheckSumValidated = false;
	private boolean isApplicantDocumentValidation = false;
	private boolean isFilesValidated = false;
	private boolean isMasterDataValidation = false;
	private boolean isMandatoryValidation = false;
	private boolean isRIdAndTypeSynched = false;
	private boolean isTransactionSuccessful;
	private String packetValidaionFailure="";
	private String packetValidatonStatusCode="";
}
