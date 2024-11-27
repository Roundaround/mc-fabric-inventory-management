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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ButtonManager {
  public static final int BUTTON_HEIGHT = ButtonBase.HEIGHT;
  public static final int BUTTON_SPACING = 1;

  private static final int BUTTON_SHIFT_Y = 1;
  private static final String SORT_KEY = "sort";
  private static final String STACK_KEY = "stack";
  private static final String TRANSFER_KEY = "transfer";

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

    this.checkAndMarkSortVisibility(containerContext);
    this.checkAndMarkStackVisibility(containerContext);
    this.checkAndMarkTransferVisibility(containerContext);

    this.generateSortButton(containerContext);
    this.generateStackButton(containerContext);
    this.generateTransferButton(containerContext);

    // Player side
    this.playerButtons.clear();
    ButtonContext<?, ?> playerContext = new ButtonContext<>(handledScreen, true);

    this.checkAndMarkSortVisibility(playerContext);
    this.checkAndMarkStackVisibility(playerContext);
    this.checkAndMarkTransferVisibility(playerContext);

    this.generateSortButton(playerContext);
    this.generateStackButton(playerContext);
    this.generateTransferButton(playerContext);
  }

  private void beforeCloseHandledScreen(ClientPlayerEntity player, ScreenHandler screenHandler) {
    this.containerButtons.clear();
    this.playerButtons.clear();
  }

  private void checkAndMarkSortVisibility(ButtonContext<?, ?> context) {
    this.checkAndMarkVisibility(context, SORT_KEY, InventoryManagementConfig.getInstance().showSort::getValue,
        InventoryManagementConfig.getInstance().perScreenConfigs::getSortVisibility,
        ButtonRegistry.getInstance()::getSortButtonVisibility
    );
  }

  private void checkAndMarkStackVisibility(ButtonContext<?, ?> context) {
    this.checkAndMarkVisibility(context, STACK_KEY, InventoryManagementConfig.getInstance().showStack::getValue,
        InventoryManagementConfig.getInstance().perScreenConfigs::getStackVisibility,
        ButtonRegistry.getInstance()::getTransferAndStackButtonVisibility
    );
  }

  private void checkAndMarkTransferVisibility(ButtonContext<?, ?> context) {
    this.checkAndMarkVisibility(context, TRANSFER_KEY, InventoryManagementConfig.getInstance().showTransfer::getValue,
        InventoryManagementConfig.getInstance().perScreenConfigs::getTransferVisibility,
        ButtonRegistry.getInstance()::getTransferAndStackButtonVisibility
    );
  }

  private void checkAndMarkVisibility(
      ButtonContext<?, ?> context,
      String buttonKey,
      Supplier<Boolean> globalShowSupplier,
      BiFunction<Screen, Boolean, ButtonVisibility> perScreenVisibilityFunction,
      Function<ButtonContext<?, ?>, ButtonVisibility> registryVisibilityFunction
  ) {
    if (this.shouldShow(context, globalShowSupplier, perScreenVisibilityFunction, registryVisibilityFunction)) {
      context.markButtonToShow(buttonKey);
    }
  }

  private boolean shouldShow(
      ButtonContext<?, ?> context,
      Supplier<Boolean> globalShowSupplier,
      BiFunction<Screen, Boolean, ButtonVisibility> perScreenVisibilityFunction,
      Function<ButtonContext<?, ?>, ButtonVisibility> registryVisibilityFunction
  ) {
    if (!InventoryManagementConfig.getInstance().modEnabled.getValue()) {
      return false;
    }

    if (!globalShowSupplier.get()) {
      return false;
    }

    LinkedList<ButtonVisibility> visibilitySettings = new LinkedList<>();

    // Per screen config
    Optional.ofNullable(perScreenVisibilityFunction.apply(context.getScreen(), context.isPlayerInventory()))
        .ifPresent(visibilitySettings::add);

    // Defaults based on registry, tied to screen, handler, or inventory
    visibilitySettings.add(registryVisibilityFunction.apply(context));

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
    this.generateButton(context, SORT_KEY, SortInventoryButton<H, S>::new);
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> void generateStackButton(
      ButtonContext<H, S> context
  ) {
    this.generateButton(context, STACK_KEY, AutoStackButton<H, S>::new);
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> void generateTransferButton(
      ButtonContext<H, S> context
  ) {
    this.generateButton(context, TRANSFER_KEY, TransferAllButton<H, S>::new);
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>, T extends ButtonBase<H, S>> void generateButton(
      ButtonContext<H, S> context, String buttonKey, ButtonConstructor<H, S, T> constructor
  ) {
    if (!context.shouldShowButton(buttonKey)) {
      return;
    }

    Coords offset = posToCoords(this.getButtonOffset(context));
    PositioningFunction<H, S> positioningFunction = this.getPositioningFunction(context);
    T button = constructor.create(offset, positioningFunction, context);
    Screens.getButtons(context.getScreen()).add(button);
    (context.isPlayerInventory() ? this.playerButtons : this.containerButtons).add(button);
  }

  private Position getButtonOffset(ButtonContext<?, ?> context) {
    Position offset = InventoryManagementConfig.getInstance().perScreenConfigs.getPosition(context.getScreen(),
        context.isPlayerInventory()
    );
    if (offset == null) {
      offset = InventoryManagementConfig.getInstance().defaultPosition.getValue();
    }

    int index = (context.isPlayerInventory() ? this.playerButtons : this.containerButtons).size();
    return new Position(offset.x(), offset.y() + BUTTON_SHIFT_Y * (ButtonBase.HEIGHT + BUTTON_SPACING) * index);
  }

  private <H extends ScreenHandler, S extends HandledScreen<H>> PositioningFunction<H, S> getPositioningFunction(
      ButtonContext<H, S> context
  ) {
    return ButtonRegistry.getInstance()
        .getPositioningFunction(context)
        .orElseGet(PositioningFunction::refSlotYAndBgRight);
  }

  private static Coords posToCoords(Position position) {
    return new Coords(position.x(), position.y());
  }

  @FunctionalInterface
  private interface ButtonConstructor<H extends ScreenHandler, S extends HandledScreen<H>, T extends ButtonBase<H, S>> {
    T create(Coords offset, PositioningFunction<H, S> positioningFunction, ButtonContext<H, S> context);
  }
}
