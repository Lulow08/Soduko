package com.bifasico.soduko.model;

/**
 * Represents a single cell in the Sudoku board.
 * This is the leaf node of the board tree structure.
 * Each cell knows its absolute position (row, column) within the 6x6 grid.
 */
public class Cell {

    private int value;
    private int row;
    private int column;
    private int guess;
    private boolean given;
    private boolean marked;

    /**
     * Constructs a Cell with its absolute grid coordinates.
     *
     * @param row    the absolute row index (0-based) in the 6x6 grid
     * @param column the absolute column index (0-based) in the 6x6 grid
     */
    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.value = 0;
        this.guess = 0;
        this.given = false;
        this.marked = false;
    }

    /**
     * Returns the definitive solution value of this cell.
     *
     * @return the correct answer value (1-6), or 0 if not yet assigned
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the definitive solution value for this cell.
     *
     * @param value the correct answer (1-6)
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Returns the absolute row index of this cell within the 6x6 grid.
     *
     * @return zero-based row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the absolute column index of this cell within the 6x6 grid.
     *
     * @return zero-based column index
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the value guessed by the player for this cell.
     *
     * @return the player's current guess (1-6), or 0 if empty
     */
    public int getGuess() {
        return guess;
    }

    /**
     * Sets the value guessed by the player for this cell.
     *
     * @param guess the player's input (1-6), or 0 to clear
     */
    public void setGuess(int guess) {
        this.guess = guess;
    }

    /**
     * Returns whether this cell was pre-filled at the start of the game
     * and is therefore not editable by the player.
     *
     * @return true if this cell is a given (pre-filled, fixed) cell
     */
    public boolean isGiven() {
        return given;
    }

    /**
     * Marks this cell as a given (pre-filled) cell visible from game start.
     *
     * @param given true to mark as given and non-editable
     */
    public void setGiven(boolean given) {
        this.given = given;
    }

    /**
     * Returns whether the player has correctly confirmed this cell's value.
     * A marked cell has a guess that matches its solution value.
     *
     * @return true if the player has correctly filled this cell
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Sets the marked state for this cell.
     * A cell should be marked when the player's guess matches the solution.
     *
     * @param marked true if the player has correctly solved this cell
     */
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    /**
     * Returns whether this cell is currently empty from the player's perspective.
     * A given cell is never considered empty. A non-given cell is empty
     * when no guess has been entered.
     *
     * @return true if the cell has no player input and is not a given
     */
    public boolean isEmpty() {
        return !given && guess == 0;
    }

    /**
     * Returns the effective display value of this cell.
     * For given cells, returns the solution value. For player cells,
     * returns the current guess (or 0 if empty).
     *
     * @return the value to display in the UI
     */
    public int getDisplayValue() {
        return given ? value : guess;
    }

    /**
     * Resets the player's guess and marked state for this cell.
     * Has no effect on given cells.
     */
    public void clearGuess() {
        if (!given) {
            guess = 0;
            marked = false;
        }
    }
}