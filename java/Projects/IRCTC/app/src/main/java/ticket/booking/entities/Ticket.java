package ticket.booking.entities;

import java.util.*;

public class Ticket {

    private String ticketId;

    private String userId;

    private String source;

    private String destination;

    private String dateOfTravel;

    private Train train;

    public Ticket(){}

    public Ticket(String ticketId,String userId,String source,String destination,String dateOfTravel,Train train) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }


    public String getTicketInfo() {
        return String.format("Ticket ID: %s belongs to User %s, travelling from %s to %s on %s",
                ticketId,userId,source,destination,dateOfTravel);
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDateOfTravel(String dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }

    public String getTicketId(){
        return this.ticketId;
    }
    public String getUserId(){
        return this.userId;
    }
    public String getSource(){
        return this.source;
    }
    public String getDestination(){
        return this.destination;
    }
    public Train getTrain(){
        return this.train;
    }
    public String getDateOfTravel(){
        return this.dateOfTravel;
    }

}
