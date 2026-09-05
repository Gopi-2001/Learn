package services.impl;

import services.SearchStrategy;

import entity.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IsbnSearchStrategy implements SearchStrategy {

    @Override
    public List<Book> search(String query, Map<String, Book> isbnIndex, Map<String, List<Book>> titleIndex, Map<String, List<Book>> authorIndex) {
        List<Book> results = new ArrayList<>();
        Book book = isbnIndex.get(query);
        if (book != null) {
            results.add(book);
        }
        return results; // O(1) Lookup
    }
}
