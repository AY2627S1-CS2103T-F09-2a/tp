package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    /** Fewest rows shown, matching the original fixed height. */
    public static final int MIN_ROWS = 3;
    /** Most rows shown before the feedback scrolls, so the student list stays visible. */
    public static final int MAX_ROWS = 10;

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a result display sized for short feedback.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.setPrefRowCount(MIN_ROWS);
    }

    /**
     * Shows {@code feedbackToUser}, growing the display to fit up to {@code MAX_ROWS} lines.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        int lineCount = feedbackToUser.split("\n", -1).length;
        resultDisplay.setPrefRowCount(Math.max(MIN_ROWS, Math.min(MAX_ROWS, lineCount)));
    }

}
