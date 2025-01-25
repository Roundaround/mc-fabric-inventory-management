package me.roundaround.inventorymanagement.config.value;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.positioning.PositionReference;
import me.roundaround.inventorymanagement.api.positioning.ScreenPositionReference;
import me.roundaround.inventorymanagement.roundalib.config.value.EnumValue;

import java.util.Arrays;

public enum ScreenPosRefType implements EnumValue<ScreenPosRefType> {
  LEFT("left"), RIGHT("right"), TOP("top"), BOTTOM("bottom"), CENTER_X("centerX"), CENTER_Y("centerY");

  private final String id;

  ScreenPosRefType(String id) {
    this.id = id;
  }

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

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return String.format("%s.posref.screen.%s", InventoryManagementMod.MOD_ID, this.id);
  }

  @Override
  public ScreenPosRefType getFromId(String id) {
    return fromId(id);
  }

  @Override
  public ScreenPosRefType getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public ScreenPosRefType getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static ScreenPosRefType fromId(String id) {
    return Arrays.stream(values()).filter((type) -> type.id.equals(id)).findFirst().orElse(RIGHT);
  }
}
