package me.roundaround.inventorymanagement.client.option;

import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.roundalib.client.event.ScreenInputEvent;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class KeyBindings {
  private KeyBindings() {
  }

  public static KeyBinding CONFIGURE;
  public static KeyBinding SORT_CONTAINER;
  public static KeyBinding SORT_PLAYER;
  public static KeyBinding SORT_ALL;
  public static KeyBinding STACK_FROM_CONTAINER;
  public static KeyBinding STACK_INTO_CONTAINER;
  public static KeyBinding TRANSFER_FROM_CONTAINER;
  public static KeyBinding TRANSFER_INTO_CONTAINER;

  private static boolean initialized = false;

  public static void init() {
    if (initialized) {
      return;
    }

    CONFIGURE = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.configure", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(),
            "inventorymanagement.keybind.category"
        ));

    SORT_CONTAINER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.sortContainer", InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), "inventorymanagement.keybind.category"
        ));

    SORT_PLAYER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.sortPlayer", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(),
            "inventorymanagement.keybind.category"
        ));

    SORT_ALL = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.sortAll", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(),
            "inventorymanagement.keybind.category"
        ));

    STACK_FROM_CONTAINER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.stackFromContainer", InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), "inventorymanagement.keybind.category"
        ));

    STACK_INTO_CONTAINER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.stackIntoContainer", InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), "inventorymanagement.keybind.category"
        ));

    TRANSFER_FROM_CONTAINER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.transferFromContainer", InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), "inventorymanagement.keybind.category"
        ));

    TRANSFER_INTO_CONTAINER = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("inventorymanagement.keybind.transferIntoContainer", InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), "inventorymanagement.keybind.category"
        ));

    ScreenInputEvent.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
      if (!(screen instanceof HandledScreen)) {
        return false;
      }

      if (CONFIGURE.matchesKey(keyCode, scanCode)) {
        //        GuiUtil.setScreen(new PerScreenConfigScreen(screen, InventoryManagementConfig.getInstance()
        //        .perScreenConfigs));
        return true;
      }

      if (InventoryButtonsManager.INSTANCE.hasContainerSideSort() && SORT_CONTAINER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendSortContainerPacket();
        return true;
      }

      if (InventoryButtonsManager.INSTANCE.hasPlayerSideSort() && SORT_PLAYER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendSortInventoryPacket();
        return true;
      }

      if (SORT_ALL.matchesKey(keyCode, scanCode)) {
        if (InventoryButtonsManager.INSTANCE.hasContainerSideSort() &&
            InventoryButtonsManager.INSTANCE.hasPlayerSideSort()) {
          GuiUtil.playClickSound();
          ClientNetworking.sendSortAllPacket();
          return true;
        } else if (InventoryButtonsManager.INSTANCE.hasContainerSideSort()) {
          GuiUtil.playClickSound();
          ClientNetworking.sendSortContainerPacket();
          return true;
        } else if (InventoryButtonsManager.INSTANCE.hasPlayerSideSort()) {
          GuiUtil.playClickSound();
          ClientNetworking.sendSortInventoryPacket();
          return true;
        }
      }

      if (InventoryButtonsManager.INSTANCE.hasContainerSideStack() &&
          STACK_FROM_CONTAINER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendStackFromContainerPacket();
        return true;
      }

      if (InventoryButtonsManager.INSTANCE.hasPlayerSideStack() && STACK_INTO_CONTAINER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendStackIntoContainerPacket();
        return true;
      }

      if (InventoryButtonsManager.INSTANCE.hasContainerSideTransfer() &&
          TRANSFER_FROM_CONTAINER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendTransferFromContainerPacket();
        return true;
      }

      if (InventoryButtonsManager.INSTANCE.hasPlayerSideTransfer() &&
          TRANSFER_INTO_CONTAINER.matchesKey(keyCode, scanCode)) {
        GuiUtil.playClickSound();
        ClientNetworking.sendTransferIntoContainerPacket();
        return true;
      }

      return false;
    });

    initialized = true;
  }
}
