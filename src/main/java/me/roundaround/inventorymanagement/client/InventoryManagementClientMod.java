package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    InventoryButtonsManager.INSTANCE.init();
    ConfigControlRegister.init();
    initKeyBindings();
  }

  private void initKeyBindings() {
    KeyBinding keybindingPlayer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "inventorymanagement.keybind.position_edit.player",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        "inventorymanagement.keybind.category"));

    KeyBinding keybindingContainer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "inventorymanagement.keybind.position_edit.container",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_L,
        "inventorymanagement.keybind.category"));

    HandleScreenInputCallback.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
      if (Screens.getButtons(screen)
          .stream()
          .noneMatch((button) -> button instanceof InventoryManagementButton)) {
        return false;
      }

      if (keybindingPlayer.matchesKey(keyCode, scanCode)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, true));
        return true;
      }
      if (keybindingContainer.matchesKey(keyCode, scanCode)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, false));
        return true;
      }

      return false;
    });
  }
}
