package com.bifasico.soduko.controller.cellInputHandler;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Contract for handling player input directed at the Sudoku board.
 */
public interface ICellInputListener {

    /**
     * Called when the player presses a key while a cell is focused.
     *
     * @param event the JavaFX key event
     * @param row   absolute row of the target cell (0-5)
     * @param col   absolute column of the target cell (0-5)
     */
    void onKeyPressed(KeyEvent event, int row, int col);

    /**
     * Called when the player clicks a cell.
     *
     * @param event the JavaFX mouse event
     * @param row   absolute row of the clicked cell (0-5)
     * @param col   absolute column of the clicked cell (0-5)
     */
    void onCellClicked(MouseEvent event, int row, int col);

    /**
     * Called when the player selects a digit from the toolbar.
     *
     * @param digit the digit selected (1-6)
     */
    void onDigitSelected(int digit);

    /**
     * Called when the player requests a hint.
     */
    void onHintRequested();

    /**
     * Called when the player triggers undo.
     */
    void onUndo();
}