package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TransferAllButton<H extends ScreenHandler, S extends HandledScreen<H>>
    extends ButtonBase<H, S> {
  private static final ButtonTextures TEXTURES_FROM =
      new ButtonTextures(new Identifier(InventoryManagementMod.MOD_ID, "transfer_from"),
          new Identifier(InventoryManagementMod.MOD_ID, "transfer_from_highlighted"));
  private static final ButtonTextures TEXTURES_TO =
      new ButtonTextures(new Identifier(InventoryManagementMod.MOD_ID, "transfer_to"),
          new Identifier(InventoryManagementMod.MOD_ID, "transfer_to_highlighted"));

  public TransferAllButton(
      Position offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context) {
    super(positioningFunction.apply(context),
        offset,
        positioningFunction,
        context,
        (button) -> TransferAllPacket.sendToServer(context.isPlayerInventory()),
        getTooltip(context.isPlayerInventory()),
        context.isPlayerInventory() ? TEXTURES_TO : TEXTURES_FROM);
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return Text.translatable(key);
  }
}
