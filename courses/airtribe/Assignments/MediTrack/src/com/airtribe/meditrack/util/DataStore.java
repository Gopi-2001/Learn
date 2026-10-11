package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.MedicalEntity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataStore<T extends MedicalEntity> {
    private final Map<String, T> items = new HashMap<>();

    public void save(T item) { 
        items.put(item.getId(), item); 
    }

    public T findById(String id) { 
        return items.get(id); 
    }

    public Collection<T> findAll() { 
        return items.values(); 
    }

    public void delete(String id) { 
        items.remove(id); 
    }
}
