package me.roundaround.inventorymanagement.client.gui;

import java.util.List;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SortInventoryButton extends InventoryManagementButton {
  private final boolean isPlayerInventory;

  public SortInventoryButton(HandledScreen<?> parent, int x, int y, boolean isPlayerInventory) {
    // super(parent, x, y, 0, 0, (button) -> {
    // NetworkHandler.sendToServer(new SortInventoryMessage(isPlayerInventory));
    // });
    super(parent, x, y, 0, 0, (button) -> {
    });
    this.isPlayerInventory = isPlayerInventory;
  }

  @Override
  protected List<Text> getTooltip() {
    String key = isPlayerInventory
        ? "inventorymanagement.button.sort_player"
        : "inventorymanagement.button.sort_container";
    return List.of(new TranslatableText(key));
  }
}
