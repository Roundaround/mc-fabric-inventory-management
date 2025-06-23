package me.roundaround.inventorymanagement.client;

import org.lwjgl.glfw.GLFW;

import me.roundaround.gradle.api.annotation.Entrypoint;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.client.texture.GuiAtlasManager;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.GuiAtlasManagerInitCallback;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.inventorymanagement.generated.Constants;
import me.roundaround.inventorymanagement.roundalib.client.gui.util.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.util.BuiltinResourcePack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;

@Entrypoint(Entrypoint.CLIENT)
public class InventoryManagementClientMod implements ClientModInitializer {
  private static GuiAtlasManager guiAtlasManager = null;

  @Override
  public void onInitializeClient() {
    GuiAtlasManagerInitCallback.EVENT.register((client) -> {
      guiAtlasManager = new GuiAtlasManager(client.getTextureManager());
      ResourceManager rawResourceManager = client.getResourceManager();
      if (rawResourceManager instanceof ReloadableResourceManagerImpl resourceManager) {
        resourceManager.registerReloader(guiAtlasManager);
      }
    });

    ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
      if (guiAtlasManager != null) {
        guiAtlasManager.close();
      }
    });

    InventoryButtonsManager.INSTANCE.init();
    ConfigControlRegister.init();
    initKeyBindings();

    BuiltinResourcePack.register(
        Constants.MOD_ID,
        "inventorymanagement-dark-ui",
        Text.translatable("inventorymanagement.resourcepack.dark"));
  }

  private static void initKeyBindings() {
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

  public static GuiAtlasManager getGuiAtlasManager() {
    return guiAtlasManager;
  }
}
