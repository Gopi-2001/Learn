package in.gopikant.billingSoftware.service;

import in.gopikant.billingSoftware.io.UserRequest;
import in.gopikant.billingSoftware.io.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}
