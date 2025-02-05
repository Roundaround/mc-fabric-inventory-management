package me.roundaround.inventorymanagement.client.gui.widget.button;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.gui.ButtonContext;
import me.roundaround.inventorymanagement.api.gui.positioning.Coords;
import me.roundaround.inventorymanagement.api.gui.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.client.option.KeyBindings;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SortInventoryButton<H extends ScreenHandler, S extends HandledScreen<H>> extends ButtonBase<H, S> {
  private static final Identifier ICON = new Identifier(InventoryManagementMod.MOD_ID, "icon/sort");

  public SortInventoryButton(
      Coords offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context
  ) {
    super(positioningFunction.apply(context), offset, positioningFunction, context,
        getAction(context.isPlayerInventory()), getTooltip(context.isPlayerInventory()), ICON
    );
  }

  private static Text getTooltip(boolean isPlayerInventory) {
    String baseKey = isPlayerInventory ?
        "inventorymanagement.button.sortPlayer" :
        "inventorymanagement.button.sortContainer";
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
    return (button) -> {
      MinecraftClient client = MinecraftClient.getInstance();
      PlayerEntity player = client.player;
      ArrayList<Integer> sorted = InventoryHelper.tempSortInventory(player, isPlayerInventory);
      ClientNetworking.sendSortInventoryPacket(isPlayerInventory, sorted);
    };
  }
}
