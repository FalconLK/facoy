import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class StateChartGUI extends JFrame implements ActionListener {

    Container container;

    JTabbedPane tabbedPane;

    JPanel bottomPanel, buttonPanel;

    JButton start, stop, pause, skip;

    public JTextArea logWindow;

    JScrollPane scrollWindow;

    boolean suspended;

    JButton okay, cancel;

    int iteration;

    Thread t;

    stateChart chart;

    stateEngine engine;

    messageServer mServer;

    eventList myList;

    StateChartGUI() {
        super("State Engine *alpha*");
        suspended = false;
        container = getContentPane();
        container.setLayout(new BorderLayout());
        iteration = 0;
        JPanel interf, menu;
        interf = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JMenu file;
        JMenuItem fileOpen, fileSave, fileClose;
        file = new JMenu("File");
        menuBar.add(file);
        fileOpen = new JMenuItem("Open");
        file.add(fileOpen);
        fileOpen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        fileSave = new JMenuItem("Save");
        file.add(fileSave);
        fileClose = new JMenuItem("Close");
        file.add(fileClose);
        tabbedPane = new JTabbedPane();
        logWindow = new JTextArea(7, 5);
        logWindow.setEditable(false);
        scrollWindow = new JScrollPane(logWindow, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollWindow.setPreferredSize(new Dimension(400, 100));
        start = new JButton("Start");
        stop = new JButton("Stop");
        pause = new JButton("Pause");
        skip = new JButton("Skip");
        start.addActionListener(this);
        pause.addActionListener(this);
        skip.addActionListener(this);
        container.add(menuBar, BorderLayout.PAGE_START);
        container.add(tabbedPane, BorderLayout.CENTER);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(scrollWindow, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(pause);
        buttonPanel.add(skip);
        bottomPanel.add(buttonPanel, BorderLayout.PAGE_END);
        container.add(bottomPanel, BorderLayout.PAGE_END);
        myList = new eventList();
        mServer = new messageServer(myList, 6000);
        start.setEnabled(false);
        setSize(640, 480);
        setVisible(true);
    }

    protected void open() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.addChoosableFileFilter(new chartFilter());
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) return;
        File fileName = fileChooser.getSelectedFile();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(this, "Invalid File Name", "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            chart = new stateChart(fileName.toString());
            start.setEnabled(true);
        }
    }

    class chartFilter extends javax.swing.filechooser.FileFilter {

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".ESP") || file.isDirectory();
        }

        public String getDescription() {
            return "*.ESP";
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ("Start".equals(e.getActionCommand())) {
            engine = new stateEngine(chart, myList, this, true, iteration);
            iteration++;
            t = new Thread(engine);
            t.start();
        } else if ("Pause".equals(e.getActionCommand())) {
        } else if ("Skip".equals(e.getActionCommand())) {
            t.stop();
        }
    }

    public static void main(String[] args) {
        StateChartGUI app = new StateChartGUI();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
