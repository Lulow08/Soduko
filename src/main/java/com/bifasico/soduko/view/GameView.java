package com.bifasico.soduko.view;

import com.bifasico.soduko.model.Board;
import com.bifasico.soduko.model.Cell;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Responsible for all JavaFX node creation, styling, and updates for the board grid
 * and digit toolbar. No model logic, no event routing — only visual representation.
 */
public class GameView {

    private static final int GRID_SIZE = Board.GRID_SIZE;

    private final GridPane boardGrid;
    private final HBox toolbar;
    private final Button[] digitButtons;
    private final StackPane[][] cellPanes;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int selectedDigit = -1;

    public GameView(GridPane boardGrid, HBox toolbar, Button[] digitButtons) {
        this.boardGrid = boardGrid;
        this.toolbar = toolbar;
        this.digitButtons = digitButtons;
        this.cellPanes = new StackPane[GRID_SIZE][GRID_SIZE];
    }

    /**
     * Builds the 6x6 cell grid and attaches the provided event handlers.
     *
     * @param clickHandler called when a cell is clicked (MouseEvent, row, col)
     * @param keyHandler   called when a key is pressed on a focused cell (KeyEvent, row, col)
     */
    public void buildGrid(TriConsumer<MouseEvent, Integer, Integer> clickHandler,
                          TriConsumer<KeyEvent, Integer, Integer> keyHandler) {
        boardGrid.getChildren().clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane pane = createCellPane(row, col, clickHandler, keyHandler);
                cellPanes[row][col] = pane;
                boardGrid.add(pane, col, row);
            }
        }
    }

    /**
     * Renders all cells according to current board state (after board generation).
     */
    public void renderBoard(Board board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                applyCell(row, col, board.getCellAt(row, col), CellState.NORMAL);
            }
        }
    }

    /**
     * Updates the visual state of a single cell.
     */
    public void refreshCell(int row, int col, Cell cell, CellState state) {
        applyCell(row, col, cell, state);
    }

    /**
     * Highlights the selected cell and dims its row/col peers.
     */
    public void updateSelection(int row, int col, Board board) {
        selectedRow = row;
        selectedCol = col;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                applyCell(r, c, board.getCellAt(r, c), resolveHighlightState(r, c, board.getCellAt(r, c)));
            }
        }
        cellPanes[row][col].requestFocus();
    }

    /**
     * Applies a hint animation: indigo background fades back to normal over 1.2s.
     */
    public void animateHint(int row, int col, Cell cell) {
        StackPane pane = cellPanes[row][col];
        applyCell(row, col, cell, CellState.HINT);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(1200), e -> applyCell(row, col, cell, CellState.NORMAL))
        );
        timeline.play();
    }

    /**
     * Marks a digit button as selected (white fill).
     */
    public void selectDigitButton(int digit) {
        selectedDigit = digit;
        for (int i = 0; i < digitButtons.length; i++) {
            String completed = "btn-digit-completed";
            if (digitButtons[i].getStyleClass().contains(completed)) {
                continue;
            }
            digitButtons[i].getStyleClass().remove("btn-digit-selected");
            if (i + 1 == digit) {
                digitButtons[i].getStyleClass().add("btn-digit-selected");
            }
        }
    }

    /**
     * Marks a digit button as completed (outline only, greyed out) when all 6
     * instances of that digit are correctly placed on the board.
     */
    public void markDigitCompleted(int digit) {
        Button btn = digitButtons[digit - 1];
        btn.getStyleClass().removeAll("btn-digit", "btn-digit-selected");
        btn.getStyleClass().add("btn-digit-completed");
        if (selectedDigit == digit) {
            selectedDigit = -1;
        }
    }

    /**
     * Returns the currently selected digit from the toolbar (-1 if none).
     */
    public int getSelectedDigit() {
        return selectedDigit;
    }

    /**
     * Clears selection state (selected row/col/digit).
     */
    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        selectedDigit = -1;
        for (Button btn : digitButtons) {
            btn.getStyleClass().remove("btn-digit-selected");
        }
    }

    /* ── Private helpers ── */

    private StackPane createCellPane(int row, int col,
                                     TriConsumer<MouseEvent, Integer, Integer> clickHandler,
                                     TriConsumer<KeyEvent, Integer, Integer> keyHandler) {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("cell");
        applyBorderStyleClasses(label, row, col);

        StackPane pane = new StackPane(label);
        pane.setAlignment(Pos.CENTER);
        pane.setFocusTraversable(true);
        pane.setOnMouseClicked(e -> clickHandler.accept(e, row, col));
        pane.setOnKeyPressed(e -> keyHandler.accept(e, row, col));
        return pane;
    }

    private void applyCell(int row, int col, Cell cell, CellState state) {
        StackPane pane = cellPanes[row][col];
        Label label = (Label) pane.getChildren().get(0);

        int displayValue = cell.getDisplayValue();
        label.setText(displayValue == 0 ? "" : String.valueOf(displayValue));

        removeStateStyleClasses(label);

        if (cell.isGiven()) {
            if (state == CellState.SELECTED) {
                label.getStyleClass().add("cell-given-selected");
            } else if (state == CellState.HIGHLIGHT) {
                label.getStyleClass().add("cell-given-highlight");
            } else {
                label.getStyleClass().add("cell-given");
            }
            return;
        }

        if (state == CellState.SELECTED) {
            label.getStyleClass().add("cell-selected");
        } else if (state == CellState.HIGHLIGHT) {
            label.getStyleClass().add("cell-highlight");
        } else if (state == CellState.ERROR) {
            label.getStyleClass().add("cell-error");
        } else if (state == CellState.HINT) {
            label.getStyleClass().add("cell-hint");
        } else if (cell.isMarked()) {
            label.getStyleClass().add("cell-correct");
        }
    }

    private void removeStateStyleClasses(Label label) {
        label.getStyleClass().removeAll(
                "cell-given", "cell-given-selected", "cell-given-highlight",
                "cell-selected", "cell-highlight",
                "cell-correct", "cell-error", "cell-hint"
        );
    }

    private void applyBorderStyleClasses(Label label, int row, int col) {
        boolean regionRight  = (col == 2);
        boolean regionBottom = (row == 1 || row == 3);
        boolean lastCol      = (col == 5);
        boolean lastRow      = (row == 5);

        if (lastCol)  label.getStyleClass().add("cell-col5");
        if (lastRow)  label.getStyleClass().add("cell-row5");

        if (regionRight && regionBottom) {
            label.getStyleClass().add("cell-region-corner");
        } else if (regionRight) {
            label.getStyleClass().add("cell-region-right");
        } else if (regionBottom) {
            label.getStyleClass().add("cell-region-bottom");
        }
    }

    private CellState resolveHighlightState(int row, int col, Cell cell) {
        if (row == selectedRow && col == selectedCol) {
            return CellState.SELECTED;
        }
        if (row == selectedRow || col == selectedCol) {
            return CellState.HIGHLIGHT;
        }
        return CellState.NORMAL;
    }

    /**
     * Visual states a cell can be rendered in.
     */
    public enum CellState {
        NORMAL, SELECTED, HIGHLIGHT, CORRECT, ERROR, HINT
    }

    /**
     * Functional interface for three-argument event lambdas.
     */
    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}