package entity;

import java.util.ArrayList;
import java.util.List;

public class Patron {
    private final String patronId;
    private final String name;
    private final String email;
    private final List<LendingRecord> borrowingHistory;

    public Patron(String patronId, String name, String email) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.borrowingHistory = new ArrayList<>();
    }

    public String getPatronId() { return patronId; }
    public String getName() { return name; }
    public List<LendingRecord> getBorrowingHistory() { return borrowingHistory; }
    public void addRecord(LendingRecord record) { borrowingHistory.add(record); }
}