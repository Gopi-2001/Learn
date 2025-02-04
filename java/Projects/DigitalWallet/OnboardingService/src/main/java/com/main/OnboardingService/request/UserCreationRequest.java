package com.main.OnboardingService.request;

import com.main.OnboardingService.model.UserIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreationRequest {

    String name;
    String email;
    String phone;
    String password;
    UserIdentifier userIdentifier;
    String userIdentifierValue;
    String dob;
    String address;

}
