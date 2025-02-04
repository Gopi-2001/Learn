package com.main.OnboardingService.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.main.OnboardingService.model.User;
import com.main.OnboardingService.model.UserRole;
import com.main.OnboardingService.model.UserStatus;
import com.main.OnboardingService.reporistory.UserRepository;
import com.main.OnboardingService.request.UserCreationRequest;
import com.main.common.example.CommonConstants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public User onboardNewUser(UserCreationRequest userCreationRequest) {
        User user = User.builder().name(userCreationRequest.getName())
                .email(userCreationRequest.getEmail())
                .phone(userCreationRequest.getPhone())
                .userIdentifier(userCreationRequest.getUserIdentifier())
                .userIdentifierValue(userCreationRequest.getUserIdentifierValue())
                .role(UserRole.NORMAL)
                .userStatus(UserStatus.ACTIVE)
                .dob(userCreationRequest.getDob())
                .build();

        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        User savedUser = userRepository.save(user);

        JSONObject userDetails = new JSONObject();
        userDetails.put(CommonConstants.USER_NAME,user.getName());
        userDetails.put(CommonConstants.USER_EMAIL,user.getEmail());
        userDetails.put(CommonConstants.USER_MOBILE,user.getPhone());
        userDetails.put(CommonConstants.USER_IDENTIFIER,user.getUserIdentifier());
        userDetails.put(CommonConstants.USER_IDENTIFIER_VALUE,user.getUserIdentifierValue());

        kafkaTemplate.send(CommonConstants.USER_DETAILS_TOPIC,userDetails.toString());

        return savedUser;
    }
}
