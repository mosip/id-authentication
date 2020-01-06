package io.mosip.registration.processor.core.queue.impl;

import java.io.IOException;

import org.apache.activemq.transport.TransportListener;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
/**
 * 
 * @author Girish Yarru
 *
 */
public class TransportExceptionListener implements TransportListener{
    private static Logger regProcLogger = RegProcessorLogger.getLogger(TransportExceptionListener.class);
    private static final String LINE_SEPERATOR = "----------------";


	@Override
	public void onCommand(Object command) {

	}

	@Override
	public void onException(IOException error) {
		regProcLogger.error(LINE_SEPERATOR, LINE_SEPERATOR, " Transport Exception ", ExceptionUtils.getStackTrace(error));

		
	}

	@Override
	public void transportInterupted() {
		regProcLogger.debug(LINE_SEPERATOR, LINE_SEPERATOR, " Transport Interrupted - ActiveMq is down ", LINE_SEPERATOR);

	}

	@Override
	public void transportResumed() {
		regProcLogger.debug(LINE_SEPERATOR, LINE_SEPERATOR, " Transport resumed - ActiveMq is Up ", LINE_SEPERATOR);
	}

}
