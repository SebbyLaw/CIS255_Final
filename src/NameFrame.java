import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Frame that contains the NameComponent and manages the overall application.
 * Stores the overall collection of NameRecords, installs
 * the NameComponent, deals with the controls and messaging the
 * NameComponent.
 */
class NameFrame extends JFrame implements ActionListener {
	// Controls if main() does a doSearch()
	public static final boolean SEARCH = false;
	private final ArrayList<NameRecord> records;
    
    private final NameComponent graph;
    
    private final JTextField textField;
    private final JButton graphButton;
    private final JButton clearAllButton;
    private final JButton clearOneButton;
    private final JButton searchButton;
    private final JTextArea searchOutput;
    
    public NameFrame() {
        setTitle("Baby Name Trends");
        records = new ArrayList<>();
        
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        
        graph = new NameComponent();
        container.add(graph, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        container.add(panel, BorderLayout.SOUTH);
        
        textField = new JTextField(15);
        graphButton = new JButton("Graph");
        clearAllButton = new JButton("Clear All");
        clearOneButton = new JButton("Clear One");
        searchButton = new JButton("Search");
        searchOutput = new JTextArea();
    
        searchOutput.setEditable(false);
        searchOutput.setLineWrap(true);
        searchOutput.setWrapStyleWord(true);
        searchOutput.setPreferredSize(new Dimension(300, 40));
        
        panel.add(textField);
        panel.add(graphButton);
        panel.add(clearAllButton);
        panel.add(clearOneButton);
        panel.add(searchButton);
        panel.add(searchOutput);
        
        textField.addActionListener(this);
        graphButton.addActionListener(this);
        clearAllButton.addActionListener(this);
        clearOneButton.addActionListener(this);
        searchButton.addActionListener(this);
        
        textField.setFocusable(true);
        graphButton.setFocusable(true);
        clearAllButton.setFocusable(true);
        clearOneButton.setFocusable(true);
        searchButton.setFocusable(true);
        searchOutput.setFocusable(true);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
    }
    
    /**
     * Finds a name from the list of name records and return it if found.
     *
     * @param name The name string to find
     * @return The NameRecord if it is found, or null if it is not found
     */
    public NameRecord findName(String name) {
        for (NameRecord record : records) {
            if (record.getName().equalsIgnoreCase(name)) return record;
        }
        
        return null;
    }
    
    /*
    Regex string that matches all possible decades
    e.g: "1900|1910|1920|1930|1940|1950|1960|1970|1980|1990|2000"
    This string will update if the dataset updates to include more years.
     */
    public static final String decadeRegex = IntStream.range(0, NameRecord.DECADES)
            .map(n -> n * 10 + NameRecord.START)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining("|"));
    
    /**
     * Get a Stream of the top NameRecords in a given decade.
     *
     * @param decade The decade to get the top names of
     * @param numElements The number of top elements to include in the stream
     * @return A Stream of the n top NameRecords
     */
    public Stream<NameRecord> mostPopular(int decade, int numElements) {
        int decadeIndex = (decade - NameRecord.START) / 10;
        
        return records.stream()
                .filter(r -> r.getRank(decadeIndex) != 0)  // values of 0 are actually "null". remove them
                .sorted(Comparator.comparingInt(r -> r.getRank(decadeIndex)))
                .limit(numElements);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(graphButton) || source.equals(textField)) {
            if (textField.getText().matches(decadeRegex)) {
                // add the top 5 names of that decade to the graph
                mostPopular(Integer.parseInt(textField.getText()), 5).forEach(graph::addName);
            } else {
                NameRecord record = findName(textField.getText());
                if (record != null) {
                    graph.addName(record);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        } else if (source.equals(clearAllButton)) {
            graph.clearAll();
        } else if (source.equals(clearOneButton)) {
            graph.clearOne();
        } else if (source.equals(searchButton)) {
            searchOutput.setText(search(textField.getText()).map(NameRecord::getName).collect(Collectors.joining(", ")));
        }
    }
    
    /**
     * Read and load the NameRecords from a file
     * @param filename The filename to read the data from
     */
    public void read(String filename) {
        try {
            Scanner in = new Scanner(new File(filename));
            while (in.hasNextLine()) {
                records.add(new NameRecord(in.nextLine()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Search all the names a Stream of NameRecord that contain the substring.
     * This method is Case-Insensitive.
     *
     * @param target The substring to search for
     * @return A Stream of NameRecords
     */
    public Stream<NameRecord> search(String target) {
        return records.stream().filter(r -> r.getName().toLowerCase().contains(target.toLowerCase()));
    }
    
	public static void main(String[] args) {
		NameFrame frame = new NameFrame();
		frame.read("names-data.txt");
	}
}
