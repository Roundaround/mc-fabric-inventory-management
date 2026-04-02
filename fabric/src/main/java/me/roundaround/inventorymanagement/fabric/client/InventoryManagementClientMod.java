package me.roundaround.inventorymanagement.fabric.client;

import me.roundaround.inventorymanagement.Constants;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.compat.roundalib.ConfigControlRegister;
import me.roundaround.inventorymanagement.fabric.event.HandleScreenInputCallback;
import me.roundaround.inventorymanagement.network.Networking;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class InventoryManagementClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Wire the networking bridge
    ClientNetworking.init(new ClientNetworking.Sender() {
      @Override
      public void sendStack(boolean fromPlayerInventory) {
        ClientPlayNetworking.send(new Networking.StackC2S(fromPlayerInventory));
      }

      @Override
      public void sendSort(boolean isPlayerInventory) {
        ClientPlayNetworking.send(new Networking.SortC2S(isPlayerInventory));
      }

      @Override
      public void sendTransfer(boolean fromPlayerInventory) {
        ClientPlayNetworking.send(new Networking.TransferC2S(fromPlayerInventory));
      }
    });

    // Register the screen-init event to add buttons
    ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
      InventoryButtonsManager.INSTANCE.onScreenAfterInit(screen, (button) -> {
        Screens.getButtons(screen).add(button);
      });
    });

    ConfigControlRegister.init();
    initKeyBindings();

    // Register the built-in dark UI resource pack
    FabricLoader.getInstance().getModContainer(Constants.MOD_ID).ifPresent(container -> {
      ResourceManagerHelper.registerBuiltinResourcePack(
          Identifier.of(Constants.MOD_ID, "inventorymanagement-dark-ui"),
          container,
          Text.translatable("inventorymanagement.resourcepack.dark"),
          ResourcePackActivationType.NORMAL
      );
    });
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
