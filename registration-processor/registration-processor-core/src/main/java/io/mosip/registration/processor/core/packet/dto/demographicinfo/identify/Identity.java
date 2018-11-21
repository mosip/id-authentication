package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class Identity {
    private String middleName;

    private String lastName;

    private String dob;

    private String addressLine6;

    private String gender;

    private String addressLine5;

    private String addressLine4;

    private String fullName;

    private String addressLine3;

    private String firstName;

    private String addressLine2;

    private String addressLine1;
    
    private String pincode;
}
