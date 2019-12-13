package io.mosip.resident.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.RequestIdType;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.constant.VidType;
import io.mosip.resident.dto.AuthHistoryRequestDTO;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentVidRequestDto;
import io.mosip.resident.dto.VidRevokeRequestDTO;
import io.mosip.resident.exception.InvalidInputException;
import io.mosip.resident.exception.ResidentServiceException;

@Component
public class RequestValidator {

	@Autowired
	private UinValidator<String> uinValidator;

	@Autowired
	private VidValidator<String> vidValidator;

	@Autowired
	private RidValidator<String> ridValidator;

	@Value("${resident.vid.id}")
	private String id;

	@Value("${resident.revokevid.id}")
	private String revokeVidId;

	@Value("${resident.vid.version}")
	private String version;

	@Value("${resident.authlock.id}")
	private String authLockId;

	@Value("${resident.euin.id}")
	private String euinId;

	private static String reprintId;

	@Value("${resident.authhistory.id}")
	private String authHstoryId;

	@Value("${auth.types.allowed}")
	private String authTypes;

	@Value("${resident.authunlock.id}")
	private String authUnLockId;

	@Value("${mosip.id.validation.identity.phone}")
	private String phoneRegex;

	@Value("${mosip.id.validation.identity.email}")
	private String emailRegex;

	private static Map<RequestIdType, String> map;

	@Value("${resident.printuin.id}")
	public void setReprintId(String reprintId) {
		RequestValidator.reprintId = reprintId;
	}

	@PostConstruct
	public void setMap() {
		map = new HashMap<RequestIdType, String>();
		map.put(RequestIdType.RE_PRINT_ID, reprintId);
		// map.put(key, value)
	}

	public void validateVidCreateRequest(ResidentVidRequestDto requestDto) {

		if (requestDto.getId() == null || !requestDto.getId().equalsIgnoreCase(id))
			throw new InvalidInputException("id");

		if (requestDto.getVersion() == null || !requestDto.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (requestDto.getRequest() == null)
			throw new InvalidInputException("request");

		if (requestDto.getRequest().getVidType() == null
				|| (!requestDto.getRequest().getVidType().equalsIgnoreCase(VidType.PERPETUAL.name())
						&& !requestDto.getRequest().getVidType().equalsIgnoreCase(VidType.TEMPORARY.name())))
			throw new InvalidInputException("vidType");

		if (requestDto.getRequest().getIndividualIdType() == null
				|| (!requestDto.getRequest().getIndividualIdType().equalsIgnoreCase(IdType.UIN.name())
						&& !requestDto.getRequest().getIndividualIdType().equalsIgnoreCase(IdType.VID.name())))
			throw new InvalidInputException("individualIdType");
		if (!validateIndividualId(requestDto.getRequest().getIndividualId(),
				requestDto.getRequest().getIndividualIdType())) {
			throw new InvalidInputException("individualId");
		}

		if (requestDto.getRequest().getOtp() == null)
			throw new InvalidInputException("otp");

		if (requestDto.getRequest().getTransactionID() == null)
			throw new InvalidInputException("transactionId");
	}

	public void validateAuthLockOrUnlockRequest(RequestWrapper<AuthLockOrUnLockRequestDto> requestDTO,
			AuthTypeStatus authTypeStatus) {
		if (authTypeStatus.equals(AuthTypeStatus.LOCK)) {
			if (requestDTO.getId() == null || !requestDTO.getId().equalsIgnoreCase(authLockId))
				throw new InvalidInputException("id");
		} else {
			if (requestDTO.getId() == null || !requestDTO.getId().equalsIgnoreCase(authUnLockId))
				throw new InvalidInputException("id");
		}

		if (requestDTO.getVersion() == null || !requestDTO.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (requestDTO.getRequest() == null)
			throw new InvalidInputException("request");

		if ((!requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.UIN.name())
				&& !requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.VID.name())))
			throw new InvalidInputException("individualIdType");

		if (!validateIndividualId(requestDTO.getRequest().getIndividualId(),
				requestDTO.getRequest().getIndividualIdType().name())) {
			throw new InvalidInputException("individualId");
		}

		validateAuthType(requestDTO.getRequest().getAuthType());

	}

	public void validateEuinRequest(RequestWrapper<EuinRequestDTO> requestDTO) {
		if (requestDTO.getId() == null || !requestDTO.getId().equalsIgnoreCase(euinId))
			throw new InvalidInputException("id");

		if (requestDTO.getVersion() == null || !requestDTO.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (requestDTO.getRequest() == null)
			throw new InvalidInputException("request");

		if (!requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.UIN.name())
				&& !requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.VID.name()))
			throw new InvalidInputException("individualIdType");

