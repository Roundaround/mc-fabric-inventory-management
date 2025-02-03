package me.roundaround.inventorymanagement.config.value;

import me.roundaround.inventorymanagement.roundalib.client.gui.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;

public class ScreenOverrides {
  private PosRefType posRefType = PosRefType.AUTO;
  private Integer manualRefSlot = null;
  private BackgroundPosRefType backgroundPosRefType = null;
  private ScreenPosRefType screenPosRefType = null;
  private Position offset = new Position(0, 0);
  private int spacing = GuiUtil.PADDING;
  private ButtonVisibility sortVisibility = ButtonVisibility.DEFAULT;
  private ButtonVisibility stackVisibility = ButtonVisibility.DEFAULT;
  private ButtonVisibility transferVisibility = ButtonVisibility.DEFAULT;

  public PosRefType getPosRefType() {
    return this.posRefType;
  }

  public boolean isAutoPosRef() {
    return this.posRefType == PosRefType.AUTO;
  }

  public boolean isManualSlotPosRef() {
    return this.posRefType == PosRefType.MANUAL_SLOT;
  }

  public boolean isBackgroundPosRef() {
    return this.posRefType == PosRefType.BACKGROUND;
  }

  public boolean isScreenPosRef() {
    return this.posRefType == PosRefType.SCREEN;
  }

  public Integer getManualRefSlot() {
    return this.manualRefSlot;
  }

  public BackgroundPosRefType getBackgroundPosRefType() {
    return this.backgroundPosRefType;
  }

  public ScreenPosRefType getScreenPosRefType() {
    return this.screenPosRefType;
  }

  public Position getOffset() {
    return this.offset;
  }

  public int getSpacing() {
    return this.spacing;
  }

  public ButtonVisibility getSortVisibility() {
    return this.sortVisibility;
  }

  public ButtonVisibility getStackVisibility() {
    return this.stackVisibility;
  }

  public ButtonVisibility getTransferVisibility() {
    return this.transferVisibility;
  }

  public void setAutoPosRef() {
    this.posRefType = PosRefType.AUTO;
    this.manualRefSlot = null;
    this.backgroundPosRefType = null;
    this.screenPosRefType = null;
  }

  public void setManualSlotPosRef(int slotIndex) {
    this.posRefType = PosRefType.MANUAL_SLOT;
    this.manualRefSlot = slotIndex;
    this.backgroundPosRefType = null;
    this.screenPosRefType = null;
  }

  public void setBackgroundPosRef(BackgroundPosRefType backgroundPosRefType) {
    this.posRefType = PosRefType.BACKGROUND;
    this.backgroundPosRefType = backgroundPosRefType;
    this.manualRefSlot = null;
    this.screenPosRefType = null;
  }

  public void setScreenPosRef(ScreenPosRefType screenPosRefType) {
    this.posRefType = PosRefType.SCREEN;
    this.screenPosRefType = screenPosRefType;
    this.manualRefSlot = null;
    this.backgroundPosRefType = null;
  }

  public void setOffset(Position offset) {
    this.offset = offset;
  }

  public void setSpacing(int spacing) {
    this.spacing = spacing;
  }

  public void setSortVisibility(ButtonVisibility sortVisibility) {
    this.sortVisibility = sortVisibility;
  }

  public void setStackVisibility(ButtonVisibility stackVisibility) {
    this.stackVisibility = stackVisibility;
  }

  public void setTransferVisibility(ButtonVisibility transferVisibility) {
    this.transferVisibility = transferVisibility;
  }
}
