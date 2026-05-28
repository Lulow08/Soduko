package com.bifasico.soduko.model;

import java.util.ArrayList;

/**
 * Represents the root node of the Sudoku board tree structure.
 * The Board owns all 6 {@link Region} children, each of which contains 6 {@link Cell} leaves.
 *
 * <p>Tree hierarchy:
 * <pre>
 *   Board (root)
 *   └── Region × 6 (branches)
 *       └── Cell × 6 (leaves)
 * </pre>
 *
 * <p>The board represents a 6×6 Sudoku grid divided into six 2×3 regions.
 */
public class Board {

    public static final int GRID_SIZE = 6;
    public static final int REGION_COUNT = 6;
    public static final int GIVEN_CELLS_PER_REGION = 2;

    private final ArrayList<Region> regions;

    /**
     * Constructs a new Board and initializes all 6 regions with their cells.
     */
    public Board() {
        this.regions = new ArrayList<>(REGION_COUNT);
        initializeRegions();
    }

    private void initializeRegions() {
        for (int i = 0; i < REGION_COUNT; i++) {
            regions.add(new Region(i));
        }
    }

    /**
     * Returns all 6 regions (children) of this board.
     *
     * @return the list of regions in index order (0-5)
     */
    public ArrayList<Region> getRegions() {
        return regions;
    }

    /**
     * Returns the region at the specified index.
     *
     * @param index the region index (0-5)
     * @return the Region at that index
     */
    public Region getRegion(int index) {
        return regions.get(index);
    }

    /**
     * Finds and returns the cell at the given absolute grid coordinates
     * by traversing the board tree.
     *
     * @param row    the absolute row (0-5)
     * @param column the absolute column (0-5)
     * @return the Cell at that position, or null if coordinates are invalid
     */
    public Cell getCellAt(int row, int column) {
        for (Region region : regions) {
            Cell cell = region.getCellAt(row, column);
            if (cell != null) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Returns the region that owns the cell at the given absolute coordinates.
     *
     * @param row    the absolute row (0-5)
     * @param column the absolute column (0-5)
     * @return the owning Region, or null if coordinates are out of bounds
     */
    public Region getRegionFor(int row, int column) {
        int regionIndex = (row / 2) * 2 + (column / 3);
        if (regionIndex < 0 || regionIndex >= REGION_COUNT) {
            return null;
        }
        return regions.get(regionIndex);
    }

    /**
     * Validates whether the player's guess at a given cell violates any Sudoku rule.
     * Checks the row, column, and region constraints for the given coordinates and value.
     *
     * @param row    the absolute row of the cell to validate
     * @param column the absolute column of the cell to validate
     * @param value  the player's guess (1-6)
     * @return true if the guess is valid (no conflict found)
     */
    public boolean isGuessValid(int row, int column, int value) {
        return !conflictsInRow(row, column, value)
                && !conflictsInColumn(row, column, value)
                && !conflictsInRegion(row, column, value);
    }

    private boolean conflictsInRow(int row, int excludedColumn, int value) {
        for (Region region : regions) {
            for (Cell cell : region.getCells()) {
                if (cell.getRow() == row && cell.getColumn() != excludedColumn) {
                    int displayValue = cell.isGiven() ? cell.getValue() : cell.getGuess();
                    if (displayValue == value) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean conflictsInColumn(int excludedRow, int column, int value) {
        for (Region region : regions) {
            for (Cell cell : region.getCells()) {
                if (cell.getColumn() == column && cell.getRow() != excludedRow) {
                    int displayValue = cell.isGiven() ? cell.getValue() : cell.getGuess();
                    if (displayValue == value) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean conflictsInRegion(int row, int column, int value) {
        Region owningRegion = getRegionFor(row, column);
        if (owningRegion == null) {
            return false;
        }
        for (Cell cell : owningRegion.getCells()) {
            if (cell.getRow() == row && cell.getColumn() == column) {
                continue;
            }
            int displayValue = cell.isGiven() ? cell.getValue() : cell.getGuess();
            if (displayValue == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the board is fully and correctly solved.
     * Every non-given cell must have a guess matching its solution value.
     *
     * @return true if all cells are correctly filled
     */
    public boolean isSolved() {
        for (Region region : regions) {
            for (Cell cell : region.getCells()) {
                if (cell.isGiven()) {
                    continue;
                }
                if (cell.getGuess() != cell.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Resets all non-given cells, clearing player guesses and marked states.
     */
    public void reset() {
        for (Region region : regions) {
            for (Cell cell : region.getCells()) {
                if (!cell.isGiven()) {
                    cell.clearGuess();
                }
            }
        }
    }
}