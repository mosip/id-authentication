package io.mosip.authentication.internal.service.dto;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Component("authorizedRoles")
@ConfigurationProperties(prefix = "mosip.role.idauth")
@Getter
@Setter
public class AuthorizedRolesDto {


    private List<String> postauth;
    
    private List<String> postverifyidentity;

    private List<String> getauthtransactionsindividualid;
	
	private List<String> postotp;
	
}	