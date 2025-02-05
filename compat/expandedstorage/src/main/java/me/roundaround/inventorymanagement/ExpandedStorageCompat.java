package me.roundaround.inventorymanagement;

import compasses.expandedstorage.impl.client.gui.FakePickScreen;
import compasses.expandedstorage.impl.client.gui.ScrollScreen;
import compasses.expandedstorage.impl.inventory.handler.AbstractHandler;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import net.minecraft.client.gui.screen.Screen;

public class ExpandedStorageCompat implements InventoryManagementEntrypointHandler {
  @Override
  public void onInventoryManagementInit() {
    ButtonRegistry registry = ButtonRegistry.getInstance();

    registry.registerBothSides(AbstractHandler.class, (context) -> {
      if (((Screen) context.getScreen()) instanceof FakePickScreen) {
        return null;
      }

      if (context.isPlayerInventory()) {
        return PositioningFunction.fromPositionRefs(SlotPositionReference.right(), SlotPositionReference.top(),
            new Coords(PositioningFunction.SLOT_TO_BG_DIST + PositioningFunction.DEFAULT_BG_RIGHT_OFFSET,
                PositioningFunction.DEFAULT_SLOT_TOP_OFFSET
            )
        ).apply(context);
      }

      int scrollbarOffset = ((Screen) context.getScreen()) instanceof ScrollScreen ? 18 : 0;
      return PositioningFunction.fromPositionRefs(BackgroundPositionReference.right(), TitlePositionReference.bottom(),
          new Coords(scrollbarOffset + PositioningFunction.DEFAULT_BG_RIGHT_OFFSET,
              PositioningFunction.TITLE_TO_SLOT_DIST + PositioningFunction.DEFAULT_SLOT_TOP_OFFSET
          )
      ).apply(context);
    });
  }
}
