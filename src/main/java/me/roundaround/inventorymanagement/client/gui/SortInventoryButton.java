package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SortInventoryButton extends InventoryManagementButton {
  private static final ButtonTextures TEXTURES = new ButtonTextures(
      new Identifier(InventoryManagementMod.MOD_ID, "sort"),
      new Identifier(InventoryManagementMod.MOD_ID, "sort_highlighted")
  );

  public SortInventoryButton(
      HandledScreen<?> parent, Inventory inventory, Slot referenceSlot, Position offset, boolean isPlayerInventory
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
      HandledScreenAccessor parent, Inventory inventory, Slot referenceSlot, Position offset, boolean isPlayerInventory
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

  private static Text getTooltip(boolean isPlayerInventory) {
    String key = isPlayerInventory ?
        "inventorymanagement.button.sort_player" :
        "inventorymanagement.button.sort_container";
    return Text.translatable(key);
  }
}