		if (!validateIndividualId(requestDTO.getRequest().getIndividualId(),
				requestDTO.getRequest().getIndividualIdType().name())) {
			throw new InvalidInputException("individualId");
		}

	}

	public void validateAuthHistoryRequest(@Valid RequestWrapper<AuthHistoryRequestDTO> requestDTO) {
		if (requestDTO.getId() == null || !requestDTO.getId().equalsIgnoreCase(authHstoryId))
			throw new InvalidInputException("id");

		if (requestDTO.getVersion() == null || !requestDTO.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (requestDTO.getRequest() == null)
			throw new InvalidInputException("request");

		if (!requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.UIN.name())
				&& !requestDTO.getRequest().getIndividualIdType().name().equalsIgnoreCase(IdType.VID.name()))
			throw new InvalidInputException("individualIdType");

		if (!validateIndividualId(requestDTO.getRequest().getIndividualId(),
				requestDTO.getRequest().getIndividualIdType().name())) {
			throw new InvalidInputException("individualId");
		}

		if (requestDTO.getRequest().getPageFetch() == null && requestDTO.getRequest().getPageStart() != null)
			throw new InvalidInputException("please provide Page size to be Fetched");

		if (requestDTO.getRequest().getPageStart() == null && requestDTO.getRequest().getPageFetch() != null)
			throw new InvalidInputException("please provide Page numer to be Fetched");
	}

	public void validateAuthType(List<String> authType) {
		if (authType == null) {
			throw new InvalidInputException("authType");
		}
		String[] authTypesArray = authTypes.split(",");
		List<String> authTypesAllowed = new ArrayList<>(Arrays.asList(authTypesArray));
		for (String type : authType) {
			if (!authTypesAllowed.contains(type))
				throw new InvalidInputException("authType");
		}
	}

	public boolean phoneValidator(String phone) {
		return phone.matches(phoneRegex);
	}

	public boolean emailValidator(String email) {
		return email.matches(emailRegex);
	}

	private boolean validateIndividualId(String individualId, String individualIdType) {
		boolean validation = false;
		try {
			if (individualIdType.equalsIgnoreCase(IdType.UIN.toString())) {
				validation = uinValidator.validateId(individualId);
			} else if (individualIdType.equalsIgnoreCase(IdType.VID.toString())) {
				validation = vidValidator.validateId(individualId);
			} else if (individualIdType.equalsIgnoreCase(IdType.RID.toString())) {
				validation = ridValidator.validateId(individualId);
			}
		} catch (InvalidIDException e) {
			throw new InvalidInputException("individualId");
		}
		return validation;
	}

	public void validateVidRevokeRequest(RequestWrapper<VidRevokeRequestDTO> requestDto) {

		if (requestDto.getId() == null || !requestDto.getId().equalsIgnoreCase(revokeVidId))
			throw new InvalidInputException("id");

		if (requestDto.getVersion() == null || !requestDto.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (requestDto.getRequest() == null)
			throw new InvalidInputException("request");

		if (requestDto.getRequest().getVidStatus() == null)
			throw new InvalidInputException("vidStatus");

		if (requestDto.getRequest().getIndividualIdType() == null
				|| (!requestDto.getRequest().getIndividualIdType().equalsIgnoreCase(IdType.VID.name())))
			throw new InvalidInputException("individualIdType");
		else if (requestDto.getRequest().getIndividualIdType().equalsIgnoreCase(IdType.VID.name())) {
			try {
				vidValidator.validateId(requestDto.getRequest().getIndividualId());
			} catch (InvalidIDException e) {
				throw new InvalidInputException("individualId");
			}
		}

		if (requestDto.getRequest().getOtp() == null)
			throw new InvalidInputException("otp");

		if (requestDto.getRequest().getTransactionID() == null)
			throw new InvalidInputException("transactionId");
	}

	public boolean validateRequest(RequestWrapper<?> request, RequestIdType requestIdType) {
		if (!request.getId().equals(map.get(requestIdType)))
			throw new ResidentServiceException(ResidentErrorCode.INVALID_INPUT.getErrorCode(),
					ResidentErrorCode.INVALID_INPUT.getErrorMessage() + "id");
		if (!request.getVersion().equals(version))
			throw new ResidentServiceException(ResidentErrorCode.INVALID_INPUT.getErrorCode(),
					ResidentErrorCode.INVALID_INPUT.getErrorMessage() + "version");
		return true;

	}

}
