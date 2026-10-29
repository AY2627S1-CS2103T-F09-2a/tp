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
    public void rowCountFor_longFeedback_returnsMaximum() {
        assertEquals(ResultDisplay.MAX_ROWS, ResultDisplay.rowCountFor("a\n".repeat(ResultDisplay.MAX_ROWS + 5)));
    }
}
