package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TransferAllButton extends InventoryManagementButton {
  private final boolean fromPlayerInventory;

  public TransferAllButton(HandledScreen<?> parent, int x, int y, boolean fromPlayerInventory) {
    // super(parent, x, y, fromPlayerInventory ? 4 : 3, 0, (button) -> {
    // NetworkHandler.sendToServer(new TransferAllMessage(fromPlayerInventory));
    // });
    super(parent, (HandledScreenAccessor) parent, x, y, fromPlayerInventory ? 4 : 3, 0, (button) -> {
      InventoryManagementMod.LOGGER.info(((TransferAllButton) button).getTooltip().getString());
    });
    this.fromPlayerInventory = fromPlayerInventory;
  }

  @Override
  protected Text getTooltip() {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return new TranslatableText(key);
  }
}
