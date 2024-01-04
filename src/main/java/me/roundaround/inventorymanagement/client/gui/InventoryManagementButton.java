package me.roundaround.inventorymanagement.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.client.InventoryManagementClientMod;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class InventoryManagementButton extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private final HandledScreenAccessor parent;
  private final Slot referenceSlot;

  private final ButtonTextures textures;

  private Position offset;

  protected InventoryManagementButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory,
      PressAction onPress,
      Text tooltip,
      ButtonTextures textures) {
    super(((HandledScreenAccessor) parent).getX() +
            ((HandledScreenAccessor) parent).getBackgroundWidth() + offset.x(),
        ((HandledScreenAccessor) parent).getY() + referenceSlot.y + offset.y(),
        WIDTH,
        HEIGHT,
        ScreenTexts.EMPTY,
        (button) -> {
          if (!Screen.hasControlDown()) {
            onPress.onPress(button);
            return;
          }

          GuiUtil.setScreen(new PerScreenPositionEditScreen(parent, isPlayerInventory));
        },
        DEFAULT_NARRATION_SUPPLIER);

    this.parent = (HandledScreenAccessor) parent;
    this.referenceSlot = referenceSlot;
    this.textures = textures;
    this.offset = offset;

    setTooltip(Tooltip.of(tooltip));
  }

  protected InventoryManagementButton(
      HandledScreenAccessor parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory,
      PressAction onPress,
      Text tooltip,
      ButtonTextures textures) {
    super(parent.getX() + parent.getBackgroundWidth() + offset.x(),
        parent.getY() + referenceSlot.y + offset.y(),
        WIDTH,
        HEIGHT,
        ScreenTexts.EMPTY,
        (button) -> {
        },
        DEFAULT_NARRATION_SUPPLIER);

    this.parent = parent;
    this.referenceSlot = referenceSlot;
    this.textures = textures;
    this.offset = offset;

    setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    this.offset = position;
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    setX(this.parent.getX() + this.parent.getBackgroundWidth() + this.offset.x());
    setY(this.parent.getY() + this.referenceSlot.y + this.offset.y());

    drawContext.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.enableDepthTest();

    Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected());
    Sprite sprite = InventoryManagementClientMod.getGuiAtlasManager().getSprite(identifier);
    drawContext.drawSprite(this.getX(), this.getY(), 0, this.width, this.height, sprite);
  }
}
