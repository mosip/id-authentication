package io.mosip.authentication.fw.dto;

/**
 * Dto to hold the output validation results for expected and actual json
 * 
 * @author Vignesh
 *
 */
public class OutputValidationDto {
	
	private String fieldName;
	private String fiedlHierarchy;
	private String actualValue;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFiedlHierarchy() {
		return fiedlHierarchy;
	}
	public void setFiedlHierarchy(String fiedlHierarchy) {
		this.fiedlHierarchy = fiedlHierarchy;
	}
	public String getActualValue() {
		return actualValue;
	}
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	public String getExpValue() {
		return expValue;
	}
	public void setExpValue(String expValue) {
		this.expValue = expValue;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String expValue;
	private String status;
}
