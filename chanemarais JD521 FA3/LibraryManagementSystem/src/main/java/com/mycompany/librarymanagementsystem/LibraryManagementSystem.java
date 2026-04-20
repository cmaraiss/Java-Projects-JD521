/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.librarymanagementsystem;

/**
 *
 * @author OEM
 */
import java.util.*;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryManagementSystem {
    static Scanner scanner = new Scanner(System.in);
    static List<Member> members = new ArrayList<>();
    static List<Book> books = new ArrayList<>();
    static ScheduledExecutorService fineUpdater = Executors.newSingleThreadScheduledExecutor();
    static ScheduledExecutorService notificationSender = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        // Adding sample books
        books.add(new Book("Tsotsi", "Athol Fugard", "978-0-06-112008-4"));
        books.add(new Book("Harry Potter", " J.K. Rowling", "978-0-545-01022-1"));
        books.add(new Book("The Da Vinci Code", "Dan Brown", "978-0-545-01022-1"));
        books.add(new Book("Lve and other drugs", "Mohinder", "978-0-316-76948-4"));
        books.add(new Book("Pride and Prejudice", "Jane Austen", "978-1-85326-000-0"));

        // Start background tasks
        startBackgroundTasks();

        // Main menu loop
        int choice;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Add new Member");
            System.out.println("2. Show list of all books");
            System.out.println("3. Checkout a book");
            System.out.println("4. Return a book");
            System.out.println("5. Search for a book");
            System.out.println("6. Show borrowed books for a member");
            System.out.println("7. Display all members");
            System.out.println("8. Calculate overdue fines for a member");
            System.out.println("9. Check due dates for borrowed books");
            System.out.println("10. View fines for a member");
            System.out.println("11. Manage notifications");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerMember();
                    break;
                case 2:
                    showAllBooks();
                    break;
                case 3:
                    checkoutBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    searchBook();
                    break;
                case 6:
                    showBorrowedBooks();
                    break;
                case 7:
                    displayAllMembers();
                    break;
                case 8:
                    calculateOverdueFines();
                    break;
                case 9:
                    checkDueDates();
                    break;
                case 10:
                    viewFines();
                    break;
                case 11:
                    manageNotifications();
                    break;
                case 12:
                    stopBackgroundTasks(); // Stop background tasks before exiting
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 12);
    }

    private static void startBackgroundTasks() {
        fineUpdater.scheduleAtFixedRate(() -> {
            System.out.println("\nUpdating fines...");
            updateFines();
        }, 0, 1, TimeUnit.DAYS); // Update fines every day

        notificationSender.scheduleAtFixedRate(() -> {
            System.out.println("\nSending notifications...");
            sendNotifications();
        }, 0, 7, TimeUnit.DAYS); // Send notifications every week
    }

    private static void stopBackgroundTasks() {
        fineUpdater.shutdown();
        notificationSender.shutdown();
    }

    private static void registerMember() {
        System.out.print("Enter member name: ");
        String name = scanner.nextLine();
        System.out.print("Enter member email: ");
        String email = scanner.nextLine();
        Member member = new Member(name, email);
        members.add(member);
        System.out.println("Member added successfully.");
    }

    private static void showAllBooks() {
        System.out.println("\nList of all books:");
        for (Book book : books) {
            System.out.println(book.getTitle() + " by " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
        }
    }

    private static void checkoutBook() {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        Member member = findMemberByName(memberName);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.print("Enter title of the book to checkout: ");
        String bookTitle = scanner.nextLine();
        Book bookToCheckout = findBookByTitle(bookTitle);
        if (bookToCheckout == null) {
            System.out.println("Book not found.");
            return;
        }

        try {
            member.borrowBook(bookToCheckout);
            System.out.println("Book '" + bookToCheckout.getTitle() + "' checked out successfully.");
        } catch (BookNotAvailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        Member member = findMemberByName(memberName);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        System.out.print("Enter title of the book to return: ");
        String bookTitle = scanner.nextLine();
        System.out.print("Enter ISBN of the book to return: ");
        String isbn = scanner.nextLine();

        member.returnBook(bookTitle, isbn);
    }

    private static void searchBook() {
        System.out.print("Enter search query (title, author, or ISBN): ");
        String query = scanner.nextLine().toLowerCase();

        System.out.println("\nSearch results:");
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query)
                    || book.getAuthor().toLowerCase().contains(query)
                    || book.getIsbn().toLowerCase().contains(query)) {
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
            }
        }
    }

    private static void showBorrowedBooks() {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        Member member = findMemberByName(memberName);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        List<Book> borrowedBooks = member.getBorrowedBooks();
        if (borrowedBooks.isEmpty()) {
            System.out.println("No books borrowed by " + memberName);
        } else {
            System.out.println("Borrowed books by " + memberName + ":");
            for (Book book : borrowedBooks) {
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
            }
        }
    }

    private static void displayAllMembers() {
        System.out.println("\nList of all members:");
        for (Member member : members) {
            System.out.println(member.getName() + " (" + member.getEmail() + ")");
        }
    }

    private static void calculateOverdueFines() {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        Member member = findMemberByName(memberName);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        double totalFines = member.calculateTotalOverdueFines();
        System.out.println("Total overdue fines for " + memberName + ": $" + totalFines);
    }

    private static void checkDueDates() {
        System.out.println("\nChecking due dates for borrowed books...");
        for (Member member : members) {
            List<Book> borrowedBooks = member.getBorrowedBooks();
            for (Book book : borrowedBooks) {
                System.out.println("Book '" + book.getTitle() + "' is due on " + book.getDueDate());
            }
        }
    }

    private static void viewFines() {
        System.out.print("Enter member name: ");
        String memberName = scanner.nextLine();
        Member member = findMemberByName(memberName);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        double totalFines = member.calculateTotalOverdueFines();
        System.out.println("Total overdue fines for " + memberName + ": $" + totalFines);
    }

    private static void manageNotifications() {
        System.out.println("\nManaging notifications...");
        // Logic for managing notifications can be added here
    }

    private static Member findMemberByName(String name) {
        for (Member member : members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    private static Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    private static void updateFines() {
        System.out.println("Fines updated.");
    }

    private static void sendNotifications() {
        System.out.println("Notifications sent.");
    }
}

class Book {
    String title;
    String author;
    String isbn;
    private boolean isAvailable;
    private LocalDate dueDate;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
        this.dueDate = null;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void toggleAvailability() {
        isAvailable = !isAvailable;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public double calculateOverdueFine() {
        if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            long daysOverdue = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
            double fine = daysOverdue * 0.50; // Example: $0.50 per day overdue
            return fine;
        }
        return 0;
    }
}

class Member {
    String name;
    private String email;
    List<Book> borrowedBooks;

    public Member(String name, String email) {
        this.name = name;
        if (isValidEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.borrowedBooks = new ArrayList<>();
    }

    private boolean isValidEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) throws BookNotAvailableException {
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("The book is not available for checkout.");
        }
        borrowedBooks.add(book);
        book.toggleAvailability();
        book.setDueDate(LocalDate.now().plusDays(14)); // Example loan period: 14 days
    }

    public void returnBook(String title, String isbn) {
        Book bookToReturn = null;
        for (Book book : borrowedBooks) {
            if (book.getTitle().equalsIgnoreCase(title) && book.getIsbn().equalsIgnoreCase(isbn)) {
                bookToReturn = book;
                break;
            }
        }
        if (bookToReturn != null) {
            borrowedBooks.remove(bookToReturn);
            bookToReturn.toggleAvailability();
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Book not found.");
        }
    }

    public double calculateTotalOverdueFines() {
        double totalFines = 0;
        for (Book book : borrowedBooks) {
            totalFines += book.calculateOverdueFine();
        }
        return totalFines;
    }
}

class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
