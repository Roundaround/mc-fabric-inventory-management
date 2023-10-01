package me.roundaround.inventorymanagement.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonBase<H extends ScreenHandler, S extends HandledScreen<H>>
    extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;
  public static final Identifier TEXTURE =
      new Identifier(InventoryManagementMod.MOD_ID, "textures/gui.png");

  protected Position offset;
  protected Position iconOffset;
  protected PositioningFunction<H, S> positioningFunction;
  protected ButtonContext<H, S> context;

  protected ButtonBase(
      Position initialPosition,
      Position offset,
      Position iconOffset,
      PositioningFunction<H, S> positioningFunction,
      ButtonContext<H, S> context,
      PressAction onPress,
      Text tooltip) {
    super(initialPosition.x() + offset.x(),
        initialPosition.y() + offset.y(),
        WIDTH,
        HEIGHT,
        Text.literal(""),
        (button) -> {
          if (!Screen.hasControlDown()) {
            onPress.onPress(button);
            return;
          }

          GuiUtil.setScreen(new PerScreenPositionEditScreen(context.getParentScreen(),
              context.getParentScreen(),
              context.isPlayerInventory()));
        },
        DEFAULT_NARRATION_SUPPLIER);

    this.offset = offset;
    this.iconOffset = iconOffset;
    this.positioningFunction = positioningFunction;
    this.context = context;

    setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    offset = position;
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    Position position = positioningFunction.apply(context);
    setX(position.x() + offset.x());
    setY(position.y() + offset.y());

    super.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
        GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.applyModelViewMatrix();
    RenderSystem.enableDepthTest();

    int u = iconOffset.x() * width;
    int v = iconOffset.y() * height + (isHovered() || isFocused() ? height : 0);

    drawContext.drawTexture(TEXTURE, getX(), getY(), u, v, WIDTH, HEIGHT);
  }
}
