package com.bifasico.soduko.model;

import java.util.ArrayList;

/**
 * Represents a 2×3 region (block) in the Sudoku board.
 * This is an intermediate branch node in the board tree structure.
 * Each region contains exactly 6 {@link Cell} leaf nodes.
 *
 * <p>The 6×6 Sudoku grid is divided into 6 regions arranged as follows:
 * <pre>
 *   Region 0 | Region 1 | Region 2
 *   ---------+----------+---------
 *   Region 3 | Region 4 | Region 5
 * </pre>
 * Each region spans 2 rows and 3 columns in the absolute grid.
 */
public class Region {

    public static final int CELLS_PER_REGION = 6;

    private final int index;
    private final int startRow;
    private final int startColumn;
    private final ArrayList<Cell> cells;

    /**
     * Constructs a Region at the given index and initializes its 6 cells
     * with their absolute grid coordinates.
     *
     * @param index the region index (0-5) in reading order (left-right, top-bottom)
     */
    public Region(int index) {
        this.index = index;
        this.startRow = (index / 2) * 2;
        this.startColumn = (index % 2) * 3;
        this.cells = new ArrayList<>(CELLS_PER_REGION);
        initializeCells();
    }

    private void initializeCells() {
        for (int r = startRow; r < startRow + 2; r++) {
            for (int c = startColumn; c < startColumn + 3; c++) {
                cells.add(new Cell(r, c));
            }
        }
    }

    /**
     * Returns the index of this region within the board (0-5).
     *
     * @return the region index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the absolute row index where this region starts in the 6×6 grid.
     *
     * @return the start row (0 or 2)
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Returns the absolute column index where this region starts in the 6×6 grid.
     *
     * @return the start column (0, 3, or 6)
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Returns the list of all 6 cells in this region.
     * The cells are ordered row by row, left to right within the region.
     *
     * @return the cell list
     */
    public ArrayList<Cell> getCells() {
        return cells;
    }

    /**
     * Returns the cell at the given index within this region (0-5).
     * Cells are ordered row-first: indices 0-2 are the top row, 3-5 the bottom row.
     *
     * @param cellIndex the index within the region (0-5)
     * @return the corresponding Cell
     * @throws IndexOutOfBoundsException if cellIndex is out of range
     */
    public Cell getCell(int cellIndex) {
        return cells.get(cellIndex);
    }

    /**
     * Returns the cell at the given absolute grid coordinates, if it belongs to this region.
     *
     * @param row    the absolute row index in the full grid
     * @param column the absolute column index in the full grid
     * @return the Cell at those coordinates, or null if not in this region
     */
    public Cell getCellAt(int row, int column) {
        for (Cell cell : cells) {
            if (cell.getRow() == row && cell.getColumn() == column) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Returns whether a given value (1-6) is already present in this region's solution values.
     *
     * @param value the value to check (1-6)
     * @return true if any cell in this region already has that solution value
     */
    public boolean containsValue(int value) {
        for (Cell cell : cells) {
            if (cell.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a given value appears in any cell of this region that shares the given column.
     * Used during board generation to check column constraints across regions.
     *
     * @param column the absolute column index to check
     * @param value  the value to look for
     * @return true if a cell in this region at that column already has that value
     */
    public boolean containsValueInColumn(int column, int value) {
        for (Cell cell : cells) {
            if (cell.getColumn() == column && cell.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a given value appears in any cell of this region that shares the given row.
     * Used during board generation to check row constraints across regions.
     *
     * @param row   the absolute row index to check
     * @param value the value to look for
     * @return true if a cell in this region at that row already has that value
     */
    public boolean containsValueInRow(int row, int value) {
        for (Cell cell : cells) {
            if (cell.getRow() == row && cell.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}