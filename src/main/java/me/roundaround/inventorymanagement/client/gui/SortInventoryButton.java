package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.client.gui.screen.ScreenPositioner;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.generated.Constants;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SortInventoryButton extends InventoryManagementButton {
  private static final ButtonTextures TEXTURES = new ButtonTextures(
      Identifier.of(Constants.MOD_ID, "sort"),
      Identifier.of(Constants.MOD_ID, "sort_highlighted")
  );

  public SortInventoryButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory
  ) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        isPlayerInventory,
        (button) -> ClientNetworking.sendSort(isPlayerInventory),
        getTooltip(isPlayerInventory),
        TEXTURES
    );
  }

  public SortInventoryButton(
      ScreenPositioner parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory
  ) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        isPlayerInventory,
        (button) -> ClientNetworking.sendSort(isPlayerInventory),
        getTooltip(isPlayerInventory),
        TEXTURES
    );
  }

  private static net.minecraft.text.Text getTooltip(boolean isPlayerInventory) {
    String key = isPlayerInventory ?
        "inventorymanagement.button.sort_player" :
        "inventorymanagement.button.sort_container";
    return net.minecraft.text.Text.translatable(key);
  }
}
