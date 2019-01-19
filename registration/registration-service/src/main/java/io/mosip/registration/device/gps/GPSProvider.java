package io.mosip.registration.device.gps;

import org.springframework.stereotype.Component;

import io.mosip.registration.device.gps.impl.GPSBU343Connector.GPSPosition;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * class for gps response parsing and getting latitude,longitude from gps
 * device.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
@Component
public abstract class GPSProvider implements MosipGPSProvider {

	@Override
	public abstract String getComPortGPSData(String comPortNo, int portReadWaitTime) throws RegBaseCheckedException;

	@Override
	public abstract boolean parse(String[] tokens, GPSPosition position);

	@Override
	public abstract GPSPosition signlaParser(String line) throws RegBaseCheckedException;
}
