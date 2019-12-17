package io.mosip.resident.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
import io.mosip.resident.constant.VidType;
import io.mosip.resident.dto.AuthHistoryRequestDTO;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.BaseRequestDTO;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentVidRequestDto;
import io.mosip.resident.dto.VidRevokeRequestDTO;
import io.mosip.resident.exception.InvalidInputException;

@Component
public class RequestValidator {

	@Autowired
	private UinValidator<String> uinValidator;

	@Autowired
	private VidValidator<String> vidValidator;

	@Autowired
	private RidValidator<String> ridValidator;

	private String euinId;

	private String reprintId;

	private String authUnLockId;

	private String authHstoryId;

	private String authLockId;
	
	private String uinUpdateId;

	@Value("${resident.updateuin.id}")
	public void setUinUpdateId(String uinUpdateId) {
		this.uinUpdateId = uinUpdateId;
	}

	@Value("${resident.vid.id}")
	private String id;

	@Value("${resident.revokevid.id}")
	private String revokeVidId;

	@Value("${resident.vid.version}")
	private String version;

	@Value("${resident.authlock.id}")
	public void setAuthLockId(String authLockId) {
		this.authLockId = authLockId;
	}

	@Value("${resident.euin.id}")
	public void setEuinIdString(String euinId) {
		this.euinId = euinId;
	}

	@Value("${resident.authhistory.id}")
	public void setAuthHstoryId(String authHstoryId) {
		this.authHstoryId = authHstoryId;
	}

	@Value("${auth.types.allowed}")
	private String authTypes;

	@Value("${resident.authunlock.id}")
	public void setAuthUnlockId(String authUnLockId) {
		this.authUnLockId = authUnLockId;
	}

	@Value("${mosip.id.validation.identity.phone}")
	private String phoneRegex;

	@Value("${mosip.id.validation.identity.email}")
	private String emailRegex;

	private Map<RequestIdType, String> map;

	@Value("${resident.printuin.id}")
	public void setReprintId(String reprintId) {
		this.reprintId = reprintId;
	}

	@PostConstruct
	public void setMap() {
		map = new EnumMap<>(RequestIdType.class);
		map.put(RequestIdType.RE_PRINT_ID, reprintId);
		map.put(RequestIdType.AUTH_LOCK_ID, authLockId);
		map.put(RequestIdType.AUTH_UNLOCK_ID, authUnLockId);
		map.put(RequestIdType.E_UIN_ID, euinId);
		map.put(RequestIdType.AUTH_HISTORY_ID, authHstoryId);
		map.put(RequestIdType.RES_UPDATE, uinUpdateId);

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
		validateAuthorUnlockId(requestDTO, authTypeStatus);

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

	private void validateAuthorUnlockId(RequestWrapper<AuthLockOrUnLockRequestDto> requestDTO,
			AuthTypeStatus authTypeStatus) {
		if (authTypeStatus.equals(AuthTypeStatus.LOCK)) {
			validateRequest(requestDTO, RequestIdType.AUTH_LOCK_ID);
		} else {
			validateRequest(requestDTO, RequestIdType.AUTH_UNLOCK_ID);
		}
	}

	public void validateEuinRequest(RequestWrapper<EuinRequestDTO> requestDTO) {
		validateRequest(requestDTO, RequestIdType.E_UIN_ID);

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
		validateRequest(requestDTO, RequestIdType.AUTH_HISTORY_ID);

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

		validateRequestWrapper(requestDto);

		if (requestDto.getRequest().getVidStatus() == null)
			throw new InvalidInputException("vidStatus");

		if (requestDto.getRequest().getIndividualIdType() == null
				|| (!requestDto.getRequest().getIndividualIdType().equalsIgnoreCase(IdType.VID.name())))
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

	public void validateRequestWrapper(RequestWrapper<?> request) {

		if (request.getId() == null || !request.getId().equalsIgnoreCase(revokeVidId))
			throw new InvalidInputException("id");

		if (request.getVersion() == null || !request.getVersion().equalsIgnoreCase(version))
			throw new InvalidInputException("version");

		if (request.getRequest() == null)
			throw new InvalidInputException("request");
	}

	public boolean validateRequest(RequestWrapper<?> request, RequestIdType requestIdType) {
		if (request.getId() == null || request.getId().isEmpty())
			throw new InvalidInputException("id");
		if (request.getVersion() == null || request.getVersion().isEmpty())
			throw new InvalidInputException("version");
		if (!request.getId().equals(map.get(requestIdType)))
			throw new InvalidInputException("id");
		if (!request.getVersion().equals(version))
			throw new InvalidInputException("version");
		return true;

	}

	public boolean validateRequest(BaseRequestDTO request, RequestIdType requestIdType) {
		if (request.getId() == null || request.getId().isEmpty())
			throw new InvalidInputException("id");
		if (request.getVersion() == null || request.getVersion().isEmpty())
			throw new InvalidInputException("version");
		if (!request.getId().equals(map.get(requestIdType)))
			throw new InvalidInputException("id");
		if (!request.getVersion().equals(version))
			throw new InvalidInputException("version");
		return true;

	}

}
