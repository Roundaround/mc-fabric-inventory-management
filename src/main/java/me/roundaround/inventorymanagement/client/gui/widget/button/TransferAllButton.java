package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.network.TransferAllPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class TransferAllButton<H extends ScreenHandler, S extends HandledScreen<H>>
    extends ButtonBase<H, S> {
  public TransferAllButton(
      Position offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context) {
    super(positioningFunction.apply(context),
        offset,
        new Position(context.isPlayerInventory() ? 4 : 3, 0),
        positioningFunction,
        context,
        (button) -> TransferAllPacket.sendToServer(context.isPlayerInventory()),
        getTooltip(context.isPlayerInventory()));
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.transfer_place"
        : "inventorymanagement.button.transfer_take";
    return Text.translatable(key);
  }
}
