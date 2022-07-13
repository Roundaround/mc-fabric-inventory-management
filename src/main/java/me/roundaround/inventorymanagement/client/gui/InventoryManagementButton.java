package me.roundaround.inventorymanagement.client.gui;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class InventoryManagementButton extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private static final Identifier TEXTURE = new Identifier(InventoryManagementMod.MOD_ID, "textures/gui.png");

  private final HandledScreenAccessor parent;
  private final Slot referenceSlot;
  private final Position iconOffset;

  private Position offset;

  public InventoryManagementButton(
      HandledScreen<?> parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      Position iconOffset,
      boolean isPlayerInventory,
      PressAction onPress) {
    super(
        ((HandledScreenAccessor) parent).getX()
            + ((HandledScreenAccessor) parent).getBackgroundWidth()
            + offset.x(),
        ((HandledScreenAccessor) parent).getY()
            + referenceSlot.y
            + offset.y(),
        WIDTH,
        HEIGHT,
        Text.literal(""),
        (button) -> {
          if (!Screen.hasControlDown()) {
            onPress.onPress(button);
            return;
          }

          GuiUtil.setScreen(new PerScreenPositionEditScreen(parent, isPlayerInventory));
        });

    this.parent = (HandledScreenAccessor) parent;
    this.referenceSlot = referenceSlot;
    this.offset = offset;
    this.iconOffset = iconOffset;
  }

  public InventoryManagementButton(
      HandledScreenAccessor parent,
      Inventory inventory,
      Slot referenceSlot,
      Position offset,
      Position iconOffset,
      boolean isPlayerInventory,
      PressAction onPress) {
    super(
        parent.getX() + parent.getBackgroundWidth() + offset.x(),
        parent.getY() + referenceSlot.y + offset.y(),
        WIDTH,
        HEIGHT,
        Text.literal(""),
        (button) -> {
        });

    this.parent = parent;
    this.referenceSlot = referenceSlot;
    this.offset = offset;
    this.iconOffset = iconOffset;
  }

  protected Text getTooltip() {
    return Text.literal("");
  }

  public void setOffset(Position position) {
    offset = position;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    x = parent.getX() + parent.getBackgroundWidth() + offset.x();
    y = parent.getY() + referenceSlot.y + offset.y();

    super.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, TEXTURE);
    RenderSystem.applyModelViewMatrix();
    RenderSystem.enableDepthTest();

    int u = iconOffset.x() * width;
    int v = iconOffset.y() * height
        + (isHovered() || isFocused() ? height : 0)
        + (InventoryButtonsManager.INSTANCE.usingDarkMode() ? height * 2 : 0);

    drawTexture(matrices, x, y, u, v, WIDTH, HEIGHT);

    if (hovered) {
      renderTooltip(matrices, mouseX, mouseY);
    }
  }

  @Override
  public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    Text tooltip = getTooltip();
    if (tooltip == null) {
      return;
    }

    if (!(parent instanceof Screen)) {
      return;
    }
    matrices.push();
    matrices.translate(0, 0, 40);
    ((Screen) parent).renderTooltip(matrices, List.of(tooltip), Optional.empty(), mouseX, mouseY);
    matrices.pop();
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    appendDefaultNarrations(builder);
    Text tooltip = getTooltip();
    if (tooltip == null) {
      return;
    }
    builder.put(NarrationPart.HINT, tooltip);
  }
}
