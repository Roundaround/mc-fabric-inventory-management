package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class AutoStackButton extends InventoryManagementButton {
  private final boolean fromPlayerInventory;

  public AutoStackButton(HandledScreen<?> parent, int x, int y, boolean fromPlayerInventory) {
    // super(parent, x, y, fromPlayerInventory ? 2 : 1, 0, (button) -> {
    // NetworkHandler.sendToServer(new AutoStackMessage(fromPlayerInventory));
    // });
    super(parent, (HandledScreenAccessor) parent, x, y, fromPlayerInventory ? 2 : 1, 0, (button) -> {
      InventoryManagementMod.LOGGER.info(((AutoStackButton) button).getTooltip().getString());
    });
    this.fromPlayerInventory = fromPlayerInventory;
  }

  @Override
  protected Text getTooltip() {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.autostack_into"
        : "inventorymanagement.button.autostack_from";
    return new TranslatableText(key);
  }
}
