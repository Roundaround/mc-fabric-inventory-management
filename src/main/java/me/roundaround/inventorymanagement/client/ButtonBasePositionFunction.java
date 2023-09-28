package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

@FunctionalInterface
public interface ButtonBasePositionFunction<T extends HandledScreen<?>> {
  Position apply(
      T parentScreen, HandledScreenAccessor accessor, Position offset, boolean isPlayerInventory);

  static <T extends HandledScreen<?>> ButtonBasePositionFunction<T> getDefault() {
    return (parentScreen, accessor, offset, isPlayerInventory) -> {
      Slot referenceSlot = getReferenceSlot(parentScreen, isPlayerInventory);
      int refY = referenceSlot == null ? 0 : referenceSlot.y;
      return new Position(accessor.getX() + accessor.getBackgroundWidth() + offset.x(),
          accessor.getY() + refY + offset.y());
    };
  }

  static <T extends HandledScreen<?>> ButtonBasePositionFunction<T> forReferenceSlot(Slot referenceSlot) {
    return (parentScreen, accessor, offset, fromPlayerInventory) -> {
      int refY = referenceSlot == null ? 0 : referenceSlot.y;
      return new Position(accessor.getX() + accessor.getBackgroundWidth() + offset.x(),
          accessor.getY() + refY + offset.y());
    };
  }

  static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .max(Comparator.comparingInt(slot -> slot.x - slot.y))
        .orElse(null);
  }
}
