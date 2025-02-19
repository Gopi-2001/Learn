package ticket.booking.entities;

import java.util.*;

public class User {

    private String name;

    private String password;

    private String hashedPassword;

    private List<Ticket> ticketsBooked;

    private String userId;

    public User() {}

    public User(String name,String password,String hashedPassword,List<Ticket> ticketsBooked,String userId){
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked;
    }

    public void printTicket(){
        for(Ticket t : ticketsBooked){
            System.out.println(t.getTicketInfo());
        }
    }
    public List<Ticket> getTicketsBooked(){
        return this.ticketsBooked;
    }
    public String getUserId() {
        return  this.userId;
    }
    public String getName() {
        return this.name;
    }
    public String getHashedPassword() {
        return this.hashedPassword;
    }
    public String getPassword() {
        return this.password;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setHashedPassword(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setTicketsBooked(List<Ticket> ticketsBooked){
        this.ticketsBooked = ticketsBooked;
    }

}
