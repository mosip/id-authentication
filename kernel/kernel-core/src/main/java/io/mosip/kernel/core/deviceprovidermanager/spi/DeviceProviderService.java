package io.mosip.kernel.core.deviceprovidermanager.spi;

/**
 * @author M1046464
 *
 * @param <T> - 
 * @param <D>
 * @param <S>
 * @param <U>
 */
public interface DeviceProviderService<T,Q,D, S, U> {

	/**
	 * Validate device providers.
	 *
	 * @param validateDeviceDto
	 *            the validate device dto
	 * @return {@link ResponseDto}
	 */
	public T validateDeviceProviders(Q validateDeviceDto);

	/**
	 * Validate device provider history.
	 *
	 * @param validateDeviceDto
	 *            the validate device dto
	 * @return {@link ResponseDto} the response dto
	 */
	public T validateDeviceProviderHistory(D validateDeviceHistoryDto);

	/**
	 * Method to create Device Provider
	 * 
	 * @param dto
	 *            Device Provider dto from user
	 * @return DeviceProviderExtnDto device Provider dto which has created
	 */
	public U createDeviceProvider(S dto);

	/**
	 * Method to update Device Provider
	 * 
	 * @param dto
	 *            Device Provider dto from user
	 * @return DeviceProviderExtnDto device Provider dto which has updated
	 */
	public U updateDeviceProvider(S dto);
}
