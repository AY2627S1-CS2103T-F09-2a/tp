package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /** Matches the characters other than letters and digits at the start or end of a word. */
    private static final Pattern EDGE_PUNCTUATION = Pattern.compile("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$");

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns true if the {@code sentence} contains the {@code word}, as {@link #containsWordIgnoreCase} does,
     * or if they match after removing punctuation at the start and end of each word.
     *   <br>examples:<pre>
     *       containsWordIgnoreCaseAndEdgePunctuation("Wei Ming Tan.", "Tan") == true
     *       containsWordIgnoreCaseAndEdgePunctuation("Ali (Junior)", "junior") == true
     *       containsWordIgnoreCaseAndEdgePunctuation("Mary-Ann Lim", "Mary") == false //not a full word match
     *       </pre>
     *
     * @param sentence Cannot be null.
     * @param word Cannot be null, cannot be empty, must be a single word.
     */
    public static boolean containsWordIgnoreCaseAndEdgePunctuation(String sentence, String word) {
        if (containsWordIgnoreCase(sentence, word)) {
            return true;
        }

        String strippedWord = stripEdgePunctuation(word.trim());
        if (strippedWord.isEmpty()) {
            return false;
        }
        return Arrays.stream(sentence.split("\\s+"))
                .map(StringUtil::stripEdgePunctuation)
                .anyMatch(strippedWord::equalsIgnoreCase);
    }

    private static String stripEdgePunctuation(String word) {
        return EDGE_PUNCTUATION.matcher(word).replaceAll("");
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "01", "+1", " 2 " (untrimmed), "3 0" (whitespace), "1 a" (letters).
     * Only ASCII digits are accepted, without leading zeros.
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && s.matches("[1-9][0-9]*");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
