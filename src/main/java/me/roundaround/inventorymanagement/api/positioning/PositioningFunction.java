package me.roundaround.inventorymanagement.api.positioning;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.roundalib.client.gui.util.Coords;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface PositioningFunction<H extends ScreenHandler, S extends HandledScreen<H>> {
  Coords apply(ButtonContext<H, S> context);

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> fromPositionRefs(
      PositionReference<H, S> referenceX, PositionReference<H, S> referenceY
  ) {
    return fromPositionRefs(referenceX, referenceY, Coords.zero());
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> fromPositionRefs(
      PositionReference<H, S> referenceX, PositionReference<H, S> referenceY, Coords offset
  ) {
    return (context) -> new Coords(referenceX.get(context) + offset.x(), referenceY.get(context) + offset.y());
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> refSlotYAndBgRight() {
    return refSlotYAndBgRight(Coords.zero());
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> refSlotYAndBgRight(Coords offset) {
    return fromPositionRefs(BackgroundPositionReference.right(), SlotPositionReference.top(), offset);
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> bgTopRight(Coords offset) {
    return fromPositionRefs(BackgroundPositionReference.right(), BackgroundPositionReference.top(), offset);
  }
}
