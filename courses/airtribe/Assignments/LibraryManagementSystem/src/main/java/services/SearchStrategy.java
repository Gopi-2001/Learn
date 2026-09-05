package services;

import entity.Book;
import java.util.List;
import java.util.Map;

public interface SearchStrategy {
    List<Book> search(String query, Map<String, Book> isbnIndex, Map<String, List<Book>> titleIndex, Map<String, List<Book>> authorIndex);
}