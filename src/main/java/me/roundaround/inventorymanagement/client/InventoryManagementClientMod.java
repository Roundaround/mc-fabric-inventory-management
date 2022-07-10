package me.roundaround.inventorymanagement.client;

import org.lwjgl.glfw.GLFW;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.DefaultPositionEditScreen;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry;
import me.roundaround.roundalib.config.gui.control.SubScreenControl;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry.RegistrationException;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.event.HandleScreenInputCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    InventoryButtonsManager.INSTANCE.init();
    initCustomConfigControls();
    initKeyBindings();
  }

  private void initCustomConfigControls() {
    try {
      ControlFactoryRegistry.register(InventoryManagementMod.CONFIG.DEFAULT_POSITION.getId(),
          (PositionConfigOption configOption,
              OptionRowWidget parent,
              int top,
              int left,
              int height,
              int width) -> new SubScreenControl<>(
                  DefaultPositionEditScreen.getSubScreenFactory(),
                  configOption,
                  parent,
                  top,
                  left,
                  height,
                  width) {
                @Override
                protected Text getCurrentText() {
                  return Text.literal(configOption.getValue().toString());
                }
              });
    } catch (RegistrationException e) {
      InventoryManagementMod.LOGGER.error(e);
    }
  }

  private void initKeyBindings() {
    KeyBinding keybindingPlayer = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "inventorymanagement.keybind.position_edit.player",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "inventorymanagement.keybind.category"));

    KeyBinding keybindingContainer = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "inventorymanagement.keybind.position_edit.container",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "inventorymanagement.keybind.category"));

    HandleScreenInputCallback.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
      if (Screens.getButtons(screen).stream().noneMatch((button) -> button instanceof InventoryManagementButton)) {
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
