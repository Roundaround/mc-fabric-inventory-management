package me.roundaround.inventorymanagement.client.gui.screen;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface ScreenAccessor {
  int getX();

  int getY();

  int getBackgroundWidth();

  void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, int x, int y);
}
