package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.InventoryButtonsRegistry;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenConfigScreen;
import me.roundaround.inventorymanagement.client.texture.GuiAtlasManager;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.event.GuiAtlasManagerInitCallback;
import me.roundaround.inventorymanagement.event.HandleScreenInputCallback;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
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

    FabricLoader.getInstance()
        .getModContainer(InventoryManagementMod.MOD_ID)
        .ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(
                InventoryManagementMod.MOD_ID,
                "inventorymanagement-dark-ui"),
            container,
            Text.literal("Inventory Management Dark UI"),
            ResourcePackActivationType.NORMAL));

    initKeyBindings();
    initButtonRegistry();
  }

  private void initKeyBindings() {
    KeyBinding configureKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "inventorymanagement.keybind.configure",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_K,
        "inventorymanagement.keybind.category"));

    HandleScreenInputCallback.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
      if (!(screen instanceof HandledScreen)) {
        return false;
      }

      if (configureKeybind.matchesKey(keyCode, scanCode)) {
        GuiUtil.setScreen(new PerScreenConfigScreen(screen,
            InventoryManagementMod.CONFIG.perScreenConfigs
        ));
        return true;
      }

      return false;
    });
  }

  private void initButtonRegistry() {
    InventoryButtonsRegistry.INVENTORIES.sortableAndTransferable(PlayerInventory.class);
    InventoryButtonsRegistry.INVENTORIES.sortableAndTransferable(EnderChestInventory.class);
    InventoryButtonsRegistry.INVENTORIES.sortableAndTransferable(LootableContainerBlockEntity.class);

    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(GenericContainerScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(ShulkerBoxScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortableAndTransferable(HorseScreenHandler.class);
    InventoryButtonsRegistry.SCREEN_HANDLERS.sortable(HopperScreenHandler.class);

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit());
  }

  public static GuiAtlasManager getGuiAtlasManager() {
    return guiAtlasManager;
  }
}
