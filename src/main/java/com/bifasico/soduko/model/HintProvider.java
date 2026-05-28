package com.bifasico.soduko.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Provides hint suggestions to the player by finding an empty cell that can
 * be filled with a valid number according to Sudoku rules.
 *
 * <p>The hint reveals the correct solution value for one randomly chosen empty cell
 * without solving the entire board. This fulfills HU-4 (Opción de Ayuda) from
 * the project requirements.
 */
public class HintProvider {

    private final Random random;

    /**
     * Constructs a HintProvider.
     */
    public HintProvider() {
        this.random = new Random();
    }

    /**
     * Finds a random empty cell in the board and returns it as a hint.
     * The cell will have its solution value ready to display.
     *
     * @param board the current game board
     * @return a random empty Cell with its solution value, or null if no empty cells remain
     */
    public Cell findHintCell(Board board) {
        ArrayList<Cell> emptyCells = collectEmptyCells(board);
        if (emptyCells.isEmpty()) {
            return null;
        }
        Collections.shuffle(emptyCells, random);
        return emptyCells.get(0);
    }

    private ArrayList<Cell> collectEmptyCells(Board board) {
        ArrayList<Cell> empty = new ArrayList<>();
        for (Region region : board.getRegions()) {
            for (Cell cell : region.getCells()) {
                if (!cell.isGiven() && cell.getGuess() == 0) {
                    empty.add(cell);
                }
            }
        }
        return empty;
    }
}