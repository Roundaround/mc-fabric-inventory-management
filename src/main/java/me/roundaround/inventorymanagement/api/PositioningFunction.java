package me.roundaround.inventorymanagement.api;

import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

@FunctionalInterface
public interface PositioningFunction<H extends ScreenHandler, S extends HandledScreen<H>> {
  Position apply(ButtonContext<H, S> context);

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> getDefault() {
    return (context) -> {
      Slot referenceSlot = context.getReferenceSlot();
      int refY = referenceSlot == null ? 0 : referenceSlot.y;
      return new Position(context.getAccessor().getX() + context.getAccessor().getBackgroundWidth(),
          context.getAccessor().getY() + refY);
    };
  }

  static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .max(Comparator.comparingInt(slot -> slot.x - slot.y))
        .orElse(null);
  }
}
