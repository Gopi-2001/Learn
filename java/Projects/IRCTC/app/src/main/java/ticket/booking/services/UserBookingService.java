package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class UserBookingService {
    private User user;

    private List<User> userList;

    private static final String USER_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    // The ObjectMapper class from the Jackson library is used for the deserialization process.
    // It takes care of mapping the JSON data to the specified Java object types.
    // Json -> Object(User) --> Deserialization
    // Object(User) -> Json --> Serialization
    private ObjectMapper objectMapper = new ObjectMapper();

     public UserBookingService(User user) throws IOException {
         this.user = user;
         userList = loadUsers();
     }

     public UserBookingService() throws IOException{
         userList = loadUsers();
     }

     public List<User> loadUsers() throws IOException {
         File usersFile = new File(USER_PATH);
         // https://dev.to/emilossola/a-comprehensive-guide-for-java-typereference-249m
         // The usage of TypeReference specifies the generic type information during deserialization
         // to ensure correct handling of the data.
         return objectMapper.readValue(usersFile,new TypeReference<List<User>>(){});
     }

    public Boolean loginUser() {
        return findUser().isPresent();
    }

    public Boolean signup(User user){
        try {
            userList.add(user);
            saveUserListToFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void saveUserListToFile() throws IOException{
        File usersFile = new File(USER_PATH);
        objectMapper.writeValue(usersFile,userList);
    }

    public void fetchBookings(){
         Optional<User> foundUser = findUser();
         if(foundUser.isPresent()){
            foundUser.get().printTicket();
        }
    }

    public Boolean cancelBooking(){

         Scanner sc = new Scanner(System.in);
         System.out.println("Please Enter Ticket ID to cancel");
         String ticketId = sc.next();

         if(ticketId==null || ticketId.isEmpty()){
             System.out.println("Ticket Id cannot be null or Empty");
             return Boolean.FALSE;
         }
         Boolean isRemoved = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));
         if(isRemoved){
                System.out.println("Ticket with ID " + ticketId + " has been Cancelled." );
                return Boolean.TRUE;
         } else {
             System.out.println("No ticket found with ID " + ticketId);
             return Boolean.FALSE;
         }
    }

    private Optional<User> findUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser;
    }

    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public boolean bookTrainSeat(Train train,int row,int seat){
         try{
             TrainService trainService = new TrainService();
             List<List<Integer>> seats = train.getSeats();

             if(row>=0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()){
                 if(seats.get(row).get(seat)==0){
                     seats.get(row).set(seat,1);
                     train.setSeats(seats);
                     trainService.updateTrain(train);
                     return true;
                 } else {
                     return false;
                 }
             } else {
                 return false;
             }
         } catch (IOException ex){
             return false;
         }
    }

}
