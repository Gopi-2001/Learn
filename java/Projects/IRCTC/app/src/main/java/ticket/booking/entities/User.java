package ticket.booking.entities;

import java.util.*;

public class User {

    private String name;

    private String password;

    private String hashedPassword;

    private List<Ticket> ticketsBooked;

    private String userId;

    public String getName() {
        return this.name;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }


    public String getPassword() {
        return this.password;
    }
}
