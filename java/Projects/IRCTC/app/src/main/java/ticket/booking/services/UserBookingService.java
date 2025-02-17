package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class UserBookingService {
    private User user;

    private List<User> userList;

    private static final String USER_PATH = "app/src/main/java/ticket/booking/localDb/user.json";

    // The ObjectMapper class from the Jackson library is used for the deserialization process.
    // It takes care of mapping the JSON data to the specified Java object types.
    private ObjectMapper objectMapper = new ObjectMapper();

     public UserBookingService(User user) throws IOException {
         this.user = user;

         File users = new File(USER_PATH);

         // https://dev.to/emilossola/a-comprehensive-guide-for-java-typereference-249m
         // The usage of TypeReference specifies the generic type information during deserialization
         // to ensure correct handling of the data.
         userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
     }

    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }



}
