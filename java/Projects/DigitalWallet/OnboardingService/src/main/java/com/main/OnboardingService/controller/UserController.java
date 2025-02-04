package com.main.OnboardingService.controller;

import com.main.OnboardingService.model.User;
import com.main.OnboardingService.request.UserCreationRequest;
import com.main.OnboardingService.response.UserCreationResponse;
import com.main.OnboardingService.service.UserService;
import com.main.OnboardingService.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding-service")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/onboard/user")
    public ResponseEntity<UserCreationResponse> createUser(@RequestBody UserCreationRequest userCreationRequest) {
        UserCreationResponse userCreationResponse = new UserCreationResponse();
        if(userCreationRequest==null){
            userCreationResponse.setReturnCode("06"); //return U
            userCreationResponse.setReturnMessage("Invalid Request");//userCreationResponse
            return new ResponseEntity<>(userCreationResponse, HttpStatus.BAD_REQUEST);
        }

        if(StringUtils.isBlank(userCreationRequest.getEmail() )|| StringUtils.isBlank(userCreationRequest.getPhone())) {
            if (StringUtils.isBlank(userCreationRequest.getEmail())) {
                userCreationResponse.setReturnCode("07");
                userCreationResponse.setReturnMessage("Email is required");
            }
            if (StringUtils.isBlank(userCreationRequest.getPhone())) {
                userCreationResponse.setReturnCode("08");
                userCreationResponse.setReturnMessage("Phone Number is required");
            }
            return new ResponseEntity<>(userCreationResponse, HttpStatus.BAD_REQUEST);//return U
        }

        User user = userService.onboardNewUser(userCreationRequest);

        if(user==null){
            userCreationResponse.setReturnCode("11");
            userCreationResponse.setReturnMessage("Something went wrong!! User not created");
            return new ResponseEntity<>(userCreationResponse,HttpStatus.OK);
        } else {
            userCreationResponse.setReturnCode("00");
            userCreationResponse.setReturnMessage("User Onboarded Successfully");
            userCreationResponse.setName(user.getName());
            userCreationResponse.setEmail(user.getEmail());
            userCreationResponse.setPhoneNo(user.getPhone());
        }

        return new ResponseEntity<>(userCreationResponse,HttpStatus.CREATED);
    }
}
