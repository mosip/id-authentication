/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */


package org.mosip.kernel.logger.constants;

/** {@link Enum} for exception constants
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum LogExeptionCodeConstants {
	CLASSNAMENOTFOUNDEXEPTION("COK-LGM-LGM-001"), 
	EMPTYPATTERNEXCEPTION("COK-LGM-LGM-004"), 
	FILENAMENOTPROVIDED("COK-LGM-LGM-006"), 
	MOSIPILLEGALARGUMENTEXCEPTION("COK-LGM-LGM-007"),
	MOSIPILLEGALFILEACCESS("COK-LGM-LGM-008"),
	MOSIPILLEGALSTATEEXCEPTION("COK-LGM-LGM-003"), 
	IMPLEMENTATIONNOTFOUND("COK-LGM-LGM-002"), 
	PATTERNSYNTAXEXCEPTION("COK-LGM-LGM-005"),
	CLASSNAMENOTFOUNDEXEPTIONMESSAGE("Class name is empty"), 
	EMPTYPATTERNEXCEPTIONMESSAGEEMPTY("File name pattern is empty"),
	EMPTYPATTERNEXCEPTIONMESSAGENULL("File name pattern is null"),
	MOSIPILLEGALSTATEEXCEPTIONMESSAGE("FileNamePattern does not contain a valid DateToken"),
	MOSIPILLEGALARGUMENTEXCEPTIONMESSAGE("String value of size is not in expected format"),
	FILENAMENOTPROVIDEDMESSAGEEMPTY("File name is empty"),
	FILENAMENOTPROVIDEDMESSAGENULL("File name is null"),
	IMPLEMENTATIONNOTFOUNDMESSAGE("Log Implementation not found"), 
	MOSIPILLEGALFILEACCESSMESSAGE("File location not accessible"),
	PATTERNSYNTAXEXCEPTIONMESSAGED("Pattern should contain %d{SimpleDateFormat}"),
	PATTERNSYNTAXEXCEPTIONMESSAGEI("Pattern should contain %i"),
	PATTERNSYNTAXEXCEPTIONMESSAGENOTI("Pattern should not contain %i");
	
	/** Value of exception constants
	 *  constant {@link Enum} value
	 */
	private final String value;

	/** Constructor for this class
	 * @param value set {@link Enum} value 
	 */
	private LogExeptionCodeConstants(final String value) {
		this.value = value;
	}

	/** Getter for value
	 * @return get {@link Enum} value
	 */
	public String getValue() {
		return value;
	}

}
