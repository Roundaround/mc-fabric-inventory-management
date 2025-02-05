package me.roundaround.inventorymanagement.api.gui;

import me.roundaround.inventorymanagement.inventory.InventoryHelper;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.LinkedHashSet;
import java.util.Objects;

public class ButtonContext<H extends ScreenHandler, S extends HandledScreen<H>> {
  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

  private final S parentScreen;
  private final HandledScreenAccessor accessor;
  private final boolean isPlayerInventory;
  private final H screenHandler;
  private final Inventory playerInventory;
  private final Inventory containerInventory;
  private final LinkedHashSet<String> buttonsToShow = new LinkedHashSet<>();

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
    this.referenceSlot = InventoryHelper.getReferenceSlot(parentScreen, isPlayerInventory);
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
    return this.parentScreen != null;
  }

  public S getScreen() {
    return this.parentScreen;
  }

  public HandledScreenAccessor getScreenAccessor() {
    return this.accessor;
  }

  public boolean isPlayerInventory() {
    return this.isPlayerInventory;
  }

  public boolean hasScreenHandler() {
    return this.screenHandler != null;
  }

  public H getScreenHandler() {
    return this.screenHandler;
  }

  public boolean hasReferenceSlot() {
    return this.referenceSlot != null;
  }

  public Slot getReferenceSlot() {
    return this.referenceSlot;
  }

  public boolean hasPlayerInventory() {
    return this.playerInventory != null;
  }

  public Inventory getPlayerInventory() {
    return this.playerInventory;
  }

  public boolean hasContainerInventory() {
    return this.containerInventory != null;
  }

  public Inventory getContainerInventory() {
    return this.containerInventory;
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
    return InventoryHelper.getReferenceSlot(this.parentScreen, this.isPlayerInventory);
  }

  @SuppressWarnings("unchecked")
  public Class<H> getScreenHandlerClass() {
    if (!this.hasScreenHandler()) {
      return null;
    }
    return (Class<H>) this.getScreenHandler().getClass();
  }

  public void markButtonToShow(String button) {
    this.buttonsToShow.add(button);
  }

  public boolean shouldShowButton(String button) {
    return this.buttonsToShow.contains(button);
  }

  public int getButtonShownCount() {
    return this.buttonsToShow.size();
  }

  public int getButtonsTotalHeight(int height, int spacing) {
    int count = this.getButtonShownCount();
    if (count == 0) {
      return 0;
    }
    return count * (height + spacing) - spacing;
  }

  public int getButtonIndex(String button) {
    if (!this.buttonsToShow.contains(button)) {
      return -1;
    }

    int index = 0;
    for (String resource : this.buttonsToShow) {
      if (Objects.equals(resource, button)) {
        return index;
      }
      index++;
    }

    // Technically should never get here but whatever
    return -1;
  }
}
