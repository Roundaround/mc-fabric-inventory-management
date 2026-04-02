package me.roundaround.inventorymanagement.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.inventorymanagement.generated.Constants;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.util.BuiltinResourcePack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
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
        Component.translatable("inventorymanagement.resourcepack.dark")
    );
  }

  private static void initKeyBindings() {
    KeyMapping keybindingPlayer = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "inventorymanagement.keybind.position_edit.player",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        KeyMapping.Category.INVENTORY
    ));

    KeyMapping keybindingContainer = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        "inventorymanagement.keybind.position_edit.container",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_L,
        KeyMapping.Category.INVENTORY
    ));

    HandleScreenInputCallback.EVENT.register((screen, input) -> {
      if (Screens.getWidgets(screen).stream().noneMatch((widget) -> widget instanceof InventoryManagementButton)) {
        return false;
      }

      if (keybindingPlayer.matches(input)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, true));
        return true;
      }
      if (keybindingContainer.matches(input)) {
        GuiUtil.setScreen(new PerScreenPositionEditScreen(screen, false));
        return true;
      }

      return false;
    });
  }
}
