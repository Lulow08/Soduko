package com.bifasico.soduko.controller.cellInputHandler;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Adapter for {@link ICellInputListener} providing empty default implementations.
 * Concrete classes extend this and override only the events they need.
 */
public abstract class CellInputAdapter implements ICellInputListener {

    @Override
    public void onKeyPressed(KeyEvent event, int row, int col) {}

    @Override
    public void onCellClicked(MouseEvent event, int row, int col) {}

    @Override
    public void onDigitSelected(int digit) {}

    @Override
    public void onHintRequested() {}

    @Override
    public void onUndo() {}
}