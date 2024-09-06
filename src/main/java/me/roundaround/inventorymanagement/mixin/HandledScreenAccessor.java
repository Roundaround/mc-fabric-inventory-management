package me.roundaround.inventorymanagement.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
  @Accessor
  int getX();

  @Accessor
  int getY();

  @Accessor
  int getBackgroundWidth();

  @Accessor
  int getBackgroundHeight();

  @Accessor
  int getTitleX();

  @Accessor
  int getTitleY();

  @Accessor
  int getPlayerInventoryTitleX();

  @Accessor
  int getPlayerInventoryTitleY();

  @Accessor
  Text getPlayerInventoryTitle();
}
