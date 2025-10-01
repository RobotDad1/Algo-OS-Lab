import javax.swing.*;
import java.awt.*;

// Node for linked list stack
class Node {
    String data;
    Node next;
    Node(String data) {
        this.data = data;
        this.next = null;
    }
}

// Custom Stack implementation (linked list)
class MyStack {
    private Node top;

    public void push(String val) {
        Node newNode = new Node(val);
        newNode.next = top;
        top = newNode;
    }

    public String pop() {
        if (isEmpty()) return null;
        String val = top.data;
        top = top.next;
        return val;
    }

    public String peek() {
        return isEmpty() ? null : top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void clear() {
        top = null;
    }

    public String[] toArray() {
        int size = 0;
        Node curr = top;
        while (curr != null) {
            size++;
            curr = curr.next;
        }
        String[] arr = new String[size];
        curr = top;
        for (int i = 0; i < size; i++) {
            arr[i] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    // deep copy array (for snapshot)
    public String[] snapshot() {
        return toArray().clone();
    }
}

// Snapshot of step (log + stack states)
class StepSnapshot {
    String log;
    String[] mainStack, helper1, helper2;
    StepSnapshot(String log, String[] m, String[] h1, String[] h2) {
        this.log = log;
        this.mainStack = m;
        this.helper1 = h1;
        this.helper2 = h2;
    }
}

// Manual snapshot manager (instead of ArrayList)
class SnapshotManager {
    private StepSnapshot[] snapshots;
    private int size;

    public SnapshotManager(int capacity) {
        snapshots = new StepSnapshot[capacity];
        size = 0;
    }

    public void add(StepSnapshot s) {
        if (size < snapshots.length) {
            snapshots[size++] = s;
        }
    }

    public StepSnapshot get(int index) {
        if (index >= 0 && index < size) {
            return snapshots[index];
        }
        return null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    public StepSnapshot[] all() {
        StepSnapshot[] copy = new StepSnapshot[size];
        for (int i = 0; i < size; i++) {
            copy[i] = snapshots[i];
        }
        return copy;
    }
}

// Sorting Logic with snapshots
class StackSorter {
    MyStack mainStack = new MyStack();
    MyStack helper1 = new MyStack();
    MyStack helper2 = new MyStack();
    SnapshotManager snapshots = new SnapshotManager(1000); // capacity = 1000 steps
    int currentStep = -1;

    private void takeSnapshot(String log) {
        snapshots.add(new StepSnapshot(
                log,
                mainStack.snapshot(),
                helper1.snapshot(),
                helper2.snapshot()
        ));
    }

    public void pushColors(String[] colors) {
        for (String c : colors) {
            String col = c.trim();
            if (!col.isEmpty()) {
                mainStack.push(col);
            }
        }
    }

    public void sort() {
        snapshots.clear();
        helper1.clear();
        helper2.clear();

        while (!mainStack.isEmpty()) {
            String temp = mainStack.pop();
            takeSnapshot("Popped " + temp + " from Main Stack");

            while (!helper1.isEmpty() && helper1.peek().compareToIgnoreCase(temp) > 0) {
                String moved = helper1.pop();
                helper2.push(moved);
                takeSnapshot("Moved " + moved + " from Helper 1 → Helper 2");
            }

            helper1.push(temp);
            takeSnapshot("Pushed " + temp + " into Helper 1");

            while (!helper2.isEmpty()) {
                String movedBack = helper2.pop();
                helper1.push(movedBack);
                takeSnapshot("Moved " + movedBack + " back from Helper 2 → Helper 1");
            }
        }

        while (!helper1.isEmpty()) {
            String c = helper1.pop();
            mainStack.push(c);
            takeSnapshot("Moved " + c + " from Helper 1 → Main Stack (sorted)");
        }

        currentStep = -1;
    }

    public StepSnapshot nextStep() {
        if (currentStep + 1 < snapshots.size()) {
            currentStep++;
            return snapshots.get(currentStep);
        }
        return null;
    }

    public StepSnapshot prevStep() {
        if (currentStep - 1 >= 0) {
            currentStep--;
            return snapshots.get(currentStep);
        }
        return null;
    }

    public StepSnapshot[] allSteps() {
        return snapshots.all();
    }

    public void resetSteps() {
        currentStep = -1;
    }
}

// GUI
public class StackSorterGUI2 extends JFrame {
    private StackSorter sorter = new StackSorter();
    private JPanel mainPanel, helper1Panel, helper2Panel;
    private JTextArea logArea;
    private JTextField inputField;

    public StackSorterGUI2() {
        setTitle("Stack Sorting Visualizer");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top controls
        JPanel top = new JPanel();
        inputField = new JTextField(20);
        JButton pushBtn = new JButton("Push Colors");
        JButton startBtn = new JButton("Start");   // renamed
        top.add(new JLabel("Colors (comma separated):"));
        top.add(inputField);
        top.add(pushBtn);
        top.add(startBtn);
        add(top, BorderLayout.NORTH);

        // Center stacks view
        JPanel stacksPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel = createStackPanel("Main Stack");
        helper1Panel = createStackPanel("Helper Stack 1");
        helper2Panel = createStackPanel("Helper Stack 2");
        stacksPanel.add(mainPanel);
        stacksPanel.add(helper1Panel);
        stacksPanel.add(helper2Panel);
        add(stacksPanel, BorderLayout.CENTER);

        // Right log area
        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createTitledBorder("Operations Log"));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(400, 0));
        add(logScroll, BorderLayout.EAST);

        // Bottom buttons
        JPanel bottom = new JPanel();
        JButton prevBtn = new JButton("Prev");
        JButton nextBtn = new JButton("Next");
        JButton finalBtn = new JButton("Final");
        JButton resetBtn = new JButton("Reset");
        bottom.add(prevBtn);
        bottom.add(nextBtn);
        bottom.add(finalBtn);
        bottom.add(resetBtn);
        add(bottom, BorderLayout.SOUTH);

        // Button Actions
        pushBtn.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                sorter.pushColors(text.split(","));
                inputField.setText("");
                updateStacks(sorter.mainStack.snapshot(),
                        sorter.helper1.snapshot(),
                        sorter.helper2.snapshot());
            }
        });

