package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.client.texture.GuiAtlasManager;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.GuiAtlasManagerInitCallback;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

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

    FabricLoader.getInstance()
        .getModContainer(InventoryManagementMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
                InventoryManagementMod.MOD_ID,
                "inventorymanagement-dark-ui"),
            container,
            Text.literal("Inventory Management Dark UI"),
            ResourcePackActivationType.NORMAL));
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

  public static GuiAtlasManager getGuiAtlasManager() {
    return guiAtlasManager;
  }
}
