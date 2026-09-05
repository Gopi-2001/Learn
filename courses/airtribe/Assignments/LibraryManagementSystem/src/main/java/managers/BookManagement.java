package managers;

import services.SearchStrategy;

import entity.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookManagement {
    private final Map<String, Book> booksByIsbn = new HashMap<>();
    private final Map<String, List<Book>> booksByTitle = new HashMap<>();
    private final Map<String, List<Book>> booksByAuthor = new HashMap<>();

    public void addBook(Book book) {
        booksByIsbn.put(book.getIsbn(), book);

        String titleKey = book.getTitle().toLowerCase();
        if (!booksByTitle.containsKey(titleKey)) {
            booksByTitle.put(titleKey, new ArrayList<>());
        }
        booksByTitle.get(titleKey).add(book);

        String authorKey = book.getAuthor().toLowerCase();
        if (!booksByAuthor.containsKey(authorKey)) {
            booksByAuthor.put(authorKey, new ArrayList<>());
        }
        booksByAuthor.get(authorKey).add(book);
    }

    public void removeBook(String isbn) {
        Book book = booksByIsbn.remove(isbn);
        if (book != null) {
            List<Book> titleList = booksByTitle.get(book.getTitle().toLowerCase());
            if (titleList != null) titleList.remove(book);

            List<Book> authorList = booksByAuthor.get(book.getAuthor().toLowerCase());
            if (authorList != null) authorList.remove(book);
        }
    }

    public List<Book> executeSearch(String query, SearchStrategy strategy) {
        return strategy.search(query, booksByIsbn, booksByTitle, booksByAuthor);
    }
}
