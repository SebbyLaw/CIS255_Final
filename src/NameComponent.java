import java.util.*;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;

/**
 * Maintains a collection of NameRecords and graphs their data.
 */
public class NameComponent extends JComponent  {
    public static int SIZE = 600;
    public static final Color[] COLORS = new Color[] { Color.BLACK, Color.RED, Color.BLUE, Color.DARK_GRAY };
    
    // Space at top and bottom of graph
    public static final int SPACE = 20;
    
    // records currently being displayed on the graph
    private final ArrayList<NameRecord> records;
    
    public NameComponent() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        records = new ArrayList<>();
    }
    
    /**
     * Add a record to this component to be graphed.
     * Ignores duplicate records.
     *
     * @param record The NameRecord to add
     */
    public void addName(NameRecord record) {
        if (!records.contains(record)) {
            records.add(record);
            repaint();
        }
    }
    
    /**
     * Remove the earliest added name from the graph and repaints.
     */
    public void clearOne() {
        if (!records.isEmpty()) {
            records.remove(0);
            repaint();
        }
    }
    
    /**
     * Clears all records from the graph and repaints.
     */
    public void clearAll() {
        records.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // drawing the graph
        g.setColor(Color.BLACK);
        // draw horizontal lines on top of bottom of graph
        g.drawLine(0, SPACE, getWidth(), SPACE); // top
        g.drawLine(0, getHeight() - SPACE, getWidth(), getHeight() - SPACE); // bottom
        
        // draw vertical lines for the graph
        int gap = getWidth() / NameRecord.DECADES;
        for (int i = 0; i < NameRecord.DECADES; i++) {
            g.drawLine(gap * i, 0, gap * i, getHeight());
            // also write the decade string next to the vertical line
            g.drawString(String.valueOf(i * 10 + NameRecord.START), gap * i, getHeight());
        }
        
        // draw the line for each name record
        for (int i = 0; i < records.size(); i++) {
            NameRecord record = records.get(i);
            Color color = COLORS[i % COLORS.length];
            
            g.setColor(color);
            // draw the label for the first decade, since the loop skips the first one
            g.drawString(String.format("%s %d", record.getName(), record.getRank(0)), 0, rankHeight(record.getRank(0)));
            // draw the rest of the lines and the labels
            for (int decade = 1; decade < NameRecord.DECADES; decade++) {
                // draw the line between decade-1 and decade
                int x1 = (decade - 1) * gap;
                int y1 = rankHeight(record.getRank(decade - 1));
                int x2 = x1 + gap;
                int y2 = rankHeight(record.getRank(decade));
                
                g.drawLine(x1, y1, x2, y2);
                // also draw the label for this decade
                g.drawString(String.format("%s %d", record.getName(), record.getRank(decade)), x2, y2);
            }
        }
    }
    
    /**
     * A helper method to convert rank number to height on the component graph.
     *
     * @param rank The rank to convert
     * @return An integer for the y-value at which the rank should be on the graph
     */
    private int rankHeight(int rank) {
        // if the value is null (0) or over 1000, it should be at the bottom of the graph.
        if (rank == 0 || rank >= 1000) return getHeight() - SPACE;
        
        // the full height of the graph is the height of the component minus the two spaces
        // at the top and bottom of the component (above and below the horizontal lines)
        int full = getHeight() - SPACE * 2;
        // if we divide the height into 1000 pieces, the rank should be x out of 1000 pieces.
        // We subtract `1` because rank 1 should be at the very top of the graph,
        // and add SPACE to account for the space at the top of the graph.
        return full * rank / 1000 - 1 + SPACE;
    }
}
