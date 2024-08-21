package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.roundalib.client.gui.util.Coords;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

@FunctionalInterface
public interface PositioningFunction<H extends ScreenHandler, S extends HandledScreen<H>> {
  Position apply(ButtonContext<H, S> context);

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> refSlotYAndBgRight() {
    return refSlotYAndBgRight(Coords.zero());
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> refSlotYAndBgRight(Coords offset) {
    return (context) -> {
      Slot referenceSlot = context.getReferenceSlot();
      int refY = referenceSlot == null ? 0 : referenceSlot.y;
      return new Position(
          context.getAccessor().getX() + context.getAccessor().getBackgroundWidth() + offset.x(),
          context.getAccessor().getY() + refY + offset.y()
      );
    };
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> bgTopRight(Coords offset) {
    return (context) -> {
      int refX = context.getAccessor().getX() + context.getAccessor().getBackgroundWidth();
      int refY = context.getAccessor().getY();
      return new Position(refX + offset.x(), refY + offset.y());
    };
  }

  static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter((slot) -> {
          if (isPlayerInventory != (slot.inventory instanceof PlayerInventory)) {
            return false;
          }

          // Only consider "bulk inventory" slots if player inventory
          return !isPlayerInventory || SlotRange.playerMainRange().contains(slot.getIndex());
        })
        .max(Comparator.comparingInt(slot -> slot.x - slot.y))
        .orElse(null);
  }
}
