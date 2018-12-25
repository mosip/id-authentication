package io.mosip.preregistration.documents.service.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.Entity;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.documents.code.RequestCodes;
import io.mosip.preregistration.documents.dto.DocumentDTO;
import io.mosip.preregistration.documents.dto.UploadRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;


@Component
public class DocumentServiceUtil {
	
	public Map<String, String> prepareRequestParamMap( UploadRequestDTO<DocumentDTO> docReqDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), docReqDto.getId());
		inputValidation.put(RequestCodes.ver.toString(), docReqDto.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(docReqDto.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(),docReqDto.getRequest().toString());
		return inputValidation;
	}
	
	public UploadRequestDTO<DocumentDTO> createUploadDto(String documentJsonString) throws JSONException, JsonParseException, JsonMappingException, IOException, ParseException{
		UploadRequestDTO<DocumentDTO> uploadReqDto= new UploadRequestDTO<>();
		JSONObject documentData = new JSONObject(documentJsonString);
		JSONObject docDTOData = (JSONObject) documentData.get("request");
		DocumentDTO documentDto = (DocumentDTO) JsonUtils.jsonStringToJavaObject(DocumentDTO.class,
				docDTOData.toString());
		uploadReqDto.setId(documentData.get("id").toString());
		uploadReqDto.setVer(documentData.get("ver").toString());
		uploadReqDto.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(documentData.get("reqTime").toString()));
		uploadReqDto.setRequest(documentDto);
		return uploadReqDto;
	}
	
	public DocumentEntity dtoToEntity(DocumentDTO dto) {
		DocumentEntity documentEntity= new DocumentEntity();
		documentEntity.setPreregId(dto.getPrereg_id());
		documentEntity.setDocCatCode(dto.getDoc_cat_code());
		documentEntity.setDocTypeCode(dto.getDoc_typ_code());
		documentEntity.setDocFileFormat(dto.getDoc_file_format());
		documentEntity.setStatusCode(dto.getStatus_code());
		documentEntity.setCrDtime(new Timestamp(System.currentTimeMillis()));
		documentEntity.setUpdBy(dto.getUpd_by());
		documentEntity.setUpdDtime(new Timestamp(System.currentTimeMillis()));
		return documentEntity;
	}
	
	public DocumentDTO EntityToDto(DocumentEntity entity) {
		return null;
	}

}
