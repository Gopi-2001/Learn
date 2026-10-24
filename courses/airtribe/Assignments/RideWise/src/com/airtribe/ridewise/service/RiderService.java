package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RiderService {

    private Map<String, Rider> riders = new LinkedHashMap<>();

    public Rider registerRider(String name, int location) {
        Rider rider = new Rider(IdGenerator.nextRiderId(), name, location);
        riders.put(rider.getId(), rider);
        return rider;
    }

    public Rider getRiderById(String id) {
        Rider rider = riders.get(id.trim().toUpperCase());
        if (rider == null) {
            throw new IllegalArgumentException("Rider not found with id: " + id);
        }
        return rider;
    }

    public List<Rider> getAllRiders() {
        return new ArrayList<>(riders.values());
    }
}
