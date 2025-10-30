import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> tasks;
    private static final String FILE_PATH = "tasks.txt";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks();
    }

    public void addTask(String desc, String priority) {
        tasks.add(new Task(tasks.size() + 1, desc));
        tasks.get(tasks.size() - 1).setPriority(priority);
        saveTasks();
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            reindexTasks();
            saveTasks();
        }
    }

    public void markDone(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            saveTasks();
        }
    }

    public void editTask(int index, String newDescription) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setDescription(newDescription);
            saveTasks();
        }
    }

    public void updatePriority(int index, String priority) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setPriority(priority);
            saveTasks();
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getFilteredTasks(String filter) {
        switch (filter) {
            case "COMPLETED":
                return tasks.stream().filter(Task::isCompleted).collect(Collectors.toList());
            case "PENDING":
                return tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
            case "HIGH":
                return tasks.stream().filter(t -> t.getPriority().equals("HIGH")).collect(Collectors.toList());
            case "MEDIUM":
                return tasks.stream().filter(t -> t.getPriority().equals("MEDIUM")).collect(Collectors.toList());
            case "LOW":
                return tasks.stream().filter(t -> t.getPriority().equals("LOW")).collect(Collectors.toList());
            default:
                return tasks;
        }
    }

    public void deleteCompletedTasks() {
        tasks.removeIf(Task::isCompleted);
        reindexTasks();
        saveTasks();
    }

    public long getCompletedCount() {
        return tasks.stream().filter(Task::isCompleted).count();
    }

    public long getPendingCount() {
        return tasks.stream().filter(t -> !t.isCompleted()).count();
    }

    private void reindexTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setId(i + 1);
        }
    }

    private void saveTasks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task t : tasks) {
                bw.write(t.getId() + "," +
                        t.getDescription() + "," +
                        t.isCompleted() + "," +
                        t.getPriority() + "," +
                        t.getCreatedDate() + "," +
                        (t.getCompletedDate() != null ? t.getCompletedDate() : "") + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    LocalDateTime createdDate = LocalDateTime.parse(parts[4]);
                    LocalDateTime completedDate = parts.length > 5 && !parts[5].isEmpty()
                            ? LocalDateTime.parse(parts[5])
                            : null;

                    Task t = new Task(Integer.parseInt(parts[0]), parts[1],
                            Boolean.parseBoolean(parts[2]), parts[3],
                            createdDate, completedDate);
                    tasks.add(t);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
