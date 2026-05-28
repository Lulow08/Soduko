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
    public void onGuessPlaced(int row, int col, int value, boolean valid) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        GameView.CellState state = valid ? GameView.CellState.CORRECT : GameView.CellState.ERROR;
        gameView.refreshCell(row, col, cell, state);
        if (valid) {
            checkDigitCompletion(value);
        }
        selectedRow = row;
        selectedCol = col;
    }

    @Override
    public void onGuessCleared(int row, int col) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        gameView.refreshCell(row, col, cell, GameView.CellState.NORMAL);
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
                boolean valid = boardManager.placeGuess(selectedRow, selectedCol, digit);
                onGuessPlaced(selectedRow, selectedCol, digit, valid);
                if (boardManager.isGameWon()) {
                    onGameWon();
                }
            }
        }
    }

    @Override
    public void onHintApplied(int row, int col) {
        Cell cell = boardManager.getBoard().getCellAt(row, col);
        gameView.animateHint(row, col, cell);
        checkDigitCompletion(cell.getValue());
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

    /**
     * Builds a minimal synthetic KeyEvent carrying just the digit character,
     * used when the player selects a digit from the toolbar then clicks a cell.
     */
    private KeyEvent syntheticKeyEvent(int digit) {
        return new KeyEvent(
                KeyEvent.KEY_PRESSED, String.valueOf(digit), String.valueOf(digit),
                null, false, false, false, false
        );
    }
}