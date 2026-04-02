package me.roundaround.inventorymanagement.client.gui;

import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.client.gui.screen.ScreenPositioner;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.mixin.ScreenAccessor;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public abstract class InventoryManagementButton extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private final ScreenPositioner positioner;
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
      net.minecraft.text.Text tooltip,
      ButtonTextures textures
  ) {
    super(
        ((HandledScreenAccessor) parent).getX() + ((HandledScreenAccessor) parent).getBackgroundWidth() + offset.x(),
        ((HandledScreenAccessor) parent).getY() + referenceSlot.y + offset.y(),
        WIDTH,
        HEIGHT,
        ScreenTexts.EMPTY,
        (button) -> {
          if (!((ScreenAccessor) parent).getClient().isCtrlPressed()) {
            onPress.onPress(button);
            return;
          }

          GuiUtil.setScreen(new PerScreenPositionEditScreen(parent, isPlayerInventory));
        },
        DEFAULT_NARRATION_SUPPLIER
    );

    this.positioner = new ScreenPositioner.HandledScreenWrapper(parent);
    this.referenceSlot = referenceSlot;
    this.textures = textures;
    this.offset = offset;

    this.setTooltip(Tooltip.of(tooltip));
  }

  protected InventoryManagementButton(
      ScreenPositioner parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      boolean isPlayerInventory,
      PressAction onPress,
      net.minecraft.text.Text tooltip,
      ButtonTextures textures
  ) {
    super(
        parent.getX() + parent.getBackgroundWidth() + offset.x(),
        parent.getY() + referenceSlot.y + offset.y(),
        WIDTH,
        HEIGHT,
        ScreenTexts.EMPTY,
        (button) -> {
        },
        DEFAULT_NARRATION_SUPPLIER
    );

    this.positioner = parent;
    this.referenceSlot = referenceSlot;
    this.textures = textures;
    this.offset = offset;

    this.setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    this.offset = position;
  }

  @Override
  public void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
    this.setX(this.positioner.getX() + this.positioner.getBackgroundWidth() + this.offset.x());
    this.setY(this.positioner.getY() + this.referenceSlot.y + this.offset.y());

    Identifier texture = this.textures.get(this.isInteractable(), this.isSelected());
    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), this.width, this.height);
  }

  public void clearSelected() {
    this.hovered = false;
    this.setFocused(false);
  }
}
