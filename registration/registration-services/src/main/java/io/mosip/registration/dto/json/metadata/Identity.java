package io.mosip.registration.dto.json.metadata;

import java.util.List;

import io.mosip.registration.dto.demographic.ValuesDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Getter
@Setter
public class Identity {

	private Biometric biometric;
	private List<BiometricException> exceptionBiometrics;
	private Photograph applicantPhotograph;
	private ExceptionPhotograph exceptionPhotograph;
	private List<Document> documents;
	private List<FieldValue> metaData;
	private List<FieldValue> osiData;
	private List<FieldValueArray> hashSequence1;
	private List<FieldValueArray> hashSequence2;
	private List<FieldValue> capturedRegisteredDevices;
	private List<FieldValue> capturedNonRegisteredDevices;
	private List<FieldValue> checkSum;
	private List<ValuesDTO> printingName;
	}
