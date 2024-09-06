package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.inventory.SlotRange;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SlotPositionReference<S extends HandledScreen<?>> implements PositionReference<S> {
  public static final int SLOT_SIZE = 16;

  private final BiFunction<S, HandledScreenAccessor, Slot> slotFunction;
  private final Function<Slot, Integer> producer;

  private SlotPositionReference(
      BiFunction<S, HandledScreenAccessor, Slot> slotFunction, Function<Slot, Integer> producer
  ) {
    this.slotFunction = slotFunction;
    this.producer = producer;
  }

  @Override
  public int get(S screen, HandledScreenAccessor accessor) {
    return this.producer.apply(this.slotFunction.apply(screen, accessor));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> left(
      BiFunction<S, HandledScreenAccessor, Slot> slotFunction
  ) {
    return new SlotPositionReference<>(slotFunction, SlotPositionReference::getSlotLeft);
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> left(boolean isPlayerInventory) {
    return left(byAuto(isPlayerInventory));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> left(int slotIndex) {
    return left(byIndex(slotIndex));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> left(Slot slot) {
    return left(byReference(slot));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> right(
      BiFunction<S, HandledScreenAccessor, Slot> slotFunction
  ) {
    return new SlotPositionReference<>(slotFunction, SlotPositionReference::getSlotRight);
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> right(boolean isPlayerInventory) {
    return right(byAuto(isPlayerInventory));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> right(int slotIndex) {
    return right(byIndex(slotIndex));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> right(Slot slot) {
    return right(byReference(slot));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> top(BiFunction<S, HandledScreenAccessor, Slot> slotFunction) {
    return new SlotPositionReference<>(slotFunction, SlotPositionReference::getSlotTop);
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> top(boolean isPlayerInventory) {
    return top(byAuto(isPlayerInventory));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> top(int slotIndex) {
    return top(byIndex(slotIndex));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> top(Slot slot) {
    return top(byReference(slot));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> bottom(
      BiFunction<S, HandledScreenAccessor, Slot> slotFunction
  ) {
    return new SlotPositionReference<>(slotFunction, SlotPositionReference::getSlotBottom);
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> bottom(boolean isPlayerInventory) {
    return bottom(byAuto(isPlayerInventory));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> bottom(int slotIndex) {
    return bottom(byIndex(slotIndex));
  }

  public static <S extends HandledScreen<?>> SlotPositionReference<S> bottom(Slot slot) {
    return bottom(byReference(slot));
  }

  public static int getSlotLeft(Slot slot) {
    return slot == null ? 0 : slot.x;
  }

  private static int getSlotRight(Slot slot) {
    return getSlotLeft(slot) + SLOT_SIZE;
  }

  private static int getSlotTop(Slot slot) {
    return slot == null ? 0 : slot.y;
  }

  private static int getSlotBottom(Slot slot) {
    return getSlotTop(slot) + SLOT_SIZE;
  }

  private static <S extends HandledScreen<?>> BiFunction<S, HandledScreenAccessor, Slot> byAuto(boolean isPlayerInventory) {
    return (screen, accessor) -> screen.getScreenHandler().slots.stream().filter((slot) -> {
      if (isPlayerInventory != (slot.inventory instanceof PlayerInventory)) {
        return false;
      }

      // Only consider "bulk inventory" slots if player inventory
      return !isPlayerInventory || SlotRange.playerMainRange().contains(slot.getIndex());
    }).max(Comparator.comparingInt(slot -> slot.x - slot.y)).orElse(null);
  }

  private static <S extends HandledScreen<?>> BiFunction<S, HandledScreenAccessor, Slot> byIndex(int slotIndex) {
    return (screen, accessor) -> {
      try {
        return screen.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
        return null;
      }
    };
  }

  private static <S extends HandledScreen<?>> BiFunction<S, HandledScreenAccessor, Slot> byReference(Slot slot) {
    return (screen, accessor) -> slot;
  }
}
