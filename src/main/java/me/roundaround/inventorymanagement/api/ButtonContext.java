package me.roundaround.inventorymanagement.api;

import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ButtonContext<H extends ScreenHandler, S extends HandledScreen<H>> {
  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

  private final S parentScreen;
  private final HandledScreenAccessor accessor;
  private final boolean isPlayerInventory;
  private final H screenHandler;
  private final Inventory playerInventory;
  private final Inventory containerInventory;

  private Slot referenceSlot;

  public ButtonContext(
      S parentScreen,
      HandledScreenAccessor accessor,
      H screenHandler,
      Slot referenceSlot,
      boolean isPlayerInventory,
      Inventory playerInventory,
      Inventory containerInventory
  ) {
    this.parentScreen = parentScreen;
    this.accessor = accessor;
    this.screenHandler = screenHandler;
    this.referenceSlot = referenceSlot;
    this.isPlayerInventory = isPlayerInventory;
    this.playerInventory = playerInventory;
    this.containerInventory = containerInventory;
  }

  public ButtonContext(
      S parentScreen, boolean isPlayerInventory
  ) {
    this.parentScreen = parentScreen;
    this.accessor = (HandledScreenAccessor) parentScreen;
    this.screenHandler = parentScreen.getScreenHandler();
    this.referenceSlot = PositioningFunction.getReferenceSlot(parentScreen, isPlayerInventory);
    this.isPlayerInventory = isPlayerInventory;

    ClientPlayerEntity player = MINECRAFT.player;
    if (player != null) {
      this.playerInventory = player.getInventory();
      this.containerInventory = InventoryHelper.getContainerInventory(player);
    } else {
      this.playerInventory = null;
      this.containerInventory = null;
    }
  }

  public boolean hasParentScreen() {
    return parentScreen != null;
  }

  public S getParentScreen() {
    return parentScreen;
  }

  public HandledScreenAccessor getAccessor() {
    return accessor;
  }

  public boolean isPlayerInventory() {
    return isPlayerInventory;
  }

  public boolean hasScreenHandler() {
    return screenHandler != null;
  }

  public H getScreenHandler() {
    return screenHandler;
  }

  public boolean hasReferenceSlot() {
    return referenceSlot != null;
  }

  public Slot getReferenceSlot() {
    return referenceSlot;
  }

  public boolean hasPlayerInventory() {
    return playerInventory != null;
  }

  public Inventory getPlayerInventory() {
    return playerInventory;
  }

  public boolean hasContainerInventory() {
    return containerInventory != null;
  }

  public Inventory getContainerInventory() {
    return containerInventory;
  }

  public boolean hasInventory() {
    return this.isPlayerInventory && this.hasPlayerInventory() ||
        !this.isPlayerInventory && this.hasContainerInventory();
  }

  public Inventory getInventory() {
    if (!this.hasInventory()) {
      return null;
    }
    return this.isPlayerInventory ? this.getPlayerInventory() : this.getContainerInventory();
  }

  public void setReferenceSlot(Slot referenceSlot) {
    this.referenceSlot = referenceSlot;
  }

  public Slot getDefaultReferenceSlot() {
    return PositioningFunction.getReferenceSlot(this.parentScreen, this.isPlayerInventory);
  }

  @SuppressWarnings("unchecked")
  public Class<H> getScreenHandlerClass() {
    if (!this.hasScreenHandler()) {
      return null;
    }
    return (Class<H>) this.getScreenHandler().getClass();
  }
}
