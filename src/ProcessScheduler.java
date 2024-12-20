import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class ProcessScheduler extends JFrame {

    private JTextField numProcessesField, arrivalTimeField, burstTimeField, priorityField, timeQuantumField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ProcessScheduler() {
        setTitle("Process Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Number of Processes:"));
        numProcessesField = new JTextField();
        inputPanel.add(numProcessesField);

        inputPanel.add(new JLabel("Arrival Times (comma-separated):"));
        arrivalTimeField = new JTextField();
        inputPanel.add(arrivalTimeField);

        inputPanel.add(new JLabel("Burst Times (comma-separated):"));
        burstTimeField = new JTextField();
        inputPanel.add(burstTimeField);

        inputPanel.add(new JLabel("Priorities (comma-separated):"));
        priorityField = new JTextField();
        inputPanel.add(priorityField);

        inputPanel.add(new JLabel("Time Quantum (for Round Robin):"));
        timeQuantumField = new JTextField();
        inputPanel.add(timeQuantumField);

        // Create a panel to hold the button and table
        JPanel buttonAndTablePanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton fcfsButton = new JButton("FCFS");
        JButton sjfButton = new JButton("SJF");
        JButton srtButton = new JButton("SRT");
        JButton rrButton = new JButton("Round Robin");
        JButton priorityButton = new JButton("Priority");
        buttonPanel.add(fcfsButton);
        buttonPanel.add(sjfButton);
        buttonPanel.add(srtButton);
        buttonPanel.add(rrButton);
        buttonPanel.add(priorityButton);


        tableModel = new DefaultTableModel(new Object[]{"Process ID", "Arrival Time", "Burst Time", "Priority", "Start Time", "Completion Time", "Waiting Time", "Turnaround Time"}, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // Add button panel to the north of the new panel
        buttonAndTablePanel.add(buttonPanel, BorderLayout.NORTH);
        // Add the scroll pane to the center of the new panel
        buttonAndTablePanel.add(scrollPane, BorderLayout.CENTER);


        add(inputPanel, BorderLayout.NORTH);
         add(buttonAndTablePanel, BorderLayout.CENTER); // Add the new panel

        fcfsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler(1); // FCFS
            }
        });
        sjfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler(2); // SJF
            }
        });
        srtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler(3); // SRT
            }
        });
        rrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler(4); // Round Robin
            }
        });
        priorityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler(5); // Priority
            }
        });
    }

    // Main function to run the scheduler
    private void runScheduler(int algorithm) {
        clearTable();

        try {
            int numProcesses = Integer.parseInt(numProcessesField.getText());
            String[] arrivalTimes = arrivalTimeField.getText().split(",");
            String[] burstTimes = burstTimeField.getText().split(",");
            String[] priorities = priorityField.getText().split(",");
            int timeQuantum = 0;

            if(algorithm == 4){
             timeQuantum = Integer.parseInt(timeQuantumField.getText());
            }

            if (numProcesses != arrivalTimes.length || numProcesses != burstTimes.length || numProcesses != priorities.length) {
                JOptionPane.showMessageDialog(this, "Number of processes do not match input size", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int[] processId = new int[numProcesses];
            int[] arrivalTime = new int[numProcesses];
            int[] burstTime = new int[numProcesses];
            int[] priority = new int[numProcesses];
            int[] remainingTime = new int[numProcesses];
            int[] startTime = new int[numProcesses];
            int[] completionTime = new int[numProcesses];
            int[] waitingTime = new int[numProcesses];
            int[] turnaroundTime = new int[numProcesses];

            for (int i = 0; i < numProcesses; i++) {
                processId[i] = i;
                arrivalTime[i] = Integer.parseInt(arrivalTimes[i].trim());
                burstTime[i] = Integer.parseInt(burstTimes[i].trim());
                priority[i] = Integer.parseInt(priorities[i].trim());
                remainingTime[i] = burstTime[i]; //Copy burst time for SRT
            }
            
              switch (algorithm) {
                case 1:
                    fcfs(processId, arrivalTime, burstTime, startTime, completionTime, waitingTime, turnaroundTime);
                    break;
                case 2:
                    sjf(processId, arrivalTime, burstTime, startTime, completionTime, waitingTime, turnaroundTime);
                    break;
                case 3:
                    srt(processId, arrivalTime, burstTime, remainingTime, startTime, completionTime, waitingTime, turnaroundTime);
                    break;
                  case 4:
                    roundRobin(processId, arrivalTime, burstTime, remainingTime, startTime, completionTime, waitingTime, turnaroundTime, timeQuantum);
                      break;
                  case 5:
                      priorityScheduling(processId, arrivalTime, burstTime, priority, startTime, completionTime, waitingTime, turnaroundTime);
                      break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid algorithm selected", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
             // Add results to the table
            for (int i = 0; i < numProcesses; i++) {
                tableModel.addRow(new Object[]{processId[i], arrivalTime[i], burstTime[i], priority[i], startTime[i], completionTime[i], waitingTime[i], turnaroundTime[i]});
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Error Occured, Check your input.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void clearTable(){
        tableModel.setRowCount(0);
    }
    
      // FCFS Scheduling Algorithm
    private static void fcfs(int[] processId, int[] arrivalTime, int[] burstTime, int[] startTime,
                             int[] completionTime, int[] waitingTime, int[] turnaroundTime) {
        int n = processId.length;
        int currentTime = 0;

        for (int i = 0; i < n; i++) {
             startTime[i] = Math.max(currentTime, arrivalTime[i]);
            completionTime[i] = startTime[i] + burstTime[i];
            turnaroundTime[i] = completionTime[i] - arrivalTime[i];
            waitingTime[i] = turnaroundTime[i] - burstTime[i];
            currentTime = completionTime[i];
        }
    }


     // SJF Scheduling Algorithm
    private static void sjf(int[] processId, int[] arrivalTime, int[] burstTime, int[] startTime, int[] completionTime, int[] waitingTime, int[] turnaroundTime) {
            int n = processId.length;
            int currentTime = 0;
            boolean[] completed = new boolean[n];
            int completedProcesses = 0;

            while (completedProcesses < n) {
                int shortestJob = -1;
                int minBurstTime = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (!completed[i] && arrivalTime[i] <= currentTime && burstTime[i] < minBurstTime) {
                        shortestJob = i;
                        minBurstTime = burstTime[i];
                    }
                }

                if (shortestJob != -1) {
                     startTime[shortestJob] = Math.max(currentTime, arrivalTime[shortestJob]);
                    completionTime[shortestJob] = startTime[shortestJob] + burstTime[shortestJob];
                    turnaroundTime[shortestJob] = completionTime[shortestJob] - arrivalTime[shortestJob];
                    waitingTime[shortestJob] = turnaroundTime[shortestJob] - burstTime[shortestJob];
                    currentTime = completionTime[shortestJob];
                    completed[shortestJob] = true;
                    completedProcesses++;
                } else {
                    currentTime++; // if no job is available increase time
                }
            }
    }
    
        // SRT Scheduling Algorithm
    private static void srt(int[] processId, int[] arrivalTime, int[] burstTime, int[] remainingTime, int[] startTime, int[] completionTime, int[] waitingTime, int[] turnaroundTime) {
           int n = processId.length;
            int currentTime = 0;
            int completedProcesses = 0;
            boolean[] completed = new boolean[n];

            while (completedProcesses < n) {
                int shortestJob = -1;
                int minRemainingTime = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (!completed[i] && arrivalTime[i] <= currentTime && remainingTime[i] < minRemainingTime) {
                        shortestJob = i;
                        minRemainingTime = remainingTime[i];
                    }
                }
                if (shortestJob != -1) {
                   if (remainingTime[shortestJob] == burstTime[shortestJob]) {
                       startTime[shortestJob] = Math.max(currentTime, arrivalTime[shortestJob]);
                   }
                    remainingTime[shortestJob]--;
                    currentTime++;

                    if(remainingTime[shortestJob] == 0){
                        completionTime[shortestJob] = currentTime;
                        turnaroundTime[shortestJob] = completionTime[shortestJob] - arrivalTime[shortestJob];
                        waitingTime[shortestJob] = turnaroundTime[shortestJob] - burstTime[shortestJob];
                        completed[shortestJob] = true;
                        completedProcesses++;
                    }
                } else {
                   currentTime++;
                }

            }

    }

    // Round Robin Scheduling Algorithm
    private static void roundRobin(int[] processId, int[] arrivalTime, int[] burstTime, int[] remainingTime, int[] startTime, int[] completionTime, int[] waitingTime, int[] turnaroundTime, int timeQuantum) {
            int n = processId.length;
        int currentTime = 0;
        int completedProcesses = 0;
        boolean[] completed = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();


        for(int i=0; i< n; i++) {
            if(arrivalTime[i] == 0) queue.add(i);
        }

            while (completedProcesses < n) {
                if (queue.isEmpty()) {
                    currentTime++;
                   for(int i = 0; i < n; i++)
                   {
                       if(arrivalTime[i] <= currentTime && !completed[i] && !queue.contains(i))
                           queue.add(i);
                   }
                    if(queue.isEmpty())
                    continue; // if there is still no jobs
                }

                int currentProcess = queue.poll();
                if (remainingTime[currentProcess] == burstTime[currentProcess]) {
                   startTime[currentProcess] = Math.max(currentTime, arrivalTime[currentProcess]);
                }

                int executionTime = Math.min(timeQuantum, remainingTime[currentProcess]);
                remainingTime[currentProcess] -= executionTime;
                currentTime += executionTime;

                for(int i = 0; i < n; i++) {
                   if(arrivalTime[i] <= currentTime && !completed[i] && i != currentProcess && !queue.contains(i))
                       queue.add(i);
                }


                if(remainingTime[currentProcess] == 0) {
                    completionTime[currentProcess] = currentTime;
                    turnaroundTime[currentProcess] = completionTime[currentProcess] - arrivalTime[currentProcess];
                    waitingTime[currentProcess] = turnaroundTime[currentProcess] - burstTime[currentProcess];
                    completed[currentProcess] = true;
                    completedProcesses++;
                }
                else {
                     queue.add(currentProcess);
                }
            }

    }

    //Priority Scheduling Algorithm
      private static void priorityScheduling(int[] processId, int[] arrivalTime, int[] burstTime, int[] priority, int[] startTime, int[] completionTime, int[] waitingTime, int[] turnaroundTime) {
            int n = processId.length;
            int currentTime = 0;
            boolean[] completed = new boolean[n];
            int completedProcesses = 0;

            while (completedProcesses < n) {
                int highestPriorityProcess = -1;
                int minPriority = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (!completed[i] && arrivalTime[i] <= currentTime && priority[i] < minPriority) {
                        highestPriorityProcess = i;
                        minPriority = priority[i];
                    }
                }
                if (highestPriorityProcess != -1) {
                     startTime[highestPriorityProcess] = Math.max(currentTime, arrivalTime[highestPriorityProcess]);
                    completionTime[highestPriorityProcess] = startTime[highestPriorityProcess] + burstTime[highestPriorityProcess];
                    turnaroundTime[highestPriorityProcess] = completionTime[highestPriorityProcess] - arrivalTime[highestPriorityProcess];
                    waitingTime[highestPriorityProcess] = turnaroundTime[highestPriorityProcess] - burstTime[highestPriorityProcess];
                    currentTime = completionTime[highestPriorityProcess];
                    completed[highestPriorityProcess] = true;
                    completedProcesses++;

                } else {
                    currentTime++; // if no job is available increase time
                }
            }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProcessScheduler scheduler = new ProcessScheduler();
            scheduler.setVisible(true);
        });
    }
}