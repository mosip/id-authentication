package io.mosip.resident.service;

import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResponseDTO;

public interface ResidentService {


	public ResponseDTO getRidStatus(RequestDTO dto);
	
	public ResponseDTO reqEuin(RequestDTO dto);
	
	public ResponseDTO reqPrintUin(RequestDTO dto);
	
	public ResponseDTO reqUin(RequestDTO dto);
	
	public ResponseDTO reqRid(RequestDTO dto);
	
	public ResponseDTO reqUpdateUin(RequestDTO dto);
	
	public ResponseDTO generatVid(RequestDTO dto);
	
	public ResponseDTO revokeVid(RequestDTO dto);
	
	public ResponseDTO reqAauthLock(RequestDTO dto);
	
	public ResponseDTO reqAuthUnlock(RequestDTO dto);
	
	public ResponseDTO reqAuthHistory(RequestDTO dto);
	
	
	

}
