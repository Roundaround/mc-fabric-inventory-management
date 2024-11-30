package me.roundaround.inventorymanagement;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.api.positioning.BackgroundPositionReference;
import me.roundaround.inventorymanagement.api.positioning.Coords;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.api.positioning.TitlePositionReference;

public class ReinforcedStorageCompat implements InventoryManagementEntrypointHandler {
  @Override
  public void onInventoryManagementInit() {
    ButtonRegistry registry = ButtonRegistry.getInstance();

    registry.registerBothSides(ReinforcedStorageScreenHandler.class,
        (context) -> PositioningFunction.fromPositionRefs(BackgroundPositionReference.right(),
            TitlePositionReference.autoBottom(), new Coords(PositioningFunction.DEFAULT_BG_RIGHT_OFFSET,
                PositioningFunction.TITLE_TO_SLOT_DIST + PositioningFunction.DEFAULT_SLOT_TOP_OFFSET
            )
        ).apply(context)
    );
  }
}
