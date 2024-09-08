package me.roundaround.inventorymanagement.client;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.ButtonRegistry;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.client.gui.widget.button.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.ButtonBase;
import me.roundaround.inventorymanagement.client.gui.widget.button.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.TransferAllButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.config.value.ButtonVisibility;
import me.roundaround.inventorymanagement.event.BeforeCloseHandledScreen;
import me.roundaround.roundalib.client.gui.util.Coords;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.ScreenHandler;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ButtonManager {
  public static final int BUTTON_WIDTH = ButtonBase.WIDTH;
  public static final int BUTTON_HEIGHT = ButtonBase.HEIGHT;
  public static final int BUTTON_SPACING = 1;

  private static final int BUTTON_SHIFT_X = 0;
  private static final int BUTTON_SHIFT_Y = 1;

  private static ButtonManager instance = null;

  private final LinkedHashSet<ButtonBase<?, ?>> playerButtons = new LinkedHashSet<>();
  private final LinkedHashSet<ButtonBase<?, ?>> containerButtons = new LinkedHashSet<>();

  private ButtonManager() {
  }

  public static ButtonManager getInstance() {
    if (instance == null) {
      instance = new ButtonManager();
      ScreenEvents.AFTER_INIT.register(instance::onScreenAfterInit);
      BeforeCloseHandledScreen.EVENT.register(instance::beforeCloseHandledScreen);
    }
    return instance;
  }

  public static void init() {
    getInstance();
  }

  public boolean hasPlayerSideSort() {
    return this.playerButtons.stream().anyMatch((button) -> button instanceof SortInventoryButton<?, ?>);
  }

  public boolean hasContainerSideSort() {
    return this.containerButtons.stream().anyMatch((button) -> button instanceof SortInventoryButton<?, ?>);
  }

  public boolean hasPlayerSideStack() {
    return this.playerButtons.stream().anyMatch((button) -> button instanceof AutoStackButton<?, ?>);
  }

  public boolean hasContainerSideStack() {
    return this.containerButtons.stream().anyMatch((button) -> button instanceof AutoStackButton<?, ?>);
  }

  public boolean hasPlayerSideTransfer() {
    return this.playerButtons.stream().anyMatch((button) -> button instanceof TransferAllButton<?, ?>);
  }

  public boolean hasContainerSideTransfer() {
    return this.containerButtons.stream().anyMatch((button) -> button instanceof TransferAllButton<?, ?>);
  }

  private void onScreenAfterInit(
      MinecraftClient client, Screen screen, float scaledWidth, float scaledHeight
  ) {
    if (!(screen instanceof HandledScreen<?> handledScreen)) {
      return;
    }

    // Container side
    this.containerButtons.clear();
    ButtonContext<?, ?> containerContext = new ButtonContext<>(handledScreen, false);
    generateSortButton(containerContext);
    generateAutoStackButton(containerContext);
    generateTransferAllButton(containerContext);

    // Player side
    this.playerButtons.clear();
    ButtonContext<?, ?> playerContext = new ButtonContext<>(handledScreen, true);
    generateSortButton(playerContext);
    generateAutoStackButton(playerContext);
    generateTransferAllButton(playerContext);
  }

  private void beforeCloseHandledScreen(ClientPlayerEntity player, ScreenHandler screenHandler) {
    this.containerButtons.clear();
    this.playerButtons.clear();
  }

  private boolean shouldTryGeneratingSortButton(ButtonContext<?, ?> context) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue()) {
      return false;
    }

    if (!InventoryManagementConfig.getInstance().showSort.getValue()) {
      return false;
    }

    LinkedList<ButtonVisibility> visibilitySettings = new LinkedList<>();

    // Per screen config
    Optional.ofNullable(InventoryManagementConfig.getInstance().perScreenConfigs.getSortVisibility(context.getScreen(),
        context.isPlayerInventory()
    )).ifPresent(visibilitySettings::add);

    // Defaults based on registry, tied to screen, handler, or inventory
    visibilitySettings.add(ButtonRegistry.getInstance().getSortButtonVisibility(context));

    for (ButtonVisibility visibility : visibilitySettings) {
      if (ButtonVisibility.SHOW.equals(visibility)) {
        return true;
      } else if (ButtonVisibility.HIDE.equals(visibility)) {
        return false;
      }
    }

    return false;
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> void generateSortButton(
      ButtonContext<H, S> context
  ) {
    if (!this.shouldTryGeneratingSortButton(context)) {
      return;
    }

    Coords offset = posToCoords(getButtonOffset(context));
    PositioningFunction<H, S> positioningFunction = getPositioningFunction(context);
    SortInventoryButton<H, S> button = new SortInventoryButton<>(offset, positioningFunction, context);
    addButton(context.getScreen(), button, context.isPlayerInventory());
  }

  private boolean shouldTryGeneratingStackButton(ButtonContext<?, ?> context) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue()) {
      return false;
    }

    if (!context.hasPlayerInventory() || !context.hasContainerInventory() ||
        context.getPlayerInventory() == context.getContainerInventory()) {
      return false;
    }

    if (!InventoryManagementConfig.getInstance().showStack.getValue()) {
      return false;
    }

    LinkedList<ButtonVisibility> visibilitySettings = new LinkedList<>();

    // Per screen config
    Optional.ofNullable(InventoryManagementConfig.getInstance().perScreenConfigs.getStackVisibility(context.getScreen(),
        context.isPlayerInventory()
    )).ifPresent(visibilitySettings::add);

    // Defaults based on registry, tied to screen, handler, or inventory
    visibilitySettings.add(ButtonRegistry.getInstance().getTransferAndStackButtonVisibility(context));

    for (ButtonVisibility visibility : visibilitySettings) {
      if (ButtonVisibility.SHOW.equals(visibility)) {
        return true;
      } else if (ButtonVisibility.HIDE.equals(visibility)) {
        return false;
      }
    }

    return false;
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> void generateAutoStackButton(
      ButtonContext<H, S> context
  ) {
    if (!this.shouldTryGeneratingStackButton(context)) {
      return;
    }

    Coords offset = posToCoords(getButtonOffset(context));
    PositioningFunction<H, S> positioningFunction = getPositioningFunction(context);
    AutoStackButton<H, S> button = new AutoStackButton<>(offset, positioningFunction, context);
    addButton(context.getScreen(), button, context.isPlayerInventory());
  }

  private boolean shouldTryGeneratingTransferButton(ButtonContext<?, ?> context) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue()) {
      return false;
    }

    if (!context.hasPlayerInventory() || !context.hasContainerInventory() ||
        context.getPlayerInventory() == context.getContainerInventory()) {
      return false;
    }

    if (!InventoryManagementConfig.getInstance().showTransfer.getValue()) {
      return false;
    }

    LinkedList<ButtonVisibility> visibilitySettings = new LinkedList<>();

    // Per screen config
    Optional.ofNullable(
        InventoryManagementConfig.getInstance().perScreenConfigs.getTransferVisibility(context.getScreen(),
            context.isPlayerInventory()
        )).ifPresent(visibilitySettings::add);

    // Defaults based on registry, tied to screen, handler, or inventory
    visibilitySettings.add(ButtonRegistry.getInstance().getTransferAndStackButtonVisibility(context));

    for (ButtonVisibility visibility : visibilitySettings) {
      if (ButtonVisibility.SHOW.equals(visibility)) {
        return true;
      } else if (ButtonVisibility.HIDE.equals(visibility)) {
        return false;
      }
    }

    return false;
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> void generateTransferAllButton(
      ButtonContext<H, S> context
  ) {
    if (!this.shouldTryGeneratingTransferButton(context)) {
      return;
    }

    Coords offset = posToCoords(getButtonOffset(context));
    PositioningFunction<H, S> positioningFunction = getPositioningFunction(context);
    TransferAllButton<H, S> button = new TransferAllButton<>(offset, positioningFunction, context);
    addButton(context.getScreen(), button, context.isPlayerInventory());
  }

  private void addButton(
      HandledScreen<?> screen, ButtonBase<?, ?> button, boolean isPlayerInventory
  ) {
    Screens.getButtons(screen).add(button);
    (isPlayerInventory ? this.playerButtons : this.containerButtons).add(button);
  }

  private Position getButtonOffset(ButtonContext<?, ?> context) {
    Position offset = InventoryManagementConfig.getInstance().perScreenConfigs.getPosition(context.getScreen(),
        context.isPlayerInventory()
    );
    if (offset == null) {
      offset = InventoryManagementConfig.getInstance().defaultPosition.getValue();
    }

    return getButtonOffset((context.isPlayerInventory() ? this.playerButtons : this.containerButtons).size(), offset);
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> getPositioningFunction(
      ButtonContext<H, S> context
  ) {
    return ButtonRegistry.getInstance()
        .getPositioningFunction(context)
        .orElseGet(PositioningFunction::refSlotYAndBgRight);
  }

  public Position getButtonOffset(int index, Position offset) {
    int x = offset.x() + BUTTON_SHIFT_X * (ButtonBase.WIDTH + BUTTON_SPACING) * index;
    int y = offset.y() + BUTTON_SHIFT_Y * (ButtonBase.HEIGHT + BUTTON_SPACING) * index;

    return new Position(x, y);
  }

  private static Coords posToCoords(Position position) {
    return new Coords(position.x(), position.y());
  }
}
