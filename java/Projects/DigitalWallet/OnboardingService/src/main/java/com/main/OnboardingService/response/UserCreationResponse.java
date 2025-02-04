package com.main.OnboardingService.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationResponse {

    String returnCode;
    String returnMessage;
    String name;
    String email;
    String phoneNo;
}
