import entity.*;
import managers.BookManagement;
import managers.InventoryManagement;
import managers.LendingManagement;
import managers.PatronManagement;
import services.impl.IsbnSearchStrategy;
import services.impl.TitleSearchStrategy;

import java.util.List;

public class main {
    public static void main(String[] args) {
        System.out.println("=== INITIALIZING ENVIRONMENT STACK ===\n");

        BookManagement bookManager = new BookManagement();
        PatronManagement patronManager = new PatronManagement();
        InventoryManagement inventoryManager = new InventoryManagement();
        LendingManagement lendingManager = new LendingManagement(inventoryManager, patronManager);

        Book b1 = new Book("1234567890", "Book1", "Author1", 2025);
        Book b2 = new Book("1234567891", "Book2", "Author2", 2026);

        bookManager.addBook(b1);
        bookManager.addBook(b2);

        inventoryManager.createCopies(b1, 2);
        inventoryManager.createCopies(b2, 1);

        Patron p1 = new Patron("101", "Gopikant", "gopi@example.com");
        patronManager.addPatron(p1);

        System.out.println("--- Execution Step 1: Performing Strategy Lookups (O(1) Map Hash Resolution) ---");

        List<Book> titleResults = bookManager.executeSearch("Book1", new TitleSearchStrategy());
        for (Book b : titleResults) {
            System.out.println("Result Match [Title]: " + b.getTitle() + " written by " + b.getAuthor());
        }

        List<Book> isbnResults = bookManager.executeSearch("1234567891", new IsbnSearchStrategy());
        for (Book b : isbnResults) {
            System.out.println("Result Match [ISBN]: " + b.getTitle() + " written by " + b.getAuthor());
        }

        System.out.println("\n--- Execution Step 2: Running Operational Transaction Pipeline ---");
        lendingManager.checkoutBook("101", "1234567890");
        lendingManager.checkoutBook("101", "1234567891");

        System.out.println("\n--- Execution Step 3: Performing Post-Transaction Ledger Updates ---");
        lendingManager.returnBook("101", "1234567890-C1");
    }
}

