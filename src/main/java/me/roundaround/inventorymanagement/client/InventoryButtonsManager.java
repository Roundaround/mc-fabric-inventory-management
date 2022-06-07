package me.roundaround.inventorymanagement.client;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.SortInventoryButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class InventoryButtonsManager {
  public static final int BUTTON_SPACING = 1;
  public static final InventoryButtonsManager INSTANCE = new InventoryButtonsManager();

  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();
  private static final int BUTTON_START_OFFSET_X = -4;
  private static final int BUTTON_START_OFFSET_Y = -1;
  private static final int BUTTON_SHIFT_X = 0;
  private static final int BUTTON_SHIFT_Y = 1;
  private static final Set<InventoryManagementButton> PLAYER_BUTTONS = new HashSet<>();
  private static final Set<InventoryManagementButton> CONTAINER_BUTTONS = new HashSet<>();
  private static final Set<Class<? extends Inventory>> SORTABLE_INVENTORIES = new HashSet<>();
  private static final Set<Class<? extends Inventory>> TRANSFERABLE_INVENTORIES = new HashSet<>();

  private InventoryButtonsManager() {
    registerSortableContainer(PlayerInventory.class);
    registerSortableContainer(EnderChestInventory.class);
    registerSortableContainer(LootableContainerBlockEntity.class);

    registerTransferableContainer(PlayerInventory.class);
    registerTransferableContainer(EnderChestInventory.class);
    registerTransferableContainer(LootableContainerBlockEntity.class);
  }

  public static void registerSortableContainer(Class<? extends Inventory> clazz) {
    SORTABLE_INVENTORIES.add(clazz);
  }

  public static void registerTransferableContainer(Class<? extends Inventory> clazz) {
    TRANSFERABLE_INVENTORIES.add(clazz);
  }

  public static void init() {
    ScreenEvents.AFTER_INIT.register(InventoryButtonsManager::onScreenAfterInit);
  }

  private static void onScreenAfterInit(MinecraftClient client, Screen screen, float scaledWidth, float scaledHeight) {
    if (!(screen instanceof HandledScreen)) {
      return;
    }

    HandledScreen<?> handledScreen = ((HandledScreen<?>) screen);
  }

  private static void generatePlayerInventorySortButton(HandledScreen<?> screen, boolean isPlayerInventory) {
    if (screen instanceof InventoryScreen && !isPlayerInventory) {
      return;
    }

    Slot referenceSlot = getReferenceSlot(screen, isPlayerInventory);
    if (referenceSlot == null) {
      return;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return;
    }

    Inventory inventory = isPlayerInventory ? player.getInventory() : getContainerInventory(player);
    if (inventory == null) {
      return;
    }

    if (SORTABLE_INVENTORIES.stream().noneMatch(clazz -> clazz.isInstance(inventory))) {
      return;
    }

    if (getNumberOfBulkInventorySlots(screen, isPlayerInventory) < 3) {
      return;
    }

    Vec3i position = getButtonPosition(referenceSlot, isPlayerInventory);
    SortInventoryButton button = new SortInventoryButton(screen, position.getX(), position.getY(), isPlayerInventory);
    addButton(screen, button, isPlayerInventory);
  }

  private static void addButton(HandledScreen<?> screen, InventoryManagementButton button, boolean isPlayerInventory) {
    Screens.getButtons(screen).add(button);
    (isPlayerInventory ? PLAYER_BUTTONS : CONTAINER_BUTTONS).add(button);
  }

  private static Inventory getContainerInventory(ClientPlayerEntity player) {
    ScreenHandler currentScreenHandler = player.currentScreenHandler;
    if (currentScreenHandler == null) {
      return null;
    }

    try {
      return currentScreenHandler.getSlot(0).inventory;
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  private static Slot getReferenceSlot(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .max(Comparator.comparingInt(slot -> slot.x - slot.y))
        .orElse(null);
  }

  private static int getNumberOfBulkInventorySlots(HandledScreen<?> screen, boolean isPlayerInventory) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(screen.getScreenHandler() instanceof HorseScreenHandler) || slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private static int getNumberOfNonPlayerBulkInventorySlots(HandledScreen<? extends Inventory> screen) {
    return screen.getScreenHandler().slots.stream()
        .filter(slot -> !(slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(screen instanceof HorseScreen) || slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private static Vec3i getButtonPosition(Slot referenceSlot, boolean isPlayerInventory) {
    int x = BUTTON_START_OFFSET_X;
    int y = referenceSlot.y + BUTTON_START_OFFSET_Y;

    x += BUTTON_SHIFT_X * (InventoryManagementButton.WIDTH + BUTTON_SPACING)
        * (isPlayerInventory ? PLAYER_BUTTONS : CONTAINER_BUTTONS).size();
    y += BUTTON_SHIFT_Y * (InventoryManagementButton.HEIGHT + BUTTON_SPACING)
        * (isPlayerInventory ? PLAYER_BUTTONS : CONTAINER_BUTTONS).size();

    return new Vec3i(x, y, 0);
  }
}
