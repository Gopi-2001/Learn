package managers;

import entity.BookCopy;

import entity.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagement {
    private final Map<String, List<BookCopy>> inventory = new HashMap<>();
    private final Map<String, BookCopy> specificCopies = new HashMap<>();

    public void createCopies(Book book, int count) {
        List<BookCopy> copiesList = inventory.get(book.getIsbn());
        if (copiesList == null) {
            copiesList = new ArrayList<>();
            inventory.put(book.getIsbn(), copiesList);
        }
        for (int i = 0; i < count; i++) {
            String copyId = book.getIsbn() + "-C" + (copiesList.size() + 1);
            BookCopy copy = new BookCopy(copyId, book);
            copiesList.add(copy);
            specificCopies.put(copyId, copy);
        }
    }

    public BookCopy findAvailableCopy(String isbn) {
        List<BookCopy> copies = inventory.get(isbn);
        if (copies != null) {
            for (BookCopy copy : copies) {
                if (!copy.isBorrowed()) {
                    return copy;
                }
            }
        }
        return null;
    }

    public BookCopy getCopyById(String copyId) {
        return specificCopies.get(copyId);
    }
}
