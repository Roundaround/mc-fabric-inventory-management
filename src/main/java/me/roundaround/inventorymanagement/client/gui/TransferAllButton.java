package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TransferAllButton extends InventoryManagementButton {
  private static final ButtonTextures TEXTURES_FROM =
      new ButtonTextures(new Identifier(InventoryManagementMod.MOD_ID, "transfer_from"),
          new Identifier(InventoryManagementMod.MOD_ID, "transfer_from_highlighted"));
  private static final ButtonTextures TEXTURES_TO =
      new ButtonTextures(new Identifier(InventoryManagementMod.MOD_ID, "transfer_to"),
          new Identifier(InventoryManagementMod.MOD_ID, "transfer_to_highlighted"));

  public TransferAllButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(parent,
        inventory,
        referenceSlot,
        offset,
        fromPlayerInventory,
        (button) -> TransferAllPacket.sendToServer(fromPlayerInventory),
        getTooltip(fromPlayerInventory),
        fromPlayerInventory ? TEXTURES_TO : TEXTURES_FROM);
  }

  public TransferAllButton(
      HandledScreenAccessor parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory) {
    super(parent,
        inventory,
        referenceSlot,
        offset,
        fromPlayerInventory,
        (button) -> TransferAllPacket.sendToServer(fromPlayerInventory),
        getTooltip(fromPlayerInventory),
        fromPlayerInventory ? TEXTURES_TO : TEXTURES_FROM);
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return Text.translatable(key);
  }
}