        startBtn.addActionListener(e -> {
            sorter.sort();
            logArea.setText("Press Next to start...\n");
        });

        nextBtn.addActionListener(e -> {
            StepSnapshot step = sorter.nextStep();
            if (step != null) {
                logArea.append(step.log + "\n");
                updateStacks(step.mainStack, step.helper1, step.helper2);
            }
        });

        prevBtn.addActionListener(e -> {
            StepSnapshot step = sorter.prevStep();
            if (step != null) {
                logArea.setText(step.log + "\n");
                updateStacks(step.mainStack, step.helper1, step.helper2);
            }
        });

        finalBtn.addActionListener(e -> {
            logArea.setText("");
            for (StepSnapshot step : sorter.allSteps()) {
                logArea.append(step.log + "\n");
            }
            StepSnapshot[] all = sorter.allSteps();
            if (all.length > 0) {
                StepSnapshot last = all[all.length - 1];
                updateStacks(last.mainStack, last.helper1, last.helper2);
            }
        });

        resetBtn.addActionListener(e -> {
            sorter = new StackSorter();
            logArea.setText("");
            updateStacks(new String[]{}, new String[]{}, new String[]{});
        });

        setVisible(true);
    }

    private JPanel createStackPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void updateStacks(String[] main, String[] h1, String[] h2) {
        updatePanel(mainPanel, main);
        updatePanel(helper1Panel, h1);
        updatePanel(helper2Panel, h2);
    }

    private void updatePanel(JPanel panel, String[] items) {
        panel.removeAll();
        for (String s : items) {
            JLabel label = new JLabel(s, SwingConstants.CENTER);
            panel.add(label);
        }
        panel.revalidate();
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StackSorterGUI2::new);
    }
}
