import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String description;
    private boolean completed;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
        this.priority = "MEDIUM";
        this.createdDate = LocalDateTime.now();
    }

    public Task(int id, String description, boolean completed, String priority,
            LocalDateTime createdDate, LocalDateTime completedDate) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.priority = priority;
        this.createdDate = createdDate;
        this.completedDate = completedDate;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void markCompleted() {
        this.completed = true;
        this.completedDate = LocalDateTime.now();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdDate.format(formatter);
    }

    @Override
    public String toString() {
        String status = completed ? "[DONE]" : "[    ]";
        String prioritySymbol = "";
        switch (priority) {
            case "HIGH":
                prioritySymbol = "[HIGH]";
                break;
            case "MEDIUM":
                prioritySymbol = "[MED]";
                break;
            case "LOW":
                prioritySymbol = "[LOW]";
                break;
        }
        return status + " " + id + ". " + description + " " + prioritySymbol;
    }
}
