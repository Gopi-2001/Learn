package services.impl;

import services.SearchStrategy;

import entity.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthorSearchStrategy implements SearchStrategy {
    @Override
    public List<Book> search(String query, Map<String, Book> isbnIndex, Map<String, List<Book>> titleIndex, Map<String, List<Book>> authorIndex) {
        List<Book> results = authorIndex.get(query.toLowerCase());
        return (results != null) ? results : new ArrayList<>(); // O(1) Lookup
    }
}