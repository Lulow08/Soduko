package com.bifasico.soduko.controller.cellInputHandler;


import com.bifasico.soduko.model.BoardManager;
import com.bifasico.soduko.model.Cell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Concrete input handler. Translates raw JavaFX events into model calls
 * and notifies the controller via {@link CellInputCallback}.
 * No knowledge of any JavaFX node or view class.
 */
public class CellInputHandler extends CellInputAdapter {

    private final BoardManager boardManager;
    private final CellInputCallback callback;

    public CellInputHandler(BoardManager boardManager, CellInputCallback callback) {
        this.boardManager = boardManager;
        this.callback = callback;
    }

    @Override
    public void onKeyPressed(KeyEvent event, int row, int col) {
        String text = event.getText();
        if (text != null && !text.isEmpty()) {
            char key = text.charAt(0);
            if (key >= '1' && key <= '6') {
                int digit = Character.getNumericValue(key);
                java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions =
                        boardManager.getBoard().getConflictingCells(row, col, digit);
                boolean valid = boardManager.placeGuess(row, col, digit);
                callback.onGuessPlaced(row, col, digit, valid, collisions);
                if (boardManager.isGameWon()) {
                    callback.onGameWon();
                }
                return;
            }
        }
        if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            boardManager.undoLastGuess();
            callback.onGuessCleared(row, col);
        }
    }

    @Override
    public void onCellClicked(MouseEvent event, int row, int col) {
        callback.onCellSelected(row, col);
    }

    @Override
    public void onDigitSelected(int digit) {
        callback.onDigitSelected(digit);
    }

    @Override
    public void onHintRequested() {
        Cell hint = boardManager.requestHint();
        if (hint == null) {
            return;
        }
        // Collect cells that will conflict with this hint BEFORE placing it,
        // so the hint cell itself is never marked as error.
        java.util.ArrayList<Cell> collisions =
                boardManager.getBoard().getConflictingCells(hint.getRow(), hint.getColumn(), hint.getValue());
        boardManager.placeGuess(hint.getRow(), hint.getColumn(), hint.getValue());
        callback.onHintApplied(hint.getRow(), hint.getColumn(), collisions);
        if (boardManager.isGameWon()) {
            callback.onGameWon();
        }
    }

    @Override
    public void onUndo() {
        int[] undone = boardManager.undoLastGuess();
        if (undone != null) {
            callback.onGuessCleared(undone[0], undone[1]);
        }
    }

    /**
     * Callback implemented by the controller.
     */
    public interface CellInputCallback {
        void onGuessPlaced(int row, int col, int value, boolean valid,
                           java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions);
        void onGuessCleared(int row, int col);
        void onCellSelected(int row, int col);
        void onDigitSelected(int digit);
        void onHintApplied(int row, int col,
                           java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions);
        void onGameWon();
    }
}