package io.mosip.registration.dto;

/**
 * The Class RegistrationApprovalDTO.
 * 
 * @author Mahesh Kumar
 */
public class RegistrationApprovalDTO {

	private String slno;
	private String id;
	private String date;
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
	public RegistrationApprovalDTO(String slno, String id, String date, String acknowledgementFormPath, String statusComment) {
		super();
		
		this.slno = slno;
		this.id = id;		
		this.date = date;
		this.acknowledgementFormPath =acknowledgementFormPath;
		this.statusComment = statusComment;
		}

	/**
	 * @return the slno
	 */
	public String getSlno() {
		return slno;
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
	 * @return the date
	 */
	public String getDate() {
		return date;
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
