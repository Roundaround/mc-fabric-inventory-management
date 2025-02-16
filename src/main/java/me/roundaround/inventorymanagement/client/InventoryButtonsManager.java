package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.client.gui.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.TransferAllButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class InventoryButtonsManager {
  public static final InventoryButtonsManager INSTANCE = new InventoryButtonsManager();

  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();
  private static final int BUTTON_SPACING = 1;
  private static final int BUTTON_SHIFT_X = 0;
  private static final int BUTTON_SHIFT_Y = 1;

  private final LinkedHashSet<InventoryManagementButton> playerButtons = new LinkedHashSet<>();
  private final LinkedHashSet<InventoryManagementButton> containerButtons = new LinkedHashSet<>();
  private final HashSet<Class<? extends Inventory>> sortableInventories = new HashSet<>();
  private final HashSet<Class<? extends Inventory>> transerableInventories = new HashSet<>();
  private final HashSet<Class<? extends ScreenHandler>> sortableScreenHandlers = new HashSet<>();
  private final HashSet<Class<? extends ScreenHandler>> transferableScreenHandlers = new HashSet<>();

  private InventoryButtonsManager() {
    this.registerSortableContainer(PlayerInventory.class);
    this.registerSortableContainer(EnderChestInventory.class);
    this.registerSortableContainer(LootableContainerBlockEntity.class);

    this.registerTransferableContainer(PlayerInventory.class);
    this.registerTransferableContainer(EnderChestInventory.class);
    this.registerTransferableContainer(LootableContainerBlockEntity.class);

    this.registerSimpleInventorySortableHandler(GenericContainerScreenHandler.class);
    this.registerSimpleInventorySortableHandler(ShulkerBoxScreenHandler.class);
    this.registerSimpleInventorySortableHandler(HorseScreenHandler.class);
    this.registerSimpleInventorySortableHandler(HopperScreenHandler.class);

    this.registerSimpleInventoryTransferableHandler(GenericContainerScreenHandler.class);
    this.registerSimpleInventoryTransferableHandler(ShulkerBoxScreenHandler.class);
    this.registerSimpleInventoryTransferableHandler(HorseScreenHandler.class);
  }

  public void registerSortableContainer(Class<? extends Inventory> clazz) {
    this.sortableInventories.add(clazz);
  }

  public void registerTransferableContainer(Class<? extends Inventory> clazz) {
    this.transerableInventories.add(clazz);
  }

  public void registerSimpleInventorySortableHandler(Class<? extends ScreenHandler> clazz) {
    this.sortableScreenHandlers.add(clazz);
  }

  public void registerSimpleInventoryTransferableHandler(Class<? extends ScreenHandler> clazz) {
    this.transferableScreenHandlers.add(clazz);
  }

  public void init() {
    ScreenEvents.AFTER_INIT.register(this::onScreenAfterInit);
  }

  private void onScreenAfterInit(
      MinecraftClient client, Screen screen, float scaledWidth, float scaledHeight
  ) {
    if (!(screen instanceof HandledScreen<?> handledScreen)) {
      return;
    }

    this.playerButtons.clear();
    this.containerButtons.clear();

    // Container side
    this.generateSortButton(handledScreen, false);
    this.generateAutoStackButton(handledScreen, false);
    this.generateTransferAllButton(handledScreen, false);

    // Player side
    this.generateSortButton(handledScreen, true);
    this.generateAutoStackButton(handledScreen, true);
    this.generateTransferAllButton(handledScreen, true);
  }

  private void generateSortButton(HandledScreen<?> screen, boolean isPlayerInventory) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue() ||
        !InventoryManagementConfig.getInstance().showSort.getValue()) {
      return;
    }

    if (screen instanceof InventoryScreen && !isPlayerInventory) {
      return;
    }

    Slot referenceSlot = this.getReferenceSlot(screen, isPlayerInventory);
    if (referenceSlot == null) {
      return;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return;
    }

    Inventory inventory = isPlayerInventory ? player.getInventory() : InventoryHelper.getContainerInventory(player);
    if (inventory == null) {
      return;
    }

    if (inventory instanceof SimpleInventory) {
      if (this.sortableScreenHandlers.stream().noneMatch(clazz -> clazz.isInstance(screen.getScreenHandler()))) {
        return;
      }
    } else {
      if (this.sortableInventories.stream().noneMatch(clazz -> clazz.isInstance(inventory))) {
        return;
      }
    }

    if (this.getNumberOfBulkInventorySlots(screen, isPlayerInventory) < 3) {
      return;
    }

    Position position = this.getButtonPosition(screen, isPlayerInventory);
    SortInventoryButton button = new SortInventoryButton(screen, inventory, referenceSlot, position, isPlayerInventory);
    this.addButton(screen, button, isPlayerInventory);
  }

  private void generateAutoStackButton(HandledScreen<?> screen, boolean isPlayerInventory) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue() ||
        !InventoryManagementConfig.getInstance().showStack.getValue()) {
      return;
    }

    if (screen instanceof InventoryScreen && !isPlayerInventory) {
      return;
    }

    Slot referenceSlot = this.getReferenceSlot(screen, isPlayerInventory);
    if (referenceSlot == null) {
      return;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return;
    }

    Inventory fromInventory = isPlayerInventory ? InventoryHelper.getContainerInventory(player) : player.getInventory();
    Inventory toInventory = isPlayerInventory ? player.getInventory() : InventoryHelper.getContainerInventory(player);
    if (fromInventory == null || toInventory == null || fromInventory == toInventory) {
      return;
    }

    if (fromInventory instanceof SimpleInventory) {
      if (this.transferableScreenHandlers.stream().noneMatch(clazz -> clazz.isInstance(screen.getScreenHandler()))) {
        return;
      }
    } else {
      if (this.transerableInventories.stream().noneMatch(clazz -> clazz.isInstance(fromInventory))) {
        return;
      }
    }

    if (toInventory instanceof SimpleInventory) {
      if (this.transferableScreenHandlers.stream().noneMatch(clazz -> clazz.isInstance(screen.getScreenHandler()))) {
        return;
      }
    } else {
      if (this.transerableInventories.stream().noneMatch(clazz -> clazz.isInstance(toInventory))) {
        return;
      }
    }

    if (this.getNumberOfNonPlayerBulkInventorySlots(screen) < 3) {
      return;
    }

    Position position = this.getButtonPosition(screen, isPlayerInventory);
    AutoStackButton button = new AutoStackButton(screen, fromInventory, referenceSlot, position, isPlayerInventory);
    this.addButton(screen, button, isPlayerInventory);
  }

  private void generateTransferAllButton(HandledScreen<?> screen, boolean isPlayerInventory) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue() ||
        !InventoryManagementConfig.getInstance().showTransfer.getValue()) {
      return;
    }

    if (screen instanceof InventoryScreen && !isPlayerInventory) {
      return;
    }

    Slot referenceSlot = this.getReferenceSlot(screen, isPlayerInventory);
    if (referenceSlot == null) {
      return;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return;
    }

    Inventory fromInventory = isPlayerInventory ? InventoryHelper.getContainerInventory(player) : player.getInventory();
    Inventory toInventory = isPlayerInventory ? player.getInventory() : InventoryHelper.getContainerInventory(player);
    if (fromInventory == null || toInventory == null || fromInventory == toInventory) {
      return;
    }

    if (fromInventory instanceof SimpleInventory) {
      if (this.transferableScreenHandlers.stream().noneMatch(clazz -> clazz.isInstance(screen.getScreenHandler()))) {
        return;
      }
    } else {
      if (this.transerableInventories.stream().noneMatch(clazz -> clazz.isInstance(fromInventory))) {
        return;
      }
    }

    if (toInventory instanceof SimpleInventory) {
      if (this.transferableScreenHandlers.stream().noneMatch(clazz -> clazz.isInstance(screen.getScreenHandler()))) {
        return;
      }
    } else {
      if (this.transerableInventories.stream().noneMatch(clazz -> clazz.isInstance(toInventory))) {
        return;
      }
    }

    if (this.getNumberOfNonPlayerBulkInventorySlots(screen) < 3) {
      return;
    }

    Position position = this.getButtonPosition(screen, isPlayerInventory);
    TransferAllButton button = new TransferAllButton(screen, fromInventory, referenceSlot, position, isPlayerInventory);
    this.addButton(screen, button, isPlayerInventory);
  }

  private void addButton(
      HandledScreen<?> screen, InventoryManagementButton button, boolean isPlayerInventory
  ) {
    Screens.getButtons(screen).add(button);
    (isPlayerInventory ? this.playerButtons : this.containerButtons).add(button);
  }

  private Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .max(Comparator.comparingInt(slot -> slot.x - slot.y))
        .orElse(null);
  }

  private int getNumberOfBulkInventorySlots(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(screen.getScreenHandler() instanceof HorseScreenHandler) || slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private int getNumberOfNonPlayerBulkInventorySlots(HandledScreen<?> screen) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> !(slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(screen.getScreenHandler() instanceof HorseScreenHandler) || slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private Position getButtonPosition(Screen screen, boolean isPlayerInventory) {
    Position offset = InventoryManagementConfig.getInstance().screenPositions.get(screen, isPlayerInventory)
        .orElse(InventoryManagementConfig.getInstance().defaultPosition.getValue());
    return this.getButtonPosition((isPlayerInventory ? this.playerButtons : this.containerButtons).size(), offset);
  }

  public Position getButtonPosition(int index, Position offset) {
    int x = offset.x() + BUTTON_SHIFT_X * (InventoryManagementButton.WIDTH + BUTTON_SPACING) * index;
    int y = offset.y() + BUTTON_SHIFT_Y * (InventoryManagementButton.HEIGHT + BUTTON_SPACING) * index;

    return new Position(x, y);
  }

  public LinkedList<InventoryManagementButton> getPlayerButtons() {
    return new LinkedList<>(this.playerButtons);
  }

  public LinkedList<InventoryManagementButton> getContainerButtons() {
    return new LinkedList<>(this.containerButtons);
  }
}
