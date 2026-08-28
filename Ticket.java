import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private int id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;

    public Ticket(int id, String title, String description, Category category,
                  Priority priority, String createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.createdBy = createdBy;
        this.status = Status.OPEN;
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        touch();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        touch();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        touch();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        touch();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    private void touch() {
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format(
                "Ticket #%d | %-20s | %-10s | %-8s | %-12s | By: %-10s | Created: %s | Updated: %s",
                id, title, category, priority, status, createdBy,
                createdDate.format(FORMATTER), lastUpdated.format(FORMATTER));
    }

    public String toDetailedString() {
        return "----------------------------------------\n"
                + "Ticket ID    : " + id + "\n"
                + "Title        : " + title + "\n"
                + "Description  : " + description + "\n"
                + "Category     : " + category + "\n"
                + "Priority     : " + priority + "\n"
                + "Status       : " + status + "\n"
                + "Created By   : " + createdBy + "\n"
                + "Created On   : " + createdDate.format(FORMATTER) + "\n"
                + "Last Updated : " + lastUpdated.format(FORMATTER) + "\n"
                + "----------------------------------------";
    }
}
