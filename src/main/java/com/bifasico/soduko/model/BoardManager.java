package com.bifasico.soduko.model;

/**
 * Coordinates the active game state: board tree, generation, hints, and undo history.
 * Exposes a focused API consumed exclusively by the controller layer.
 */
public class BoardManager {

    private Board board;
    private final BoardGenerator generator;
    private final HintProvider hintProvider;
    private final SudokuStack<int[]> undoStack;

    public BoardManager() {
        this.generator = new BoardGenerator();
        this.hintProvider = new HintProvider();
        this.undoStack = new SudokuStack<>();
        startNewGame();
    }

    /**
     * Generates a fresh board, replacing any existing game state.
     */
    public void startNewGame() {
        this.board = new Board();
        generator.generate(board);
        undoStack.clear();
    }

    /**
     * Returns the current board (root of the tree).
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Places a player guess, pushes to undo stack, and returns whether it is valid.
     *
     * @return true if the guess obeys Sudoku constraints
     */
    public boolean placeGuess(int row, int column, int value) {
        Cell cell = board.getCellAt(row, column);
        if (cell == null || cell.isGiven()) {
            return false;
        }
        int previous = cell.getGuess();
        cell.setGuess(value);
        boolean valid = board.isGuessValid(row, column, value);
        cell.setMarked(valid && value == cell.getValue());
        undoStack.push(new int[]{row, column, previous});
        return valid;
    }

    /**
     * Undoes the last guess, restoring the previous value.
     *
     * @return int[]{row, col} of the undone cell, or null if nothing to undo
     */
    public int[] undoLastGuess() {
        if (undoStack.isEmpty()) {
            return null;
        }
        int[] entry = undoStack.pop();
        int row = entry[0];
        int col = entry[1];
        int previous = entry[2];
        Cell cell = board.getCellAt(row, col);
        if (cell != null && !cell.isGiven()) {
            cell.setGuess(previous);
            cell.setMarked(previous != 0 && board.isGuessValid(row, col, previous) && previous == cell.getValue());
        }
        return new int[]{row, col};
    }

    /**
     * Returns whether all non-given cells are correctly filled.
     */
    public boolean isGameWon() {
        return board.isSolved();
    }

    /**
     * Returns a random empty cell with its solution value for hint display.
     */
    public Cell requestHint() {
        return hintProvider.findHintCell(board);
    }

    /**
     * Returns how many times the given digit value appears correctly placed on the board.
     * When this count reaches 6 the digit is considered complete.
     *
     * @param digit value 1-6
     * @return count of correctly marked cells with that value
     */
    public int countCorrectlyPlaced(int digit) {
        int count = 0;
        for (Region region : board.getRegions()) {
            for (Cell cell : region.getCells()) {
                if (cell.getValue() == digit && (cell.isGiven() || cell.isMarked())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Clears all player guesses without regenerating the board.
     */
    public void resetBoard() {
        board.reset();
        undoStack.clear();
    }
}