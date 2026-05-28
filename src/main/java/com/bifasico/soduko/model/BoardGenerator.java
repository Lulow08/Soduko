package com.bifasico.soduko.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generates a valid, randomized Sudoku solution for a {@link Board} using
 * an iterative backtracking algorithm driven by a {@link SudokuStack}.
 *
 * <p>Each entry pushed onto the stack records the cell position, the value
 * placed there, and the list of remaining untried candidates for that cell.
 * When backtracking, the stack pops the last decision and tries the next
 * candidate from the saved list, guaranteeing exhaustive search without
 * recursion and without restarting from scratch.
 *
 * <p>Cells are visited in order: region 0 cell 0, region 0 cell 1, …,
 * region 5 cell 5 (36 total). A value is accepted at a given position only
 * when it does not conflict with values already placed in the same row,
 * column, or region.
 *
 * <p>{@link SudokuStack} is the primary data structure used here, fulfilling
 * the project requirement for at least one non-array data structure in the
 * board construction logic.
 */
public class BoardGenerator {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 6;
    private static final int TOTAL_CELLS = Board.GRID_SIZE * Board.GRID_SIZE;

    private final Random random;

    /**
     * Constructs a BoardGenerator that produces a different puzzle each time.
     */
    public BoardGenerator() {
        this.random = new Random();
    }

    /**
     * Fills every cell in the given board with a valid Sudoku solution and then
     * marks exactly {@link Board#GIVEN_CELLS_PER_REGION} cells per region as given.
     *
     * @param board the Board whose cells will be populated
     * @throws IllegalStateException if a valid solution cannot be generated
     */
    public void generate(Board board) {
        boolean solved = fillBoard(board);
        if (!solved) {
            throw new IllegalStateException("No se pudo generar un tablero válido");
        }
        markGivenCells(board);
    }

    private boolean fillBoard(Board board) {
        SudokuStack<StackEntry> decisionStack = new SudokuStack<>();
        int position = 0;

        while (position < TOTAL_CELLS) {
            int regionIndex = position / Board.GRID_SIZE;
            int cellIndex = position % Board.GRID_SIZE;

            Region region = board.getRegion(regionIndex);
            Cell cell = region.getCell(cellIndex);

            ArrayList<Integer> candidates = buildShuffledCandidates(cell, region, board, regionIndex);

            if (!candidates.isEmpty()) {
                int chosen = candidates.remove(0);
                cell.setValue(chosen);
                decisionStack.push(new StackEntry(position, chosen, candidates));
                position++;
            } else {
                boolean backtracked = false;
                while (!decisionStack.isEmpty()) {
                    StackEntry last = decisionStack.pop();
                    int backPosition = last.position;
                    int backRegionIdx = backPosition / Board.GRID_SIZE;
                    int backCellIdx = backPosition % Board.GRID_SIZE;

                    Cell backCell = board.getRegion(backRegionIdx).getCell(backCellIdx);
                    backCell.setValue(0);

                    ArrayList<Integer> remaining = last.remainingCandidates;
                    remaining.removeIf(v -> !isPlacementValid(backCell, v,
                            board.getRegion(backRegionIdx), board, backRegionIdx));

                    if (!remaining.isEmpty()) {
                        int retryValue = remaining.remove(0);
                        backCell.setValue(retryValue);
                        decisionStack.push(new StackEntry(backPosition, retryValue, remaining));
                        position = backPosition + 1;
                        backtracked = true;
                        break;
                    }
                }
                if (!backtracked) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Integer> buildShuffledCandidates(Cell cell, Region region,
                                                       Board board, int regionIndex) {
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int v = MIN_VALUE; v <= MAX_VALUE; v++) {
            if (isPlacementValid(cell, v, region, board, regionIndex)) {
                candidates.add(v);
            }
        }
        Collections.shuffle(candidates, random);
        return candidates;
    }

    private boolean isPlacementValid(Cell cell, int value, Region currentRegion,
                                     Board board, int currentRegionIndex) {
        for (Cell sibling : currentRegion.getCells()) {
            if (sibling != cell && sibling.getValue() == value) {
                return false;
            }
        }

        for (int i = 0; i < currentRegionIndex; i++) {
            Region visited = board.getRegion(i);
            if (visited.containsValueInRow(cell.getRow(), value)) {
                return false;
            }
            if (visited.containsValueInColumn(cell.getColumn(), value)) {
                return false;
            }
        }

        return true;
    }

    private void markGivenCells(Board board) {
        for (Region region : board.getRegions()) {
            ArrayList<Cell> cells = new ArrayList<>(region.getCells());
            Collections.shuffle(cells, random);
            for (int i = 0; i < Board.GIVEN_CELLS_PER_REGION; i++) {
                cells.get(i).setGiven(true);
            }
        }
    }

    private static final class StackEntry {

        final int position;
        final int placedValue;
        final ArrayList<Integer> remainingCandidates;

        StackEntry(int position, int placedValue, ArrayList<Integer> remainingCandidates) {
            this.position = position;
            this.placedValue = placedValue;
            this.remainingCandidates = remainingCandidates;
        }
    }
}