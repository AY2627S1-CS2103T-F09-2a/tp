package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.ToIntFunction;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    /** Fewest rows shown, matching the original fixed height. */
    public static final int MIN_ROWS = 3;
    /** Most rows shown before the feedback scrolls, so the student list stays visible. */
    public static final int MAX_ROWS = 10;

    /** Width taken by the text area's padding and vertical scroll bar, which text cannot use. */
    private static final double NON_TEXT_WIDTH = 40;

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a result display sized for short feedback.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.setPrefRowCount(MIN_ROWS);
        resultDisplay.widthProperty().addListener((observable, oldWidth, newWidth) -> fitRowsToText());
    }

    /**
     * Shows {@code feedbackToUser}, growing the display to fit up to {@code MAX_ROWS} rows.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        fitRowsToText();
    }

    private void fitRowsToText() {
        resultDisplay.setPrefRowCount(rowCountFor(resultDisplay.getText(), this::wrappedRowsFor));
    }

    /**
     * Returns the number of rows {@code line} occupies once wrapped to the text area's current width.
     * Before the text area has been laid out, each line counts as one row.
     */
    private int wrappedRowsFor(String line) {
        double textWidth = resultDisplay.getWidth() - NON_TEXT_WIDTH;
        if (line.isEmpty() || textWidth <= 0) {
            return 1;
        }
        Text wrapped = new Text(line);
        wrapped.setFont(resultDisplay.getFont());
        wrapped.setWrappingWidth(textWidth);
        Text oneRow = new Text("X");
        oneRow.setFont(resultDisplay.getFont());
        return Math.max(1, (int) Math.round(
                wrapped.getLayoutBounds().getHeight() / oneRow.getLayoutBounds().getHeight()));
    }

    /**
     * Returns the number of rows needed to show {@code feedbackToUser}, counting one row per line, between
     * {@code MIN_ROWS} and {@code MAX_ROWS}.
     */
    static int rowCountFor(String feedbackToUser) {
        return rowCountFor(feedbackToUser, line -> 1);
    }

    /**
     * Returns the number of rows needed to show {@code feedbackToUser}, where {@code rowsForLine} gives the
     * rows each line occupies after wrapping, between {@code MIN_ROWS} and {@code MAX_ROWS}.
     */
    static int rowCountFor(String feedbackToUser, ToIntFunction<String> rowsForLine) {
        int rowCount = Arrays.stream(feedbackToUser.split("\n", -1)).mapToInt(rowsForLine).sum();
        return Math.max(MIN_ROWS, Math.min(MAX_ROWS, rowCount));
    }

}
