package com.bifasico.soduko.controller;

import com.bifasico.soduko.controller.cellInputHandler.CellInputHandler;
import com.bifasico.soduko.model.Board;
import com.bifasico.soduko.model.BoardManager;
import com.bifasico.soduko.model.Cell;

import com.bifasico.soduko.view.GameView;
import com.bifasico.soduko.view.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for game-view.fxml.
 * Routes FXML events to {@link CellInputHandler} and reacts to
 * {@link CellInputHandler.CellInputCallback} callbacks by calling {@link GameView}.
 * No view-building and no game logic live here.
 */
public class GameController implements Initializable, CellInputHandler.CellInputCallback {

    @FXML private GridPane boardGrid;
    @FXML private HBox toolbar;
    @FXML private StackPane winOverlay;
    @FXML private VBox menuContent;
    @FXML private VBox winContent;
    @FXML private Button digitButton1;
    @FXML private Button digitButton2;
    @FXML private Button digitButton3;
    @FXML private Button digitButton4;
    @FXML private Button digitButton5;
    @FXML private Button digitButton6;

    private BoardManager boardManager;
    private CellInputHandler inputHandler;
    private GameView gameView;

    private int selectedRow = -1;
    private int selectedCol = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Button[] digitButtons = {
                digitButton1, digitButton2, digitButton3,
                digitButton4, digitButton5, digitButton6
        };

        boardManager = new BoardManager();
        gameView = new GameView(boardGrid, toolbar, digitButtons);
        inputHandler = new CellInputHandler(boardManager, this);

