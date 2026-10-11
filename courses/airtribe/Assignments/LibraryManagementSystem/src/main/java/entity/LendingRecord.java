package entity;

import java.time.LocalDate;

public class LendingRecord {
    private final String recordId;
    private final String patronId;
    private final BookCopy bookCopy;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public LendingRecord(String recordId, String patronId, BookCopy bookCopy, int durationDays) {
        this.recordId = recordId;
        this.patronId = patronId;
        this.bookCopy = bookCopy;
        this.checkoutDate = LocalDate.now();
        this.dueDate = this.checkoutDate.plusDays(durationDays);
        this.returnDate = null;
    }

    public BookCopy getBookCopy() { return bookCopy; }
    public boolean isActive() { return returnDate == null; }
    public void markAsReturned() { this.returnDate = LocalDate.now(); }
}
