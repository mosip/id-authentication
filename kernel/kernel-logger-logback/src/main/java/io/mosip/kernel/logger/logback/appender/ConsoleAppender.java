/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.logger.logback.appender;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.mosip.kernel.logger.logback.constant.ConfigurationDefault;

/**
 * Console appender for Mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@XmlRootElement
public class ConsoleAppender {

	/**
	 * Name of the appender
	 */
	private String appenderName;
	/**
	 * It ensures that logging events are immediately written out; with default
	 * true
	 */
	private boolean immediateFlush = ConfigurationDefault.DEFAULTIMMEDIATEFLUSH;
	/**
	 * Target of Logging either System.out or System.err; default target is
	 * System.out.
	 */
	private String target = ConfigurationDefault.DEFAULTARGET;

	/**
	 * Getter for appenderName
	 * 
	 * @return name of current Appender
	 */
	public String getAppenderName() {
		return appenderName;
	}

	/**
	 * Setter for appenderName
	 * 
	 * @param appenderName
	 *            name of the Appender
	 */
	@XmlAttribute
	public void setAppenderName(String appenderName) {
		this.appenderName = appenderName;
	}

	/**
	 * Getter for immediateFlush
	 * 
	 * @return current immediateFlush value
	 */
	public boolean isImmediateFlush() {
		return immediateFlush;
	}

	/**
	 * Setter for immediateFlush
	 * 
	 * @param immediateFlush
	 *            it ensures that logging events are immediately written out;
	 *            with default true
	 */
	@XmlElement
	public void setImmediateFlush(boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
	}

	/**
	 * Getter for target
	 * 
	 * @return Current target of logging
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Setter for target
	 * 
	 * @param target
	 *            target of Logging either System.out or System.err; default
	 *            target is System.out.
	 */
	@XmlElement
	public void setTarget(String target) {
		this.target = target;
	}

}