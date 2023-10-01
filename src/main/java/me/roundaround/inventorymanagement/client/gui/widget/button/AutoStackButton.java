package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.network.AutoStackPacket;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class AutoStackButton<H extends ScreenHandler, S extends HandledScreen<H>>
    extends ButtonBase<H, S> {
  public AutoStackButton(
      Position offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context) {
    super(positioningFunction.apply(context),
        offset,
        new Position(context.isPlayerInventory() ? 2 : 1, 0),
        positioningFunction,
        context,
        (button) -> AutoStackPacket.sendToServer(context.isPlayerInventory()),
        getTooltip(context.isPlayerInventory()));
  }

  private static Text getTooltip(boolean fromPlayerInventory) {
    String key = fromPlayerInventory
        ? "inventorymanagement.button.autostack_into"
        : "inventorymanagement.button.autostack_from";
    return Text.translatable(key);
  }
}
