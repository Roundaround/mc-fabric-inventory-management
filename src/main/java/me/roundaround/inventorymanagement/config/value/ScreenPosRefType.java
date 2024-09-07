package me.roundaround.inventorymanagement.config.value;

import me.roundaround.inventorymanagement.api.positioning.PositionReference;
import me.roundaround.inventorymanagement.api.positioning.ScreenPositionReference;

public enum ScreenPosRefType {
  LEFT, RIGHT, TOP, BOTTOM, CENTER_X, CENTER_Y;

  public PositionReference<?, ?> get() {
    return switch (this) {
      case LEFT -> ScreenPositionReference.left();
      case RIGHT -> ScreenPositionReference.right();
      case TOP -> ScreenPositionReference.top();
      case BOTTOM -> ScreenPositionReference.bottom();
      case CENTER_X -> ScreenPositionReference.centerX();
      case CENTER_Y -> ScreenPositionReference.centerY();
    };
  }
}
