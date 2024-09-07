package me.roundaround.inventorymanagement.config.value;

import me.roundaround.inventorymanagement.api.positioning.BackgroundPositionReference;
import me.roundaround.inventorymanagement.api.positioning.PositionReference;

public enum BackgroundPosRefType {
  LEFT, RIGHT, TOP, BOTTOM, CENTER_X, CENTER_Y;

  public PositionReference<?, ?> get() {
    return switch (this) {
      case LEFT -> BackgroundPositionReference.left();
      case RIGHT -> BackgroundPositionReference.right();
      case TOP -> BackgroundPositionReference.top();
      case BOTTOM -> BackgroundPositionReference.bottom();
      case CENTER_X -> BackgroundPositionReference.centerX();
      case CENTER_Y -> BackgroundPositionReference.centerY();
    };
  }
}
