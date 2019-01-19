package io.mosip.kernel.batchframework.constant;

/**
 * This enum provides all the constant for batch framework.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum BatchPropertyConstant {

	BATCH_KEY_SEPARATOR(","), 
	EMPTY_STRING(" "),
	ERROR_CODE("ERROR-CODE"),
	TEMPORARY_DIRECTORY("java.io.tmpdir"),
	BATCH_JOB_FILE("/jobdescription.txt"),
	CHARSET("UTF-8"),
	LOGGER_TARGET("System.err"),
	REGISTER_JOBS("/apps"),
	REGISTER_PARAM("uri"),
	PROTOCALL("http://"),
	ADDRESS_PORT_SEPARATOR(":"),
	TASK_CREATER("/tasks/definitions"),
	TASK_LAUNCHER("/tasks/executions"),
	BLANK_SPACE(" "),
	EMPYT_LINES(""),
	TASK_CREATER_FIRST_PARAM("name"),
	TASK_CREATER_SECOND_PARAM("definition"),
	TASK_CREATER_FIRST_PARAM_VALUE("task-");

	/**
	 * The property for batch framework.
	 */
	private String property;

	/**
	 * The constructor to set batch property.
	 * 
	 * @param property
	 *            the property to set.
	 */
	private BatchPropertyConstant(String property) {
		this.property = property;
	}

	/**
	 * Getter for batch property
	 * 
	 * @return the batch property.
	 */
	public String getProperty() {
		return property;
	}

}
