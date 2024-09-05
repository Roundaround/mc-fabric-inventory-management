package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;

public interface PositionReference<S extends HandledScreen<?>> {
  int getCurrent(S screen, HandledScreenAccessor accessor);

  int SLOT_SIZE = 16;

  static <S extends HandledScreen<?>> PositionReference<S> slotLeft(boolean isPlayerInventory) {
    return (screen, accessor) -> getSlotLeft(getReferenceSlot(screen, isPlayerInventory));
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(boolean isPlayerInventory) {
    return slotRight(isPlayerInventory, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(boolean isPlayerInventory, int width) {
    return (screen, accessor) -> getSlotRight(getReferenceSlot(screen, isPlayerInventory), width);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotTop(boolean isPlayerInventory) {
    return (screen, accessor) -> getSlotTop(getReferenceSlot(screen, isPlayerInventory));
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(boolean isPlayerInventory) {
    return slotBottom(isPlayerInventory, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(boolean isPlayerInventory, int height) {
    return (screen, accessor) -> getSlotBottom(getReferenceSlot(screen, isPlayerInventory), height);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotLeft(int slotIndex) {
    return (screen, accessor) -> {
      Slot slot = null;
      try {
        slot = screen.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
      }
      return getSlotLeft(slot);
    };
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(int slotIndex) {
    return slotRight(slotIndex, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(int slotIndex, int width) {
    return (screen, accessor) -> {
      Slot slot = null;
      try {
        slot = screen.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
      }
      return getSlotRight(slot, width);
    };
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotTop(int slotIndex) {
    return (screen, accessor) -> {
      Slot slot = null;
      try {
        slot = screen.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
      }
      return getSlotTop(slot);
    };
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(int slotIndex) {
    return slotBottom(slotIndex, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(int slotIndex, int height) {
    return (screen, accessor) -> {
      Slot slot = null;
      try {
        slot = screen.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
      }
      return getSlotBottom(slot, height);
    };
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotLeft(Slot slot) {
    return (screen, accessor) -> getSlotLeft(slot);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(Slot slot) {
    return slotRight(slot, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotRight(Slot slot, int width) {
    return (screen, accessor) -> getSlotRight(slot, width);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotTop(Slot slot) {
    return (screen, accessor) -> getSlotTop(slot);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(Slot slot) {
    return slotBottom(slot, SLOT_SIZE);
  }

  static <S extends HandledScreen<?>> PositionReference<S> slotBottom(Slot slot, int height) {
    return (screen, accessor) -> getSlotBottom(slot, height);
  }

  static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream().filter((slot) -> {
      if (isPlayerInventory != (slot.inventory instanceof PlayerInventory)) {
        return false;
      }

      // Only consider "bulk inventory" slots if player inventory
      return !isPlayerInventory || SlotRange.playerMainRange().contains(slot.getIndex());
    }).max(Comparator.comparingInt(slot -> slot.x - slot.y)).orElse(null);
  }

  private static int getSlotLeft(Slot slot) {
    return slot == null ? 0 : slot.x;
  }

  private static int getSlotRight(Slot slot, int width) {
    return getSlotLeft(slot) + width;
  }

  private static int getSlotTop(Slot slot) {
    return slot == null ? 0 : slot.y;
  }

  private static int getSlotBottom(Slot slot, int height) {
    return getSlotTop(slot) + height;
  }
}
