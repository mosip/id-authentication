package io.mosip.resident.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.IdRepoResponseDto;
import io.mosip.resident.dto.VidGeneratorResponseDto;
import io.mosip.resident.exception.IdRepoAppException;

public class Utilitiy {

	@Autowired
	private ResidentServiceRestClient residentServiceRestClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	public JSONObject retrieveIdrepoJsonByUIN(String id, IdType idType) throws IOException, Exception {

		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(id);
		IdRepoResponseDto idRepoResponseDto = null;
		if (IdType.UIN.equals(idType))
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		else if (IdType.RID.equals(idType))
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYRID,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		else if (IdType.VID.equals(idType)) {
			ResponseWrapper<VidGeneratorResponseDto> response = (ResponseWrapper<VidGeneratorResponseDto>) residentServiceRestClient
					.getApi(ApiName.GETUINBYVID, pathsegments, null, null, ResponseWrapper.class,
							tokenGenerator.getToken());
			if (response == null)
				return null;
			if (!response.getErrors().isEmpty()) {
				List<ServiceError> error = response.getErrors();
				throw new IdRepoAppException(error.get(0).getMessage());
			}

			String uin = response.getResponse().getUIN();
			pathsegments.clear();
			pathsegments.add(uin);
			idRepoResponseDto = (IdRepoResponseDto) residentServiceRestClient.getApi(ApiName.IDREPOGETIDBYUIN,
					pathsegments, null, null, IdRepoResponseDto.class, tokenGenerator.getToken());
		}
		if (idRepoResponseDto == null)
			return null;
		if (!idRepoResponseDto.getErrors().isEmpty()) {
			List<ServiceError> error = idRepoResponseDto.getErrors();
			throw new IdRepoAppException(error.get(0).getMessage());
		}
		idRepoResponseDto.getResponse().getIdentity();
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.convertValue(idRepoResponseDto.getResponse().getIdentity(), JSONObject.class);

	}

}
