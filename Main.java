import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HelpdeskSystem system = new HelpdeskSystem();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" Student Helpdesk / Support Ticket System");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> handleCreateTicket();
                case 2 -> handleViewAllTickets();
                case 3 -> handleUpdateTicket();
                case 4 -> handleChangeStatus();
                case 5 -> handleSearchTicket();
                case 6 -> handleFilterMenu();
                case 7 -> {
                    System.out.println("Exiting. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----- MENU -----");
        System.out.println("1. Create Ticket");
        System.out.println("2. View All Tickets");
        System.out.println("3. Update Ticket");
        System.out.println("4. Change Ticket Status (Resolve/Close/etc.)");
        System.out.println("5. Search Ticket");
        System.out.println("6. Filter Tickets");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    /** Reads menu input safely; never crashes on non-numeric input. */
    private static int readMenuChoice() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void handleCreateTicket() {
        try {
            System.out.print("Enter title: ");
            String title = scanner.nextLine();

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Category category = readCategory();
            Priority priority = readPriority();

            System.out.print("Enter your name: ");
            String createdBy = scanner.nextLine();

            Ticket ticket = system.createTicket(title, description, category, priority, createdBy);
            System.out.println("Ticket created successfully!");
            System.out.println(ticket.toDetailedString());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleViewAllTickets() {
        List<Ticket> all = system.viewAllTickets();
        if (all.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        System.out.println("\nTotal tickets: " + system.getTotalTicketCount());
        for (Ticket t : all) {
            System.out.println(t);
        }
    }

    private static void handleUpdateTicket() {
        int id = readTicketId();
        System.out.println("Leave a field blank to keep it unchanged.");

        System.out.print("New title: ");
        String title = scanner.nextLine();

        System.out.print("New description: ");
        String description = scanner.nextLine();

        System.out.print("Update category? (y/n): ");
        Category category = null;
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            category = readCategory();
        }

        System.out.print("Update priority? (y/n): ");
        Priority priority = null;
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            priority = readPriority();
        }

        try {
            boolean updated = system.updateTicket(id, title, description, category, priority);
            System.out.println(updated ? "Ticket updated successfully." : "Ticket not found.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleChangeStatus() {
        int id = readTicketId();
        System.out.println("Select new status:");
        System.out.println("1. IN_PROGRESS");
        System.out.println("2. RESOLVED");
        System.out.println("3. CLOSED");
        System.out.println("4. OPEN (reopen)");
        System.out.print("Enter choice: ");
        int choice = readMenuChoice();

        Status newStatus = switch (choice) {
            case 1 -> Status.IN_PROGRESS;
            case 2 -> Status.RESOLVED;
            case 3 -> Status.CLOSED;
            case 4 -> Status.OPEN;
            default -> null;
        };

        if (newStatus == null) {
            System.out.println("Invalid status choice.");
            return;
        }

        try {
            boolean changed = system.changeStatus(id, newStatus);
            System.out.println(changed ? "Status updated successfully." : "Ticket not found.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleSearchTicket() {
        System.out.println("Search by:");
        System.out.println("1. Ticket ID");
        System.out.println("2. Keyword (title/description)");
        System.out.print("Enter choice: ");
        int choice = readMenuChoice();

        if (choice == 1) {
            int id = readTicketId();
            system.findTicketById(id)
                    .ifPresentOrElse(
                            t -> System.out.println(t.toDetailedString()),
                            () -> System.out.println("Ticket not found."));
        } else if (choice == 2) {
            System.out.print("Enter keyword: ");
            String keyword = scanner.nextLine();
            List<Ticket> results = system.searchByKeyword(keyword);
            if (results.isEmpty()) {
                System.out.println("No matching tickets found.");
            } else {
                results.forEach(System.out::println);
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void handleFilterMenu() {
        System.out.println("Filter by:");
        System.out.println("1. Status");
        System.out.println("2. Category");
        System.out.print("Enter choice: ");
        int choice = readMenuChoice();

        List<Ticket> results;
        if (choice == 1) {
            Status status = readStatus();
            results = system.filterByStatus(status);
        } else if (choice == 2) {
            Category category = readCategory();
            results = system.filterByCategory(category);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No matching tickets found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static int readTicketId() {
        System.out.print("Enter ticket ID: ");
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid ID. Enter a numeric ticket ID: ");
            }
        }
    }

    private static Category readCategory() {
        while (true) {
            System.out.print("Category (1-TECHNICAL, 2-ACADEMIC, 3-HOSTEL, 4-OTHER): ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1": return Category.TECHNICAL;
                case "2": return Category.ACADEMIC;
                case "3": return Category.HOSTEL;
                case "4": return Category.OTHER;
                default: System.out.println("Invalid input, try again.");
            }
        }
    }

    private static Priority readPriority() {
        while (true) {
            System.out.print("Priority (1-LOW, 2-MEDIUM, 3-HIGH): ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1": return Priority.LOW;
                case "2": return Priority.MEDIUM;
                case "3": return Priority.HIGH;
                default: System.out.println("Invalid input, try again.");
            }
        }
    }

    private static Status readStatus() {
        while (true) {
            System.out.print("Status (1-OPEN, 2-IN_PROGRESS, 3-RESOLVED, 4-CLOSED): ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1": return Status.OPEN;
                case "2": return Status.IN_PROGRESS;
                case "3": return Status.RESOLVED;
                case "4": return Status.CLOSED;
                default: System.out.println("Invalid input, try again.");
            }
        }
    }
}
