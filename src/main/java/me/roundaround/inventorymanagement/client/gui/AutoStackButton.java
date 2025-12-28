package me.roundaround.inventorymanagement.client.gui;

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

public class AutoStackButton extends InventoryManagementButton {
  private static final ButtonTextures TEXTURES_FROM = new ButtonTextures(
      Identifier.of(Constants.MOD_ID, "stack_from"),
      Identifier.of(Constants.MOD_ID, "stack_from_highlighted")
  );
  private static final ButtonTextures TEXTURES_TO = new ButtonTextures(
      Identifier.of(Constants.MOD_ID, "stack_to"),
      Identifier.of(Constants.MOD_ID, "stack_to_highlighted")
  );

  public AutoStackButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory
  ) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        fromPlayerInventory,
        (button) -> ClientNetworking.sendStack(fromPlayerInventory),
        getTooltip(fromPlayerInventory),
        fromPlayerInventory ? TEXTURES_TO : TEXTURES_FROM
    );
  }

  public AutoStackButton(
      HandledScreenAccessor parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean fromPlayerInventory
  ) {
    super(
        parent,
        inventory,
        referenceSlot,
        offset,
        fromPlayerInventory,
        (button) -> ClientNetworking.sendStack(fromPlayerInventory),
        getTooltip(fromPlayerInventory),
        fromPlayerInventory ? TEXTURES_TO : TEXTURES_FROM
    );
  }

  private static net.minecraft.text.Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory ?
        "inventorymanagement.button.autostack_into" :
        "inventorymanagement.button.autostack_from";
    return net.minecraft.text.Text.translatable(key);
  }
}
