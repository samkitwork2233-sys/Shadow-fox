import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HelpdeskSystem {

    private List<Ticket> tickets;
    private int nextId;

    public HelpdeskSystem() {
        this.tickets = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Creates a new ticket after validating input.
     * Ticket IDs are auto-generated so duplicates can never occur.
     */
    public Ticket createTicket(String title, String description, Category category,
                                Priority priority, String createdBy) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket title cannot be empty.");
        }
        if (createdBy == null || createdBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator name cannot be empty.");
        }

        Ticket ticket = new Ticket(nextId, title.trim(), description, category, priority, createdBy.trim());
        tickets.add(ticket);
        nextId++;
        return ticket;
    }

    public List<Ticket> viewAllTickets() {
        return tickets;
    }

    public Optional<Ticket> findTicketById(int id) {
        return tickets.stream().filter(t -> t.getId() == id).findFirst();
    }

    public List<Ticket> searchByKeyword(String keyword) {
        String lower = keyword.toLowerCase();
        List<Ticket> results = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getTitle().toLowerCase().contains(lower)
                    || t.getDescription().toLowerCase().contains(lower)) {
                results.add(t);
            }
        }
        return results;
    }

    public List<Ticket> filterByStatus(Status status) {
        List<Ticket> results = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getStatus() == status) {
                results.add(t);
            }
        }
        return results;
    }

    public List<Ticket> filterByCategory(Category category) {
        List<Ticket> results = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getCategory() == category) {
                results.add(t);
            }
        }
        return results;
    }

    /**
     * Updates editable fields of a ticket. Pass null to leave a field unchanged.
     */
    public boolean updateTicket(int id, String title, String description,
                                 Category category, Priority priority) {
        Optional<Ticket> found = findTicketById(id);
        if (found.isEmpty()) {
            return false;
        }

        Ticket ticket = found.get();

        if (ticket.getStatus() == Status.CLOSED) {
            throw new IllegalStateException("Cannot update a closed ticket.");
        }

        if (title != null && !title.trim().isEmpty()) {
            ticket.setTitle(title.trim());
        }
        if (description != null && !description.trim().isEmpty()) {
            ticket.setDescription(description.trim());
        }
        if (category != null) {
            ticket.setCategory(category);
        }
        if (priority != null) {
            ticket.setPriority(priority);
        }
        return true;
    }

    /**
     * Enforces a logical status transition instead of allowing any jump.
     * OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED
     * A RESOLVED ticket can be reopened back to IN_PROGRESS if needed.
     */
    public boolean changeStatus(int id, Status newStatus) {
        Optional<Ticket> found = findTicketById(id);
        if (found.isEmpty()) {
            return false;
        }

        Ticket ticket = found.get();
        Status current = ticket.getStatus();

        if (current == Status.CLOSED) {
            throw new IllegalStateException("Ticket is already closed and cannot change status.");
        }

        boolean validTransition =
                (current == Status.OPEN && (newStatus == Status.IN_PROGRESS || newStatus == Status.CLOSED))
                || (current == Status.IN_PROGRESS && (newStatus == Status.RESOLVED || newStatus == Status.OPEN))
                || (current == Status.RESOLVED && (newStatus == Status.CLOSED || newStatus == Status.IN_PROGRESS));

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " -> " + newStatus);
        }

        ticket.setStatus(newStatus);
        return true;
    }

    public boolean resolveTicket(int id) {
        return changeStatus(id, Status.RESOLVED);
    }

    public boolean closeTicket(int id) {
        return changeStatus(id, Status.CLOSED);
    }

    public int getTotalTicketCount() {
        return tickets.size();
    }
}
