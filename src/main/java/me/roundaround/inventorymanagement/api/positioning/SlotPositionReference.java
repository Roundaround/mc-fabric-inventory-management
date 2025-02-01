package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.function.Function;

public class SlotPositionReference<H extends ScreenHandler, S extends HandledScreen<H>> implements PositionReference<H, S> {
  public static final int SLOT_SIZE = 16;

  private final Function<ButtonContext<H, S>, Slot> slotProducer;
  private final Function<Slot, Integer> valueProducer;
  private final BackgroundPositionReference<H, S> bgBaseRef;

  private SlotPositionReference(
      Function<ButtonContext<H, S>, Slot> slotProducer,
      Function<Slot, Integer> valueProducer,
      BackgroundPositionReference<H, S> bgBaseRef
  ) {
    this.slotProducer = slotProducer;
    this.valueProducer = valueProducer;
    this.bgBaseRef = bgBaseRef;
  }

  @Override
  public int get(ButtonContext<H, S> context) {
    return this.valueProducer.apply(this.slotProducer.apply(context)) + this.bgBaseRef.get(context);
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> left(
      Function<ButtonContext<H, S>, Slot> slotProducer
  ) {
    return new SlotPositionReference<>(
        slotProducer, SlotPositionReference::getSlotLeft, BackgroundPositionReference.left());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> left() {
    return left(byAuto());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> left(int slotIndex) {
    return left(byIndex(slotIndex));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> left(Slot slot) {
    return left(byReference(slot));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> right(
      Function<ButtonContext<H, S>, Slot> slotProducer
  ) {
    return new SlotPositionReference<>(
        slotProducer, SlotPositionReference::getSlotRight, BackgroundPositionReference.left());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> right() {
    return right(byAuto());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> right(int slotIndex) {
    return right(byIndex(slotIndex));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> right(Slot slot) {
    return right(byReference(slot));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> top(
      Function<ButtonContext<H, S>, Slot> slotProducer
  ) {
    return new SlotPositionReference<>(
        slotProducer, SlotPositionReference::getSlotTop, BackgroundPositionReference.top());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> top() {
    return top(byAuto());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> top(int slotIndex) {
    return top(byIndex(slotIndex));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> top(Slot slot) {
    return top(byReference(slot));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> bottom(
      Function<ButtonContext<H, S>, Slot> slotProducer
  ) {
    return new SlotPositionReference<>(
        slotProducer, SlotPositionReference::getSlotBottom, BackgroundPositionReference.top());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> bottom() {
    return bottom(byAuto());
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> bottom(int slotIndex) {
    return bottom(byIndex(slotIndex));
  }

  public static <H extends ScreenHandler, S extends HandledScreen<H>> SlotPositionReference<H, S> bottom(Slot slot) {
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

  private static <H extends ScreenHandler, S extends HandledScreen<H>> Function<ButtonContext<H, S>, Slot> byAuto() {
    return (context) -> InventoryHelper.getReferenceSlot(context.getScreen(), context.isPlayerInventory());
  }

  private static <H extends ScreenHandler, S extends HandledScreen<H>> Function<ButtonContext<H, S>, Slot> byIndex(
      int slotIndex
  ) {
    return (context) -> {
      try {
        return context.getScreenHandler().getSlot(slotIndex);
      } catch (IndexOutOfBoundsException ignored) {
        return null;
      }
    };
  }

  private static <H extends ScreenHandler, S extends HandledScreen<H>> Function<ButtonContext<H, S>, Slot> byReference(
      Slot slot
  ) {
    return (context) -> slot;
  }
}
