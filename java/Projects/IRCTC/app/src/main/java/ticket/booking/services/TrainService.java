package ticket.booking.services;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TrainService {
    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    public TrainService() throws IOException {
        File file = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(file, new TypeReference<List<Train>>() {
        });
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainList
                .stream()
                .filter(train -> validTrain(train, source, destination))
                .collect(Collectors.toList());
    }

    public void addTrain(Train newTrain) {
        Optional<Train> foundTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if(foundTrain.isPresent()){
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) {

        OptionalInt index = IntStream.range(0,trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if(index.isPresent()){
            trainList.set(index.getAsInt(),updatedTrain);
            saveTrainListToFile();
        } else {
            addTrain(updatedTrain);
        }

    }

    public void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validTrain(Train train,String src,String des){
        List<String> stations = train.getStations();
        int srcIndex = stations.indexOf(src);
        int desIndex = stations.indexOf(des);

        return srcIndex!=-1 && desIndex!=-1 && srcIndex<desIndex;
    }

}
