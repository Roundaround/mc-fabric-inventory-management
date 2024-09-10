package me.roundaround.inventorymanagement.config.value;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;

public class ScreenOverrides {
  private PosRefType posRefType = PosRefType.AUTO;
  private BackgroundPosRefType backgroundPosRefType = null;
  private ScreenPosRefType screenPosRefType = null;
  private Position offset = new Position(0, 0);
  private int spacing = GuiUtil.PADDING;
  private ButtonVisibility sortVisibility = ButtonVisibility.DEFAULT;
  private ButtonVisibility stackVisibility = ButtonVisibility.DEFAULT;
  private ButtonVisibility transferVisibility = ButtonVisibility.DEFAULT;
}
