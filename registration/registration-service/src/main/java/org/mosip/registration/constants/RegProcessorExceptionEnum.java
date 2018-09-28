package org.mosip.registration.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Error enum for REG Processor Module
 * 
 * @author Balaji.Sridharan
 * @since 1.0.0
 *
 */
@Getter
@AllArgsConstructor
public enum RegProcessorExceptionEnum {

	REG_SOCKET_ERROR_CODE("IDC-FRA-PAC-001", "No socket is available"),
	REG_UNKNOWN_HOST_ERROR_CODE("IDC-FRA-PAC-002", "The host is unknown"),
	REG_NO_SUCH_ALGORITHM_ERROR_CODE("IDC-FRA-PAC-003", "No such algorithm available for input"),
	REG_NO_SUCH_PADDING_ERROR_CODE("IDC-FRA-PAC-004", "No such padding available for input"),
	REG_INVALID_KEY_ERROR_CODE("IDC-FRA-PAC-005", "Invalid key for input"),
	REG_INVALID_ALGORITHM_PARAMETER_ERROR_CODE("IDC-FRA-PAC-006", "Invalid parameter for the algorithm"),
	REG_ILLEGAL_BLOCK_ERROR_CODE("IDC-FRA-PAC-007", "The block size is illegal for the input"),
	REG_BAD_PADDING_ERROR_CODE("IDC-FRA-PAC-008", "Bad padding for the input"),
	REG_INVALID_KEY_SEED_ERROR_CODE("IDC-FRA-PAC-009", "Invalid seeds for key generation"),
	REG_IO_EXCEPTION("IDC-FRA-PAC-010", "IO exception"),
	REG_JSON_PROCESSING_EXCEPTION("IDI-FRA-PAC-011", "Exception while parsing object to JSON"),
	REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE("IDC-FRA-PAC-012", "Illegal key size for key generation"),
	REG_INVALID_KEY_SPEC_ERROR_CODE("IDC-FRA-PAC-013", "Invalid key spec for input"),
	REG_FILE_NOT_FOUND_ERROR_CODE("IDC-FRA-PAC-014", "File not found for input path"),
	REG_IO_ERROR_CODE("IDC-FRA-PAC-015", "Input-output relation failed"),
	REG_CLASS_NOT_FOUND_ERROR_CODE("IDC-FRA-PAC-016", "Class not found for input"),
	REG_PACKET_CREATION_ERROR_CODE("IDC-FRA-PAC-017", "Exception while creating Registration"),
	REG_PACKET_DTAILS_INSERT_SQL_ERROR_CODE("IDC-FRA-PAC-018", "SQL Exception");

	private final String errorCode;
	private final String errorMessage;
}
