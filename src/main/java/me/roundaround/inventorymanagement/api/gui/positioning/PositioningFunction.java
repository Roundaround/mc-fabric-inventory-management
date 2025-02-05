package me.roundaround.inventorymanagement.api.gui.positioning;

import me.roundaround.inventorymanagement.api.gui.ButtonContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface PositioningFunction<H extends ScreenHandler, S extends HandledScreen<H>> {
  Coords apply(ButtonContext<H, S> context);

  int DEFAULT_BG_RIGHT_OFFSET = -4;
  int DEFAULT_SLOT_TOP_OFFSET = -1;
  int SLOT_TO_BG_DIST = 8;
  int TITLE_TO_SLOT_DIST = 3;

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
    return refSlotYAndBgRight(Coords.of(DEFAULT_BG_RIGHT_OFFSET, DEFAULT_SLOT_TOP_OFFSET));
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> refSlotYAndBgRight(Coords offset) {
    return fromPositionRefs(BackgroundPositionReference.right(), SlotPositionReference.top(), offset);
  }

  static <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> bgTopRight(Coords offset) {
    return fromPositionRefs(BackgroundPositionReference.right(), BackgroundPositionReference.top(), offset);
  }
}
