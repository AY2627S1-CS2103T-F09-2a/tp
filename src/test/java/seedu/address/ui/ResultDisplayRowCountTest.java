package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks the result display's row count without starting JavaFX.
 */
public class ResultDisplayRowCountTest {

    @Test
    public void rowCountFor_shortFeedback_returnsMinimum() {
        assertEquals(ResultDisplay.MIN_ROWS, ResultDisplay.rowCountFor(""));
        assertEquals(ResultDisplay.MIN_ROWS, ResultDisplay.rowCountFor("Listed all students"));
        assertEquals(ResultDisplay.MIN_ROWS, ResultDisplay.rowCountFor("a\nb\nc"));
    }

    @Test
    public void rowCountFor_feedbackWithinBounds_returnsLineCount() {
        assertEquals(4, ResultDisplay.rowCountFor("a\nb\nc\nd"));
        assertEquals(ResultDisplay.MAX_ROWS, ResultDisplay.rowCountFor("a\n".repeat(ResultDisplay.MAX_ROWS - 1) + "a"));
    }

    @Test
    public void rowCountFor_trailingNewline_countsEmptyLastLine() {
        assertEquals(4, ResultDisplay.rowCountFor("a\nb\nc\n"));
    }

    @Test
    public void rowCountFor_wrappedLines_countsEveryWrappedRow() {
        // two lines that each wrap onto three rows need six rows
        assertEquals(6, ResultDisplay.rowCountFor("long\nlong", line -> 3));
        // only the wrapped line takes extra rows
        assertEquals(5, ResultDisplay.rowCountFor("short\nlong\nshort", line -> line.equals("long") ? 3 : 1));
        // wrapped rows are still capped
        assertEquals(ResultDisplay.MAX_ROWS, ResultDisplay.rowCountFor("long\nlong\nlong\nlong", line -> 4));
    }

    @Test
    public void rowCountFor_longFeedback_returnsMaximum() {
        assertEquals(ResultDisplay.MAX_ROWS, ResultDisplay.rowCountFor("a\n".repeat(ResultDisplay.MAX_ROWS + 5)));
    }
}
