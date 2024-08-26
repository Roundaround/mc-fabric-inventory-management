package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.client.option.KeyBindings;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SortInventoryButton<H extends ScreenHandler, S extends HandledScreen<H>> extends ButtonBase<H, S> {
  private static final Identifier ICON = new Identifier(InventoryManagementMod.MOD_ID, "icon/sort");

  public SortInventoryButton(
      Position offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context
  ) {
    super(positioningFunction.apply(context), offset, positioningFunction, context,
        getAction(context.isPlayerInventory()), getTooltip(context.isPlayerInventory()), ICON
    );
  }

  private static Text getTooltip(boolean isPlayerInventory) {
    String baseKey = isPlayerInventory ?
        "inventorymanagement.button.sort_player" :
        "inventorymanagement.button.sort_container";
    KeyBinding keyBinding = isPlayerInventory ? KeyBindings.SORT_PLAYER : KeyBindings.SORT_CONTAINER;
    if (keyBinding.isUnbound()) {
      keyBinding = KeyBindings.SORT_ALL;
    }

    return Text.translatable(baseKey)
        .append(ScreenTexts.LINE_BREAK)
        .append(Text.translatable("inventorymanagement.keybind.display", keyBinding.getBoundKeyLocalizedText())
            .formatted(Formatting.ITALIC));
  }

  private static PressAction getAction(boolean isPlayerInventory) {
    return isPlayerInventory ?
        (button) -> ClientNetworking.sendSortInventoryPacket() :
        (button) -> ClientNetworking.sendSortContainerPacket();
  }
}