        gameView.buildGrid(this::handleCellClick, this::handleCellKey);
    }

    /* ── Menu ── */

    @FXML
    private void onPlay() {
        winOverlay.setVisible(false);
        winOverlay.setManaged(false);
        toolbar.setVisible(true);
        toolbar.setManaged(true);
        gameView.renderBoard(boardManager.getBoard());
    }

    /* ── Cell event forwarders ── */

    private void handleCellClick(MouseEvent event, int row, int col) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        if (cell.isGiven()) {
            return;
        }
        int pendingDigit = gameView.getSelectedDigit();
        if (pendingDigit != -1) {
            inputHandler.onKeyPressed(syntheticKeyEvent(pendingDigit), row, col);
        } else {
            inputHandler.onCellClicked(event, row, col);
        }
    }

    private void handleCellKey(KeyEvent event, int row, int col) {
        inputHandler.onKeyPressed(event, row, col);
    }

    /* ── Toolbar FXML handlers ── */

    @FXML
    private void onDigitPressed(javafx.event.ActionEvent event) {
        Button source = (Button) event.getSource();
        int digit = Integer.parseInt((String) source.getUserData());
        inputHandler.onDigitSelected(digit);
    }

    @FXML
    private void onUndo() {
        inputHandler.onUndo();
    }

    @FXML
    private void onHintRequested() {
        inputHandler.onHintRequested();
    }

    @FXML
    private void onGoToMenu() throws IOException {
        SceneManager.getInstance().switchScene("game-view.fxml");
    }

    /* ── CellInputCallback ── */

    @Override
    public void onGuessPlaced(int row, int col, int value, boolean valid,
                              java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);

        // Determine the visual state of the placed cell:
        // - Collision with existing cells → ERROR (red)
        // - No collision but wrong answer → ERROR (red)
        // - Correct answer → CORRECT
        GameView.CellState state;
        if (!valid || value != cell.getValue()) {
            state = GameView.CellState.ERROR;
        } else {
            state = GameView.CellState.CORRECT;
        }
        gameView.refreshCell(row, col, cell, state);

        // Also mark every pre-existing cell that now collides with this guess.
        for (com.bifasico.soduko.model.Cell conflict : collisions) {
            gameView.refreshCell(conflict.getRow(), conflict.getColumn(), conflict, GameView.CellState.ERROR);
        }

        if (state == GameView.CellState.CORRECT) {
            checkDigitCompletion(value);
        }
        selectedRow = row;
        selectedCol = col;
    }

    @Override
    public void onGuessCleared(int row, int col) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        gameView.refreshCell(row, col, cell, GameView.CellState.NORMAL);

        // After clearing, neighbours that were in ERROR solely because of this
        // cell may no longer be in conflict — re-evaluate them.
        reEvaluateNeighbours(row, col);
    }

    @Override
    public void onCellSelected(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        gameView.updateSelection(row, col, boardManager.getBoard());
    }

    @Override
    public void onDigitSelected(int digit) {
        gameView.selectDigitButton(digit);
        if (selectedRow != -1 && selectedCol != -1) {
            Cell cell = boardManager.getBoard().getCellAt(selectedRow, selectedCol);
            if (!cell.isGiven()) {
                java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions =
                        boardManager.getBoard().getConflictingCells(selectedRow, selectedCol, digit);
                boolean valid = boardManager.placeGuess(selectedRow, selectedCol, digit);
                onGuessPlaced(selectedRow, selectedCol, digit, valid, collisions);
                if (boardManager.isGameWon()) {
                    onGameWon();
                }
            }
        }
    }

    @Override
    public void onHintApplied(int row, int col,
                              java.util.ArrayList<com.bifasico.soduko.model.Cell> collisions) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        // The hint cell is always shown with the hint animation — never as error.
        gameView.animateHint(row, col, cell);
        checkDigitCompletion(cell.getValue());

        // Any cell that was already wrong and now collides with the hint gets marked red.
        for (com.bifasico.soduko.model.Cell conflict : collisions) {
            gameView.refreshCell(conflict.getRow(), conflict.getColumn(), conflict, GameView.CellState.ERROR);
        }
    }

    @Override
    public void onGameWon() {
        menuContent.setVisible(false);
        menuContent.setManaged(false);
        winContent.setVisible(true);
        winContent.setManaged(true);
        winOverlay.setVisible(true);
        winOverlay.setManaged(true);
    }

    /* ── Private helpers ── */

    private void checkDigitCompletion(int digit) {
        if (boardManager.countCorrectlyPlaced(digit) == Board.GRID_SIZE) {
            gameView.markDigitCompleted(digit);
        }
    }

    private void reEvaluateNeighbours(int clearedRow, int clearedCol) {
        Board board = boardManager.getBoard();
        for (com.bifasico.soduko.model.Region region : board.getRegions()) {
            for (Cell neighbour : region.getCells()) {
                if (neighbour.isGiven()) continue;
                if (neighbour.getGuess() == 0) continue;
                int nr = neighbour.getRow();
                int nc = neighbour.getColumn();
                boolean sameRow    = nr == clearedRow;
                boolean sameCol    = nc == clearedCol;
                boolean sameRegion = board.getRegionFor(nr, nc) == board.getRegionFor(clearedRow, clearedCol);
                if (!sameRow && !sameCol && !sameRegion) continue;

                // Re-check whether this neighbour still has any conflict.
                boolean stillConflicts = !board.isGuessValid(nr, nc, neighbour.getGuess());
                boolean isWrong = neighbour.getGuess() != neighbour.getValue();
                GameView.CellState newState;
                if (stillConflicts || isWrong) {
                    newState = GameView.CellState.ERROR;
                } else {
                    newState = neighbour.isMarked() ? GameView.CellState.CORRECT : GameView.CellState.NORMAL;
                }
                gameView.refreshCell(nr, nc, neighbour, newState);
            }
        }
    }

    /** carrying just the digit character,
     * used when the player selects a digit from the toolbar then clicks a cell.
     */
    private KeyEvent syntheticKeyEvent(int digit) {
        return new KeyEvent(
                KeyEvent.KEY_PRESSED, String.valueOf(digit), String.valueOf(digit),
                null, false, false, false, false
        );
    }
}