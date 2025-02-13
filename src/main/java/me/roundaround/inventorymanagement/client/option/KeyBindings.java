package me.roundaround.inventorymanagement.client.option;

import me.roundaround.inventorymanagement.client.ButtonManager;
import me.roundaround.inventorymanagement.client.network.ClientNetworking;
import me.roundaround.inventorymanagement.roundalib.client.event.ScreenInputEvent;
import me.roundaround.inventorymanagement.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class KeyBindings {
  private KeyBindings() {
  }

  private static final ArrayList<KeyBinding> ALL_KEY_BINDINGS = new ArrayList<>();
  public static final KeyBinding CONFIGURE = register("configure");
  public static final KeyBinding SORT_CONTAINER = register("sortContainer");
  public static final KeyBinding SORT_PLAYER = register("sortPlayer");
  public static final KeyBinding SORT_ALL = register("sortAll");
  public static final KeyBinding STACK_FROM_CONTAINER = register("stackFromContainer");
  public static final KeyBinding STACK_INTO_CONTAINER = register("stackIntoContainer");
  public static final KeyBinding TRANSFER_FROM_CONTAINER = register("transferFromContainer");
  public static final KeyBinding TRANSFER_INTO_CONTAINER = register("transferIntoContainer");

  private static final Consumer<PlayerEntity> SEND_SORT_CONTAINER = ClientNetworking::sendSortContainer;
  private static final Consumer<PlayerEntity> SEND_SORT_PLAYER = ClientNetworking::sendSortPlayer;
  private static final Consumer<PlayerEntity> SEND_SORT_ALL = ClientNetworking::sendSortAll;
  private static final Consumer<PlayerEntity> SEND_STACK_FROM = (p) -> ClientNetworking.sendStackFromContainer();
  private static final Consumer<PlayerEntity> SEND_STACK_INTO = (p) -> ClientNetworking.sendStackIntoContainer();
  private static final Consumer<PlayerEntity> SEND_TRANSFER_FROM = (p) -> ClientNetworking.sendTransferFromContainer();
  private static final Consumer<PlayerEntity> SEND_TRANSFER_INTO = (p) -> ClientNetworking.sendTransferIntoContainer();

  private static boolean initialized = false;

  public static void init() {
    if (initialized) {
      return;
    }

    ALL_KEY_BINDINGS.forEach(KeyBindingHelper::registerKeyBinding);

    ScreenInputEvent.EVENT.register((screen, keyCode, scanCode, modifiers) -> {
      if (!(screen instanceof HandledScreen)) {
        return false;
      }

      if (CONFIGURE.matchesKey(keyCode, scanCode)) {
        //        GuiUtil.setScreen(new PerScreenConfigScreen(screen, InventoryManagementConfig.getInstance()
        //        .perScreenConfigs));
        return true;
      }

      Consumer<PlayerEntity> handler = getButtonMappedHandler(keyCode, scanCode);
      if (handler != null) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
          return false;
        }

        GuiUtil.playClickSound();
        handler.accept(player);
        return true;
      }

      return false;
    });

    initialized = true;
  }

  private static KeyBinding register(String id) {
    if (initialized) {
      throw new IllegalStateException("Cannot register keybindings after initialization.");
    }
    KeyBinding keyBinding = new KeyBinding(
        String.format("inventorymanagement.keybind.%s", id),
        InputUtil.Type.KEYSYM,
        InputUtil.UNKNOWN_KEY.getCode(),
        "inventorymanagement.keybind.category"
    );
    ALL_KEY_BINDINGS.add(keyBinding);
    return keyBinding;
  }

  private static Consumer<PlayerEntity> getButtonMappedHandler(int keyCode, int scanCode) {
    if (SORT_CONTAINER.matchesKey(keyCode, scanCode)) {
      return getSortContainerHandler();
    } else if (SORT_PLAYER.matchesKey(keyCode, scanCode)) {
      return getSortPlayerHandler();
    } else if (SORT_ALL.matchesKey(keyCode, scanCode)) {
      return getSortAllHandler();
    } else if (STACK_FROM_CONTAINER.matchesKey(keyCode, scanCode)) {
      return getStackFromHandler();
    } else if (STACK_INTO_CONTAINER.matchesKey(keyCode, scanCode)) {
      return getStackIntoHandler();
    } else if (TRANSFER_FROM_CONTAINER.matchesKey(keyCode, scanCode)) {
      return getTransferFromHandler();
    } else if (TRANSFER_INTO_CONTAINER.matchesKey(keyCode, scanCode)) {
      return getTransferIntoHandler();
    }

    return null;
  }

  private static Consumer<PlayerEntity> getSortContainerHandler() {
    return ButtonManager.getInstance().hasContainerSideSort() ? SEND_SORT_CONTAINER : null;
  }

  private static Consumer<PlayerEntity> getSortPlayerHandler() {
    return ButtonManager.getInstance().hasPlayerSideSort() ? SEND_SORT_PLAYER : null;
  }

  private static Consumer<PlayerEntity> getSortAllHandler() {
    ButtonManager buttonManager = ButtonManager.getInstance();
    boolean hasContainerSide = buttonManager.hasContainerSideSort();
    boolean hasPlayerSide = buttonManager.hasPlayerSideSort();

    if (hasContainerSide && hasPlayerSide) {
      return SEND_SORT_ALL;
    } else if (hasContainerSide) {
      return SEND_SORT_CONTAINER;
    } else if (hasPlayerSide) {
      return SEND_SORT_PLAYER;
    }

    return null;
  }

  private static Consumer<PlayerEntity> getStackFromHandler() {
    return ButtonManager.getInstance().hasContainerSideStack() ? SEND_STACK_FROM : null;
  }

  private static Consumer<PlayerEntity> getStackIntoHandler() {
    return ButtonManager.getInstance().hasPlayerSideStack() ? SEND_STACK_INTO : null;
  }

  private static Consumer<PlayerEntity> getTransferFromHandler() {
    return ButtonManager.getInstance().hasContainerSideTransfer() ? SEND_TRANSFER_FROM : null;
  }

  private static Consumer<PlayerEntity> getTransferIntoHandler() {
    return ButtonManager.getInstance().hasPlayerSideTransfer() ? SEND_TRANSFER_INTO : null;
  }
}
