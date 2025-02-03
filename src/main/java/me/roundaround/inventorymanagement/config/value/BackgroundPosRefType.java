package me.roundaround.inventorymanagement.config.value;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.positioning.BackgroundPositionReference;
import me.roundaround.inventorymanagement.api.positioning.PositionReference;
import me.roundaround.inventorymanagement.roundalib.config.value.EnumValue;

import java.util.Arrays;

public enum BackgroundPosRefType implements EnumValue<BackgroundPosRefType> {
  LEFT("left"), RIGHT("right"), TOP("top"), BOTTOM("bottom"), CENTER_X("centerX"), CENTER_Y("centerY");

  private final String id;

  BackgroundPosRefType(String id) {
    this.id = id;
  }

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

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return String.format("%s.posref.background.%s", InventoryManagementMod.MOD_ID, this.id);
  }

  @Override
  public BackgroundPosRefType getFromId(String id) {
    return fromId(id);
  }

  @Override
  public BackgroundPosRefType getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public BackgroundPosRefType getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static BackgroundPosRefType fromId(String id) {
    return Arrays.stream(values()).filter((type) -> type.id.equals(id)).findFirst().orElse(RIGHT);
  }
}
