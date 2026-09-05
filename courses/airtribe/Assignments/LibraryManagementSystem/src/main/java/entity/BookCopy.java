package entity;

public class BookCopy {
    private final String copyId;
    private final Book metadata;
    private boolean isBorrowed;

    public BookCopy(String copyId, Book metadata) {
        this.copyId = copyId;
        this.metadata = metadata;
        this.isBorrowed = false;
    }

    public String getCopyId() { return copyId; }
    public Book getMetadata() { return metadata; }
    public boolean isBorrowed() { return isBorrowed; }
    public void setBorrowed(boolean borrowed) { this.isBorrowed = borrowed; }
}