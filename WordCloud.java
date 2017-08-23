import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** This program reads 2 text files and compiles word counts for each.
 *  It then eliminates rare words, and words that only occur in one
 *  document, and displays the remainder as a "word cloud" on a graphics pane,
 *  to allow the user to examine differences between the word usage in
 *  the two documents.
 */ 
public class WordCloud implements UIButtonListener {

    // Fields:
    private int numWordsToRemove = 100;

    // The two maps.
    private Map <String,Double> counts1, counts2;

    // Constructor
    /** Constructs a WordCloud object
     *  Set up the graphical user interface, and call the basic method.
     */ 
    public WordCloud() {
        // Set up the GUI.
        UI.addButton("remove standard common words", this);
        UI.addButton("remove infrequent words", this);
        UI.addButton("remove un-shared words", this);

        String fname1 = UIFileChooser.open("First filename to read text from");
        counts1 = buildHistogram(fname1);
        UI.println("Text read from " + fname1);

        String fname2 = UIFileChooser.open("Second filename to read text from");
        counts2 = buildHistogram(fname2);
        UI.println("Text read from " + fname2);

        displayWords();
    }

    /** Read the contents of a file, counting how often each word occurs.
     *  Put the counts (as Doubles) into a Map, which is returned.
     *  [CORE]
     */
    public Map <String, Double> buildHistogram(String filename) {
        if (filename == null) return null;
        Map <String,Double> wordcounts;
        double total = 0.0;
        try {
            // Open the file and get ready to read from it
            Scanner scan = new Scanner(new File(filename));

            // The next line tells Scanner to remove all punctuation
            scan.useDelimiter("[^-a-zA-Z']"); 

            wordcounts = new HashMap <String,Double> ();
            /*# YOUR CODE HERE */
            while (scan.hasNext()) {
                String word = scan.next();
                if (wordcounts.containsKey(word) && word.length()>0) {
                    wordcounts.put(word, wordcounts.get(word)+1);
                } else {
                    wordcounts.put(word, 1.0);
                }
            }

            scan.close(); // closes the scanner 
            return wordcounts;
        }
        catch(IOException ex) {
            UI.println("Could not read the file " + ex.getMessage());
            return null;
        }
    }

    /** Construct and return a Set of all the words that occur in EITHER
     *  document.
     *  [CORE]
     */
    public Set <String> findAllWords() {
        Set<String> allWords = new HashSet<String>();

        allWords.addAll(counts1.keySet());
        allWords.addAll(counts2.keySet());

        return allWords;
    }

    /** Display words that exist in both documents.
     *  
     *  The x-position is essentially random (it just depends on the order in
     *  which an iterator goes through a Set).
     *  
     *  However the y-position reflects how much the word is used in the 1st
     *  document versus the 2nd. That is, a word that is common in the 1st and
     *  uncommon in the second should appear at the top.
     *  
     *  The SIZE of the word as displayed reflects how common the word is
     *  overall, including its count over BOTH documents.
     *  NB! There is UI.setFontSize method that may come in useful!
     *  
     *  [CORE]
     */
    public void displayWords() {
        UI.clearGraphics();
        if ((counts1 == null) || (counts2 == null)) return;

        // First we re-normalise the counts.
        normaliseCounts(counts1);
        normaliseCounts(counts2);

        /*# YOUR CODE HERE */
        for (String wd: findAllWords()) {
            if (counts1.containsKey(wd) && counts2.containsKey(wd)) {
                UI.setFontSize((int)(10+(1000*(counts1.get(wd)+counts2.get(wd)))));
                UI.setColor(Color.black);	/*#CHALLENGE*/
                UI.drawString(wd, 500 * (counts1.get(wd)/(counts1.get(wd)+counts2.get(wd))),  new Random().nextInt(500), false);
            } else if ((!counts1.containsKey(wd)) && counts2.containsKey(wd)) {
                UI.setFontSize((int)(10+(1000*counts2.get(wd))));
                UI.setColor(Color.red);		/*#CHALLENGE*/
                UI.drawString(wd, 500 + counts2.get(wd), new Random().nextInt(500), false);
            } else if (counts1.containsKey(wd) && (!counts2.containsKey(wd))) {
                UI.setFontSize((int)(10+(1000*counts1.get(wd))));
                UI.setColor(Color.blue);	/*#CHALLENGE*/
                UI.drawString(wd, 500 * counts1.get(wd), new Random().nextInt(500), false);
            }
        }
        UI.repaintGraphics();
        return;
    }

