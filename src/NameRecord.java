import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Encapsulates the data for one name:
 * The name string and its rank numbers over the years.
 */
public class NameRecord {
    public static final int START = 1900;
    public static final int DECADES = 11;
    
    private final String name;
    private final int[] ranks;
    
    /**
     * Construct a NameRecord
     * @param line one line from the data file
     */
    public NameRecord(String line) {
        String[] data = line.split(" ");
        name = data[0];
        
        ranks = new int[DECADES];
        for (int i = 1; i <= DECADES; i++) {
            ranks[i - 1] = Integer.parseInt(data[i]);
        }
    }
    
    /**
     * @return The name of this NameRecord.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the rank of the name given the decade.
     *
     * @param decade The decade to get the rank of
     * @return The rank of the name in the decade given
     */
    public int getRank(int decade) {
        return ranks[decade];
    }
    
    /**
     * Returns the year when the name was most popular, using the earliest year in the event of a tie.
     * Ignores unranked years with `0` as the datapoint.
     *
     * @return The best year for this name e.g. 1940
     */
    public int bestYear() {
        int best = Integer.MAX_VALUE;
        int bestYear = START;
        
        for (int i = 0; i < DECADES; i++) {
            int rank = ranks[i];
            if (rank > 0 && rank < best) {
                best = rank;
                bestYear = i * 10 + START;
            }
        }
        
        return bestYear;
    }
}
