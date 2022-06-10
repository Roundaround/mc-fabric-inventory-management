package me.roundaround.inventorymanagement.client.gui;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class InventoryManagementButton extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private static final Identifier TEXTURE = new Identifier(InventoryManagementMod.MOD_ID, "textures/gui.png");

  private final HandledScreen<?> parent;
  private final HandledScreenAccessor parentAccessor;
  private final int initialX;
  private final int iconOffsetX;
  private final int iconOffsetY;

  public InventoryManagementButton(
      HandledScreen<?> parent,
      HandledScreenAccessor parentAccessor,
      int x,
      int y,
      int iconOffsetX,
      int iconOffsetY,
      PressAction onPress) {
    super(
        parentAccessor.getX() + parentAccessor.getBackgroundWidth() + x,
        parentAccessor.getY() + y,
        WIDTH,
        HEIGHT,
        Text.literal(""),
        onPress);

    this.parent = parent;
    this.parentAccessor = parentAccessor;
    this.initialX = x;
    this.iconOffsetX = iconOffsetX;
    this.iconOffsetY = iconOffsetY;
  }

  protected Text getTooltip() {
    return Text.literal("");
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    if (parent instanceof RecipeBookProvider) {
      x = parentAccessor.getX() + parentAccessor.getBackgroundWidth() + initialX;
    }

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

    int u = iconOffsetX * width;
    int v = iconOffsetY * height + (isHovered() || isFocused() ? height : 0);

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
    matrices.push();
    matrices.translate(0, 0, 40);
    parent.renderTooltip(matrices, List.of(tooltip), Optional.empty(), mouseX, mouseY);
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
