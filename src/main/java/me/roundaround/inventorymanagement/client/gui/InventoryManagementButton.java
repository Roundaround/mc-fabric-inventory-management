package me.roundaround.inventorymanagement.client.gui;

import java.util.List;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public abstract class InventoryManagementButton extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;
  
  public InventoryManagementButton(HandledScreen<?> parent, int x, int y, int iconOffsetX, int iconOffsetY, PressAction onPress) {
    // TODO: Add message back in
    super(x, y, WIDTH, HEIGHT, new LiteralText(""), onPress);
    // super(parent.getGuiLeft() + parent.getXSize() + x, parent.getGuiTop() + y, WIDTH, HEIGHT, new LiteralText(""), onPress);
  }

  protected List<Text> getTooltip() {
    return List.of();
  }
}
