package io.mosip.registration.dto;

/**
 * The Class RegistrationApprovalDTO.
 * 
 * @author Mahesh Kumar
 */
public class RegistrationApprovalDTO {

	private String id;
	private String acknowledgementFormPath;
	private String statusComment;

	/**
	 * Instantiates a new registration approval DTO.
	 *
	 * @param id 
	 * 				the id
	 * @param acknowledgementFormPath 
	 * 				the acknowledgement form path
	 * @param statusComment 
	 * 				the status comment
	 */
	public RegistrationApprovalDTO(String id, String acknowledgementFormPath, String statusComment) {
		super();
		
		this.id = id;
		this.acknowledgementFormPath =acknowledgementFormPath;
		this.statusComment = statusComment;
		}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the acknowledgement form path.
	 *
	 * @return the acknowledgementFormPath
	 */
	public String getAcknowledgementFormPath() {
		return acknowledgementFormPath;
	}

	/**
	 * Gets the status comment.
	 *
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment;
	}
}
