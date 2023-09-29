package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.InventoryManagementEntrypointHandler;
import me.roundaround.inventorymanagement.client.gui.widget.button.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.TransferAllButton;
import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.*;

import java.util.HashMap;
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

  private final LinkedHashSet<InventoryManagementButton<?>> playerButtons = new LinkedHashSet<>();
  private final LinkedHashSet<InventoryManagementButton<?>> containerButtons =
      new LinkedHashSet<>();
  private final HashSet<Class<? extends Inventory>> sortableInventories = new HashSet<>();
  private final HashSet<Class<? extends Inventory>> transferableInventories = new HashSet<>();
  private final HashSet<Class<? extends ScreenHandler>> sortableScreenHandlers = new HashSet<>();
  private final HashSet<Class<? extends ScreenHandler>> transferableScreenHandlers =
      new HashSet<>();
  private final HashSet<Class<? extends HandledScreen<?>>> sortableScreensPlayerSide =
      new HashSet<>();
  private final HashSet<Class<? extends HandledScreen<?>>> transferableScreensPlayerSide =
      new HashSet<>();
  private final HashSet<Class<? extends HandledScreen<?>>> sortableScreensContainerSide =
      new HashSet<>();
  private final HashSet<Class<? extends HandledScreen<?>>> transferableScreensContainerSide =
      new HashSet<>();
  private final HashMap<String, ButtonBasePositionFunction<?>> buttonBasePositionFunctions =
      new HashMap<>();

  private InventoryButtonsManager() {
    registerSortableContainer(PlayerInventory.class);
    registerSortableContainer(EnderChestInventory.class);
    registerSortableContainer(LootableContainerBlockEntity.class);

    registerTransferableContainer(PlayerInventory.class);
    registerTransferableContainer(EnderChestInventory.class);
    registerTransferableContainer(LootableContainerBlockEntity.class);

    registerSimpleInventorySortableHandler(GenericContainerScreenHandler.class);
    registerSimpleInventorySortableHandler(ShulkerBoxScreenHandler.class);
    registerSimpleInventorySortableHandler(HorseScreenHandler.class);
    registerSimpleInventorySortableHandler(HopperScreenHandler.class);

    registerSimpleInventoryTransferableHandler(GenericContainerScreenHandler.class);
    registerSimpleInventoryTransferableHandler(ShulkerBoxScreenHandler.class);
    registerSimpleInventoryTransferableHandler(HorseScreenHandler.class);

    FabricLoader.getInstance()
        .getEntrypointContainers("inventorymanagement", InventoryManagementEntrypointHandler.class)
        .forEach((entrypoint) -> entrypoint.getEntrypoint().onInventoryManagementInit(this));
  }

  public void registerSortableContainer(Class<? extends Inventory> clazz) {
    sortableInventories.add(clazz);
  }

  public void registerTransferableContainer(Class<? extends Inventory> clazz) {
    transferableInventories.add(clazz);
  }

  public void registerSimpleInventorySortableHandler(Class<? extends ScreenHandler> clazz) {
    sortableScreenHandlers.add(clazz);
  }

  public void registerSimpleInventoryTransferableHandler(Class<? extends ScreenHandler> clazz) {
    transferableScreenHandlers.add(clazz);
  }

  public void registerSortableScreenPlayerSide(Class<? extends HandledScreen<?>> clazz) {
    sortableScreensPlayerSide.add(clazz);
  }

  public void registerTransferableScreenPlayerSide(Class<? extends HandledScreen<?>> clazz) {
    transferableScreensPlayerSide.add(clazz);
  }

  public void registerSortableScreenContainerSide(Class<? extends HandledScreen<?>> clazz) {
    sortableScreensContainerSide.add(clazz);
  }

  public void registerTransferableScreenContainerSide(Class<? extends HandledScreen<?>> clazz) {
    transferableScreensContainerSide.add(clazz);
  }

  public <T extends HandledScreen<?>> void registerButtonBasePositionFunction(
      Class<T> clazz, ButtonBasePositionFunction<T> function) {
    buttonBasePositionFunctions.put(InventoryManagementMod.getClassKey(clazz), function);
  }

  public void init() {
    ScreenEvents.AFTER_INIT.register(this::onScreenAfterInit);
  }

  private void onScreenAfterInit(
      MinecraftClient client, Screen screen, float scaledWidth, float scaledHeight) {
    if (!(screen instanceof HandledScreen<?> handledScreen)) {
      return;
    }

    playerButtons.clear();
    containerButtons.clear();

    // Container side
    generateSortButton(handledScreen, false);
    generateAutoStackButton(handledScreen, false);
    generateTransferAllButton(handledScreen, false);

    // Player side
    generateSortButton(handledScreen, true);
    generateAutoStackButton(handledScreen, true);
    generateTransferAllButton(handledScreen, true);
  }

  private boolean shouldTryGeneratingSortButton(Context context) {
    if (!InventoryManagementMod.CONFIG.MOD_ENABLED.getValue()) {
      return false;
    }

    ButtonVisibility sortVisibility =
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getSortVisibility(context.screen,
            context.isPlayerInventory);

    if (ButtonVisibility.HIDE.equals(sortVisibility)) {
      return false;
    }

    if (!ButtonVisibility.SHOW.equals(sortVisibility) &&
        !InventoryManagementMod.CONFIG.SHOW_SORT.getValue()) {
      return false;
    }

    if (context.screen instanceof InventoryScreen && !context.isPlayerInventory) {
      return false;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return false;
    }

    context.fromInventory = context.isPlayerInventory
        ? player.getInventory()
        : InventoryHelper.getContainerInventory(player);
    if (context.fromInventory == null) {
      return false;
    }

    if (ButtonVisibility.SHOW.equals(sortVisibility)) {
      return true;
    }

    if ((context.isPlayerInventory
        ? sortableScreensPlayerSide
        : sortableScreensContainerSide).stream()
        .anyMatch(clazz -> clazz.isInstance(context.screen))) {
      return true;
    }

    if (context.fromInventory instanceof SimpleInventory) {
      if (sortableScreenHandlers.stream()
          .noneMatch(clazz -> clazz.isInstance(context.screen.getScreenHandler()))) {
        return false;
      }
    } else {
      if (sortableInventories.stream()
          .noneMatch(clazz -> clazz.isInstance(context.fromInventory))) {
        return false;
      }
    }

    if (getNumberOfBulkInventorySlots(context) < 3) {
      return false;
    }

    return true;
  }

  private <T extends HandledScreen<?>> void generateSortButton(
      T screen, boolean isPlayerInventory) {
    Context context = new Context(screen, isPlayerInventory);

    if (!this.shouldTryGeneratingSortButton(context)) {
      return;
    }

    Position position = getButtonPosition(context);
    ButtonBasePositionFunction<T> positionFunction = getButtonBasePositionFunction(screen);
    SortInventoryButton<T> button =
        new SortInventoryButton<>(screen, positionFunction, position, isPlayerInventory);
    addButton(screen, button, isPlayerInventory);
  }

  private boolean shouldTryGeneratingStackButton(Context context) {
    if (!InventoryManagementMod.CONFIG.MOD_ENABLED.getValue()) {
      return false;
    }

    ButtonVisibility stackVisibility =
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getStackVisibility(context.screen,
            context.isPlayerInventory);

    if (ButtonVisibility.HIDE.equals(stackVisibility)) {
      return false;
    }

    if (!ButtonVisibility.SHOW.equals(stackVisibility) &&
        !InventoryManagementMod.CONFIG.SHOW_STACK.getValue()) {
      return false;
    }

    if (context.screen instanceof InventoryScreen && !context.isPlayerInventory) {
      return false;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return false;
    }

    context.fromInventory = context.isPlayerInventory
        ? InventoryHelper.getContainerInventory(player)
        : player.getInventory();
    context.toInventory = context.isPlayerInventory
        ? player.getInventory()
        : InventoryHelper.getContainerInventory(player);
    if (context.fromInventory == null || context.toInventory == null ||
        context.fromInventory == context.toInventory) {
      return false;
    }

    if (ButtonVisibility.SHOW.equals(stackVisibility)) {
      return true;
    }

    if ((context.isPlayerInventory
        ? transferableScreensPlayerSide
        : transferableScreensContainerSide).stream()
        .anyMatch(clazz -> clazz.isInstance(context.screen))) {
      return true;
    }

    if (context.fromInventory instanceof SimpleInventory) {
      if (transferableScreenHandlers.stream()
          .noneMatch(clazz -> clazz.isInstance(context.screen.getScreenHandler()))) {
        return false;
      }
    } else {
      if (transferableInventories.stream()
          .noneMatch(clazz -> clazz.isInstance(context.fromInventory))) {
        return false;
      }
    }

    if (context.toInventory instanceof SimpleInventory) {
      if (transferableScreenHandlers.stream()
          .noneMatch(clazz -> clazz.isInstance(context.screen.getScreenHandler()))) {
        return false;
      }
    } else {
      if (transferableInventories.stream()
          .noneMatch(clazz -> clazz.isInstance(context.toInventory))) {
        return false;
      }
    }

    if (getNumberOfNonPlayerBulkInventorySlots(context) < 3) {
      return false;
    }

    return true;
  }

  private <T extends HandledScreen<?>> void generateAutoStackButton(
      T screen, boolean isPlayerInventory) {
    Context context = new Context(screen, isPlayerInventory);

    if (!this.shouldTryGeneratingStackButton(context)) {
      return;
    }

    Position position = getButtonPosition(context);
    ButtonBasePositionFunction<T> positionFunction = getButtonBasePositionFunction(screen);
    AutoStackButton<T> button =
        new AutoStackButton<>(screen, positionFunction, position, isPlayerInventory);
    addButton(screen, button, isPlayerInventory);
  }

  private boolean shouldTryGeneratingTransferButton(Context context) {
    if (!InventoryManagementMod.CONFIG.MOD_ENABLED.getValue()) {
      return false;
    }

    ButtonVisibility transferVisibility =
        InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getTransferVisibility(context.screen,
            context.isPlayerInventory);

    if (ButtonVisibility.HIDE.equals(transferVisibility)) {
      return false;
    }

    if (!ButtonVisibility.SHOW.equals(transferVisibility) &&
        !InventoryManagementMod.CONFIG.SHOW_TRANSFER.getValue()) {
      return false;
    }

    if (context.screen instanceof InventoryScreen && !context.isPlayerInventory) {
      return false;
    }

    ClientPlayerEntity player = MINECRAFT.player;
    if (player == null) {
      return false;
    }

    context.fromInventory = context.isPlayerInventory
        ? InventoryHelper.getContainerInventory(player)
        : player.getInventory();
    context.toInventory = context.isPlayerInventory
        ? player.getInventory()
        : InventoryHelper.getContainerInventory(player);
    if (context.fromInventory == null || context.toInventory == null ||
        context.fromInventory == context.toInventory) {
      return false;
    }

    if (ButtonVisibility.SHOW.equals(transferVisibility)) {
      return true;
    }

    if ((context.isPlayerInventory
        ? transferableScreensPlayerSide
        : transferableScreensContainerSide).stream()
        .anyMatch(clazz -> clazz.isInstance(context.screen))) {
      return true;
    }

    if (context.fromInventory instanceof SimpleInventory) {
      if (transferableScreenHandlers.stream()
          .noneMatch(clazz -> clazz.isInstance(context.screen.getScreenHandler()))) {
        return false;
      }
    } else {
      if (transferableInventories.stream()
          .noneMatch(clazz -> clazz.isInstance(context.fromInventory))) {
        return false;
      }
    }

    if (context.toInventory instanceof SimpleInventory) {
      if (transferableScreenHandlers.stream()
          .noneMatch(clazz -> clazz.isInstance(context.screen.getScreenHandler()))) {
        return false;
      }
    } else {
      if (transferableInventories.stream()
          .noneMatch(clazz -> clazz.isInstance(context.toInventory))) {
        return false;
      }
    }

    if (getNumberOfNonPlayerBulkInventorySlots(context) < 3) {
      return false;
    }

    return true;
  }

  private <T extends HandledScreen<?>> void generateTransferAllButton(
      T screen, boolean isPlayerInventory) {
    Context context = new Context(screen, isPlayerInventory);

    if (!this.shouldTryGeneratingTransferButton(context)) {
      return;
    }

    Position position = getButtonPosition(context);
    ButtonBasePositionFunction<T> positionFunction = getButtonBasePositionFunction(screen);
    TransferAllButton<T> button =
        new TransferAllButton<>(screen, positionFunction, position, isPlayerInventory);
    addButton(screen, button, isPlayerInventory);
  }

  private void addButton(
      HandledScreen<?> screen, InventoryManagementButton<?> button, boolean isPlayerInventory) {
    Screens.getButtons(screen).add(button);
    (isPlayerInventory ? playerButtons : containerButtons).add(button);
  }

  private int getNumberOfBulkInventorySlots(Context context) {
    return context.screen.getScreenHandler().slots.stream()
        .filter(slot -> context.isPlayerInventory == (slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(context.screen.getScreenHandler() instanceof HorseScreenHandler) ||
            slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private int getNumberOfNonPlayerBulkInventorySlots(Context context) {
    return context.screen.getScreenHandler().slots.stream()
        .filter(slot -> !(slot.inventory instanceof PlayerInventory))
        .filter(slot -> !(context.screen.getScreenHandler() instanceof HorseScreenHandler) ||
            slot.getIndex() >= 2)
        .mapToInt(slot -> 1)
        .sum();
  }

  private Position getButtonPosition(Context context) {
    Position offset = InventoryManagementMod.CONFIG.PER_SCREEN_CONFIGS.getPosition(context.screen,
        context.isPlayerInventory);
    if (offset == null) {
      offset = InventoryManagementMod.CONFIG.DEFAULT_POSITION.getValue();
    }

    return getButtonPosition((context.isPlayerInventory ? playerButtons : containerButtons).size(),
        offset);
  }

  public Position getButtonPosition(int index, Position offset) {
    int x =
        offset.x() + BUTTON_SHIFT_X * (InventoryManagementButton.WIDTH + BUTTON_SPACING) * index;
    int y =
        offset.y() + BUTTON_SHIFT_Y * (InventoryManagementButton.HEIGHT + BUTTON_SPACING) * index;

    return new Position(x, y);
  }

  @SuppressWarnings("unchecked")
  public <T extends HandledScreen<?>> ButtonBasePositionFunction<T> getButtonBasePositionFunction(
      T screen) {
    // Suppressing unchecked cast warning because the API enforces that the key and implementation
    // are of the same type.
    return (ButtonBasePositionFunction<T>) buttonBasePositionFunctions.getOrDefault(
        InventoryManagementMod.getClassKey(screen.getClass()),
        ButtonBasePositionFunction.getDefault());
  }

  public LinkedList<InventoryManagementButton<?>> getPlayerButtons() {
    return new LinkedList<>(playerButtons);
  }

  public LinkedList<InventoryManagementButton<?>> getContainerButtons() {
    return new LinkedList<>(containerButtons);
  }

  private static class Context {
    public HandledScreen<?> screen;
    public boolean isPlayerInventory;
    public Inventory fromInventory;
    public Inventory toInventory;

    public Context(HandledScreen<?> screen, boolean isPlayerInventory) {
      this.screen = screen;
      this.isPlayerInventory = isPlayerInventory;
    }
  }
}
