package managers;

import entity.BookCopy;
import entity.LendingRecord;
import entity.Patron;
import services.Notification;
import services.impl.NotificationFactory;

import java.util.UUID;
import java.util.logging.Logger;

public class LendingManagement {
    private static final Logger logger = Logger.getLogger(LendingManagement.class.getName());
    private final InventoryManagement inventoryMgr;
    private final PatronManagement patronMgr;
    private final Notification notification = NotificationFactory.createNotification();

    private static final int MAX_LIMIT = 3;
    private static final int DURATION_DAYS = 14;

    public LendingManagement(InventoryManagement inventoryMgr, PatronManagement patronMgr) {
        this.inventoryMgr = inventoryMgr;
        this.patronMgr = patronMgr;
    }

    public boolean checkoutBook(String patronId, String isbn) {
        Patron patron = patronMgr.getPatron(patronId);
        if (patron == null) {
            logger.warning("Checkout failed: Profile mapping metadata is invalid.");
            return false;
        }

        int currentActiveLoans = 0;
        for (LendingRecord record : patron.getBorrowingHistory()) {
            if (record.isActive()) {
                currentActiveLoans++;
            }
        }

        if (currentActiveLoans >= MAX_LIMIT) {
            logger.warning("Checkout failed: Patron profile reached dynamic loan limits.");
            return false;
        }

        BookCopy copy = inventoryMgr.findAvailableCopy(isbn);
        if (copy == null) {
            logger.warning("Checkout failed: Stock registry depleted for core identifier: " + isbn);
            return false;
        }

        copy.setBorrowed(true);
        String recordId = "LEND-" + UUID.randomUUID().toString().substring(0, 8);
        LendingRecord record = new LendingRecord(recordId, patronId, copy, DURATION_DAYS);

        patron.addRecord(record);
        notification.send(patron.getName() + " confirmed checkout of book metadata: " + copy.getMetadata().getTitle());
        return true;
    }

    public boolean returnBook(String patronId, String copyId) {
        Patron patron = patronMgr.getPatron(patronId);
        BookCopy copy = inventoryMgr.getCopyById(copyId);

        if (patron == null || copy == null) {
            logger.warning("Return registration rejected: Database index resolution error.");
            return false;
        }
        for (LendingRecord record : patron.getBorrowingHistory()) {
            if (record.getBookCopy().getCopyId().equals(copyId) && record.isActive()) {
                copy.setBorrowed(false);
                record.markAsReturned();
                notification.send(patron.getName() + " verified return of allocation instance: " + copyId);
                return true;
            }
        }
        logger.warning("Return logic transaction aborted for identifier: " + copyId);
        return false;
    }
}
