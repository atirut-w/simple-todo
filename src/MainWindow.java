import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JTextArea taskList;
    private JTextField titleField;
    private JButton addButton;
    private JTextField indexField;
    private JButton markDoneButton;
    private JButton removeButton;

    ArrayList<TaskItem> tasks = new ArrayList<>();

    public MainWindow() {
        add(mainPanel);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleField.getText().isBlank()) {
                    JOptionPane.showMessageDialog(mainPanel, "Title cannot be blank", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                TaskItem item = new TaskItem();
                item.title = titleField.getText();
                tasks.add(item);
                try {
                    updateTaskList();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        markDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = Integer.parseInt(indexField.getText()) - 1;
                    if (ind >= tasks.size()) {
                        JOptionPane.showMessageDialog(mainPanel, "Invalid index", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tasks.get(ind).done = true;
                    updateTaskList();
                } catch (NumberFormatException | IOException err) {
                    JOptionPane.showMessageDialog(mainPanel, "Could not parse index", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = Integer.parseInt(indexField.getText()) - 1;
                    if (ind >= tasks.size()) {
                        JOptionPane.showMessageDialog(mainPanel, "Invalid index", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tasks.remove(ind);
                    updateTaskList();
                } catch (NumberFormatException | IOException err) {
                    JOptionPane.showMessageDialog(mainPanel, "Could not parse index", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        try {
            FileReader reader = new FileReader("tasks.txt");
            Scanner scanner = new Scanner(reader);
            scanner.useDelimiter("[\\r\\n]+");

            while (scanner.hasNext()) {
                String line = scanner.next();
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(",");

                TaskItem item = new TaskItem();
                item.title = lineScanner.next();
                item.done = lineScanner.next().equals("true");
                tasks.add(item);
            }

            updateTaskList();
        } catch (FileNotFoundException e) {
            System.out.println("Task data not found, starting from scratch");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void updateTaskList() throws IOException {
        String content = "";
        for (int i = 0; i < tasks.size(); i++) {
            TaskItem item = tasks.get(i);
            content += (i + 1) + ": " + item.title;
            if (item.done) {
                content += " (DONE)";
            }
            if (i != tasks.size() - 1) {
                content += "\n";
            }
        }
        taskList.setText(content);
        saveTaskList();
    }

    void saveTaskList() throws IOException {
        FileWriter writer = new FileWriter("tasks.txt");

        for (TaskItem item : tasks) {
            writer.write(item.title + "," + (item.done) + "\n");
        }

        writer.close();
    }
}
