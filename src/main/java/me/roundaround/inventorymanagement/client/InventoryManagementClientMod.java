package me.roundaround.inventorymanagement.client;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.inventorymanagement.generated.Constants;
import me.roundaround.inventorymanagement.roundalib.client.gui.util.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.util.BuiltinResourcePack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Entrypoint(Entrypoint.CLIENT)
public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    InventoryButtonsManager.INSTANCE.init();
    ConfigControlRegister.init();
    initKeyBindings();

    BuiltinResourcePack.register(
        Constants.MOD_ID,
        "inventorymanagement-dark-ui",
        Text.translatable("inventorymanagement.resourcepack.dark")
    );
  }

  private static void initKeyBindings() {
    KeyBinding keybindingPlayer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "inventorymanagement.keybind.position_edit.player",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        KeyBinding.Category.INVENTORY
    ));

    KeyBinding keybindingContainer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "inventorymanagement.keybind.position_edit.container",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_L,
        KeyBinding.Category.INVENTORY
    ));

    HandleScreenInputCallback.EVENT.register((screen, input) -> {
      if (Screens.getButtons(screen).stream().noneMatch((button) -> button instanceof InventoryManagementButton)) {
        return false;
      }

      if (keybindingPlayer.matchesKey(input)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, true));
        return true;
      }
      if (keybindingContainer.matchesKey(input)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, false));
        return true;
      }

      return false;
    });
  }
}
