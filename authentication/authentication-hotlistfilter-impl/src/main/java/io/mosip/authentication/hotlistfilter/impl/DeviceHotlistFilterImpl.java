package io.mosip.authentication.hotlistfilter.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistIdTypes;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;

/**
 * The Class DeviceHotlistFilterImpl - implementation of auth filter for
 * validating hotlisted devices in the authentication request.
 * 
 * @author Loganathan Sekar
 */
public class DeviceHotlistFilterImpl implements IMosipAuthFilter {
	
	/** The hotlist service. */
	@Autowired
	private HotlistService hotlistService;	

	/**
	 * Inits the.
	 */
	public void init() throws IdAuthenticationFilterException {
	}

	/**
	 * Test method that executes predicate test condition on the given arguments
	 *
	 * @param authRequest  the auth request
	 * @param identityData the identity data
	 * @param properties   the properties
	 * @throws IdAuthenticationBusinessException 
	 */
	public void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData,
			Map<String, Object> properties) throws IdAuthenticationFilterException {
		validateHotlistedIds(authRequest);
	}
	
	/**
	 * Checks if is devices hotlisted.
	 *
	 * @param biometrics the biometrics
	 * @param errors     the errors
	 * @throws IdAuthenticationFilterException 
	 */
    private void isDevicesHotlisted(List<BioIdentityInfoDTO> biometrics) throws IdAuthenticationFilterException {
        if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {

            System.out.println("Total biometrics received: " + biometrics.size());

            OptionalInt indexOpt = IntStream.range(0, biometrics.size()).filter(index -> {
                BioIdentityInfoDTO bioInfo = biometrics.get(index);

                // Extract Digital ID details
                String serialNo = bioInfo.getData().getDigitalId().getSerialNo();
                String make = bioInfo.getData().getDigitalId().getMake();
                String model = bioInfo.getData().getDigitalId().getModel();

                System.out.println("Checking biometric index: " + index);
                System.out.println("Serial No: " + serialNo);
                System.out.println("Make: " + make);
                System.out.println("Model: " + model);

                String concatData = serialNo.concat(make).concat(model);
                System.out.println("Concatenated data: " + concatData);

                String hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(concatData.getBytes());
                System.out.println("Generated hash: " + hash);

                HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(hash, HotlistIdTypes.DEVICE);

                System.out.println("Hotlist status for index " + index + ": " + hotlistStatus.getStatus());

                return hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED);
            }).findAny();

            if (indexOpt.isPresent()) {
                System.out.println("Blocked device found at index: " + indexOpt.getAsInt());
                throw new IdAuthenticationFilterException(
                        IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
                                String.format(BIO_PATH, indexOpt.getAsInt(), HotlistIdTypes.DEVICE))
                );
            } else {
                System.out.println("No blocked devices found.");
            }
        } else {
            System.out.println("Biometrics list is null or empty.");
        }
    }


    /**
	 * Validate hotlisted ids.
	 *
	 * @param errors         the errors
	 * @param authRequestDto the auth request dto
	 * @throws IdAuthenticationFilterException 
	 */
    private void validateHotlistedIds(AuthRequestDTO authRequestDto) throws IdAuthenticationFilterException {
        System.out.println("---- validateHotlistedIds() called ----");

        if (authRequestDto == null) {
            System.out.println("AuthRequestDTO is null!");
            return;
        }

        System.out.println("AuthRequestDTO received: " + authRequestDto);
        System.out.println("Checking if authentication type is biometric...");

        boolean isBio = AuthTypeUtil.isBio(authRequestDto);
        System.out.println("Is Bio Authentication: " + isBio);

        if (isBio) {
            if (authRequestDto.getRequest() == null) {
                System.out.println("AuthRequestDTO.getRequest() is null!");
                return;
            }

            List<BioIdentityInfoDTO> biometrics = authRequestDto.getRequest().getBiometrics();
            System.out.println("Biometrics data retrieved: " + biometrics);

            if (biometrics == null || biometrics.isEmpty()) {
                System.out.println("No biometrics found in request.");
            } else {
                System.out.println("Calling isDevicesHotlisted() with " + biometrics.size() + " biometric entries...");
                isDevicesHotlisted(biometrics);
            }
        } else {
            System.out.println("Request is not biometric type. Skipping device hotlist check.");
        }

        System.out.println("---- validateHotlistedIds() completed ----");
    }


}
