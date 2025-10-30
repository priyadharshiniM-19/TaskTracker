import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class TaskTrackerGUI extends JFrame {
    private TaskManager manager;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private JTextField inputField;
    private JLabel statsLabel;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> filterCombo;

    // Professional Color Scheme
    private Color primaryBlue = new Color(41, 128, 185);
    private Color hoverBlue = new Color(52, 152, 219);
    private Color successGreen = new Color(39, 174, 96);
    private Color hoverGreen = new Color(46, 204, 113);
    private Color warningYellow = new Color(243, 156, 18);
    private Color hoverYellow = new Color(241, 196, 15);
    private Color dangerRed = new Color(192, 57, 43);
    private Color hoverRed = new Color(231, 76, 60);
    private Color lightBg = new Color(236, 240, 241);
    private Color darkText = new Color(44, 62, 80);

    public TaskTrackerGUI() {
        manager = new TaskManager();
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        inputField = new JTextField(20);
        statsLabel = new JLabel();

        setTitle("Professional Task Tracker");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(lightBg);

        // Menu Bar
        createMenuBar();

        // Top Panel (Input Area)
        JPanel topPanel = createTopPanel();

        // Center Panel (Task List)
        JScrollPane scrollPane = createTaskListPanel();

        // Bottom Panel (Actions & Stats)
        JPanel bottomPanel = createBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshList();

        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(new MatteBorder(0, 0, 1, 0, new Color(189, 195, 199)));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem clearCompletedItem = new JMenuItem("Clear Completed Tasks");
        clearCompletedItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearCompletedItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete all completed tasks?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.deleteCompletedTasks();
                refreshList();
            }
        });
        editMenu.add(clearCompletedItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Professional Task Tracker v2.0\nDeveloped for Task Management\n(c) 2025",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        topPanel.setBackground(lightBg);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel label = new JLabel("New Task:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(darkText);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)));

        String[] priorities = { "MEDIUM", "HIGH", "LOW" };
        priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setPreferredSize(new Dimension(100, 35));
        priorityCombo.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Task", primaryBlue, hoverBlue);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> addTask());

        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addTask();
                }
            }
        });

        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setBackground(Color.WHITE);
        topRow.add(label, BorderLayout.WEST);
        topRow.add(inputField, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.add(new JLabel("Priority:"));
        bottomRow.add(priorityCombo);
        bottomRow.add(addButton);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.WHITE);
        content.add(topRow, BorderLayout.NORTH);
        content.add(bottomRow, BorderLayout.SOUTH);

        inputPanel.add(content);
        topPanel.add(inputPanel);

        return topPanel;
    }

    private JScrollPane createTaskListPanel() {
        taskList.setFont(new Font("Consolas", Font.PLAIN, 13));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setBackground(Color.WHITE);
        taskList.setBorder(new EmptyBorder(10, 10, 10, 10));
        taskList.setFixedCellHeight(32);
        taskList.setSelectionBackground(new Color(52, 152, 219));
        taskList.setSelectionForeground(Color.WHITE);

        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Task");
        JMenuItem changePriorityItem = new JMenuItem("Change Priority");
        JMenuItem markDoneItem = new JMenuItem("Mark Complete");
        JMenuItem deleteItem = new JMenuItem("Delete Task");

        editItem.addActionListener(e -> editSelectedTask());
        changePriorityItem.addActionListener(e -> changePriority());
        markDoneItem.addActionListener(e -> markSelectedDone());
        deleteItem.addActionListener(e -> deleteSelectedTask());

        contextMenu.add(markDoneItem);
        contextMenu.add(editItem);
        contextMenu.add(changePriorityItem);
        contextMenu.addSeparator();
        contextMenu.add(deleteItem);

        taskList.setComponentPopupMenu(contextMenu);

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(new CompoundBorder(
                new EmptyBorder(0, 15, 0, 15),
                new LineBorder(new Color(189, 195, 199), 1)));

        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 15));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(lightBg);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(lightBg);
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLabel.setForeground(darkText);

        String[] filters = { "ALL", "PENDING", "COMPLETED", "HIGH", "MEDIUM", "LOW" };
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(120, 30));
        filterCombo.setBackground(Color.WHITE);
        filterCombo.addActionListener(e -> applyFilter());

        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        buttonPanel.setBackground(lightBg);

        JButton doneButton = createStyledButton("Mark Complete", successGreen, hoverGreen);
        JButton editButton = createStyledButton("Edit Task", warningYellow, hoverYellow);
        JButton deleteButton = createStyledButton("Delete", dangerRed, hoverRed);

        doneButton.setPreferredSize(new Dimension(140, 38));
        editButton.setPreferredSize(new Dimension(110, 38));
        deleteButton.setPreferredSize(new Dimension(100, 38));

        doneButton.addActionListener(e -> markSelectedDone());
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());

        buttonPanel.add(doneButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(12, 15, 12, 15)));

        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsLabel.setForeground(darkText);
        statsPanel.add(statsLabel);

        JPanel topSection = new JPanel(new BorderLayout(10, 0));
        topSection.setBackground(lightBg);
        topSection.add(filterPanel, BorderLayout.WEST);
        topSection.add(buttonPanel, BorderLayout.CENTER);

        bottomPanel.add(topSection, BorderLayout.NORTH);
        bottomPanel.add(statsPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void addTask() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String priority = (String) priorityCombo.getSelectedItem();
            manager.addTask(text, priority);
            inputField.setText("");
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter a task description",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void markSelectedDone() {
        int selected = taskList.getSelectedIndex();
        if (selected != -1) {
            manager.markDone(selected);
            refreshList();
        } else {
            showSelectionError();
        }
    }

    private void editSelectedTask() {
        int selected = taskList.getSelectedIndex();
        if (selected != -1) {
            Task task = manager.getTasks().get(selected);
            String newDesc = JOptionPane.showInputDialog(this,
                    "Edit task description:",
                    task.getDescription());
            if (newDesc != null && !newDesc.trim().isEmpty()) {
                manager.editTask(selected, newDesc);
                refreshList();
            }
        } else {
            showSelectionError();
        }
    }

    private void changePriority() {
        int selected = taskList.getSelectedIndex();
        if (selected != -1) {
            String[] priorities = { "HIGH", "MEDIUM", "LOW" };
            String newPriority = (String) JOptionPane.showInputDialog(this,
                    "Select new priority:",
                    "Change Priority",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    priorities,
                    priorities[1]);

            if (newPriority != null) {
                manager.updatePriority(selected, newPriority);
                refreshList();
            }
        } else {
            showSelectionError();
        }
    }

    private void deleteSelectedTask() {
        int selected = taskList.getSelectedIndex();
        if (selected != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.deleteTask(selected);
                refreshList();
            }
        } else {
            showSelectionError();
        }
    }

    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        listModel.clear();
        for (Task t : manager.getFilteredTasks(filter)) {
            listModel.addElement(t.toString());
        }
        updateStats();
    }

    private void refreshList() {
        filterCombo.setSelectedIndex(0);
        applyFilter();
    }

    private void updateStats() {
        long total = manager.getTasks().size();
        long completed = manager.getCompletedCount();
        long pending = manager.getPendingCount();
        statsLabel.setText(String.format(
                "Total: %d  |  Completed: %d  |  Pending: %d",
                total, completed, pending));
    }

    private void showSelectionError() {
        JOptionPane.showMessageDialog(this,
                "Please select a task first",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TaskTrackerGUI gui = new TaskTrackerGUI();
            gui.setVisible(true);
        });
    }
}