    /** Take a word count Map, and a Set of words. Remove those words from the
     *  Map.
     *  [COMPLETION]
     */
    public void removeWords(Map<String,Double> wc, Set<String> words) {
        for (String wd: words) {
            if (wc.containsKey(wd)) {
                wc.remove(wd);
            }
        }
    }

    /** Takes a Map from strings to integers, and an integer,
     * limitNumWords. It should leave this Map containing only the
     * limitNumWords most common words in the original.
     * [COMPLETION]
     */
    public void removeInfrequentWords (Map<String,Double> c, int limitNumWords) {
        List<Double> list = new ArrayList<Double>();
        list.addAll(c.values());
        Collections.sort(list, Collections.reverseOrder());

        for (int i=0; i<list.size(); i++) {
            if (i > limitNumWords-1) {	//indexing starts at 0
                list.remove(i);
            }
        }

        for(Iterator<Map.Entry<String,Double>> iter = c.entrySet().iterator(); iter.hasNext();){
            Map.Entry<String, Double> entry = iter.next();
            if (!(list.contains(entry.getValue()))) {
                iter.remove();
            }
        }
    }

    /** Take a Map from words to counts, and "normalise" the counts,
     *  so that they are fractions of the total: they should sum to one.
     */
    public void normaliseCounts(Map <String, Double> counts) {
        // Figure out the total in the current Map
        if (counts == null) return;
        double total = 0.0;
        for (String wd : counts.keySet()) 
            total += counts.get(wd);

        // Divide all values by the total, so they will sum to one.
        for (String wd : counts.keySet()) {
            double count = counts.get(wd)/total;
            counts.put(wd,count);
        }
    }

    /** Print the words and their counts to standard out.
     *  Not necessary to the program, but might be useful for debugging
     */
    public void printcounts(Map <String,Double> counts ) {
        if (counts == null) {
            UI.println("The Map is empty");
            return;
        }
        for (String s : counts.keySet()) 
            UI.printf("%15s \t : \t %.3f \n",s,counts.get(s));
        UI.println("----------------------------------");
    }

    //-- GUI stuff --------------------------------------------------------
    /** Respond to button presses */
    public void buttonPerformed(String button) {

        if (button.equals("remove standard common words")) {
            String fname = "some-common-words.txt"; // More general form: UIFileChooser.open("filename to read common words from");
            if (fname == null) return;
            UI.println("Getting ignorable words from " + fname);

            // Set the elements of the toRemove Set to be the words in file
            try {
                Set <String> toRemove = new HashSet <String> ();
                Scanner scan = new Scanner(new File(fname));
                while (scan.hasNext()) {
                    String str = scan.next().toLowerCase().trim(); 
                    toRemove.add(str);
                }
                scan.close();

                // Remove the words
                removeWords(counts1, toRemove);
                removeWords(counts2, toRemove);
            }
            catch(IOException ex) {   // what to do if there is an io error.
                UI.println("Could not read the file " + ex.getMessage());
            }
        }

        else if (button.equals("remove infrequent words") ) {
            UI.println("Keeping only the most common " + numWordsToRemove 
                + " words");
            removeInfrequentWords(counts1,numWordsToRemove);
            removeInfrequentWords(counts2,numWordsToRemove);
            numWordsToRemove = numWordsToRemove/2; // It halves each time.
        }

        else if (button.equals("remove un-shared words") ) {
            UI.println("Keeping only words that occur in BOTH docs ");
            Set <String> wordsToBeRemoved = new HashSet <String> ();
            for (String wd : counts1.keySet()) 
                if (!counts2.keySet().contains(wd)) wordsToBeRemoved.add(wd);
            for (String wd : counts2.keySet()) 
                if (!counts1.keySet().contains(wd)) wordsToBeRemoved.add(wd);
            // Notice you do need to do both!
            // Now actually remove them.
            removeWords(counts1, wordsToBeRemoved);
            removeWords(counts2, wordsToBeRemoved);
        }

        // printcounts(counts1);
        // printcounts(counts2);

        // Now redo everything on the screen
        displayWords();
    }

    //================================================================
    // Main
    public static void main(String[] args) {
        new WordCloud();
    }
}
