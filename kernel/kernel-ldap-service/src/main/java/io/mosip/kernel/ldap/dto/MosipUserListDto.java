package io.mosip.kernel.ldap.dto;

import java.util.List;


public class MosipUserListDto {
	List<MosipUserDto> mosipUserDtoList;

	public List<MosipUserDto> getMosipUserDtoList() {
		return mosipUserDtoList;
	}

	public void setMosipUserDtoList(List<MosipUserDto> mosipUserDtoList) {
		this.mosipUserDtoList = mosipUserDtoList;
	}
}
