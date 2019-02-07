package io.mosip.kernel.auth.dto;

import java.util.List;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class MosipUserListDto {
    
    private List<MosipUserDto> mosipUserDtoList;

    public List<MosipUserDto> getMosipUserDtoList() {
          return mosipUserDtoList;
    }

    public void setMosipUserDtoList(List<MosipUserDto> mosipUserDtoList) {
          this.mosipUserDtoList = mosipUserDtoList;
    }

    
}

