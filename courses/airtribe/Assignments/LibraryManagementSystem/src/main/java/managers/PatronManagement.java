package managers;

import entity.LendingRecord;
import entity.Patron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatronManagement {
    private final Map<String, Patron> patrons = new HashMap<>();

    public void addPatron(Patron patron) {
        patrons.put(patron.getPatronId(), patron);
    }

    public Patron getPatron(String patronId) {
        return patrons.get(patronId);
    }

    public List<LendingRecord> getHistory(String patronId) {
        Patron patron = patrons.get(patronId);
        return (patron != null) ? patron.getBorrowingHistory() : new ArrayList<>();
    }
}
