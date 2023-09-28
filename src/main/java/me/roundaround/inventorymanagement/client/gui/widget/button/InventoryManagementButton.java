package me.roundaround.inventorymanagement.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.ButtonBasePositionFunction;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class InventoryManagementButton<T extends HandledScreen<?>> extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private static final Identifier TEXTURE =
      new Identifier(InventoryManagementMod.MOD_ID, "textures/gui.png");

  private final T parent;
  private final HandledScreenAccessor accessor;
  private final ButtonBasePositionFunction<T> basePositionFunction;
  private final Position iconOffset;
  private final boolean isPlayerInventory;

  private Position offset;

  protected InventoryManagementButton(
      T parent,
      ButtonBasePositionFunction<T> basePositionFunction,
      Position offset,
      Position iconOffset,
      boolean isPlayerInventory,
      PressAction onPress,
      Text tooltip) {
    super(
        basePositionFunction.apply(parent, (HandledScreenAccessor) parent, isPlayerInventory).x() +
            offset.x(),
        basePositionFunction.apply(parent, (HandledScreenAccessor) parent, isPlayerInventory).y() +
            offset.y(),
        WIDTH,
        HEIGHT,
        Text.literal(""),
        (button) -> {
          if (!Screen.hasControlDown()) {
            onPress.onPress(button);
            return;
          }

          GuiUtil.setScreen(new PerScreenPositionEditScreen(parent, parent, isPlayerInventory));
        },
        DEFAULT_NARRATION_SUPPLIER);

    this.parent = parent;
    this.accessor = (HandledScreenAccessor) parent;
    this.basePositionFunction = basePositionFunction;
    this.offset = offset;
    this.iconOffset = iconOffset;
    this.isPlayerInventory = isPlayerInventory;

    setTooltip(Tooltip.of(tooltip));
  }

  protected InventoryManagementButton(
      HandledScreenAccessor accessor,
      Slot referenceSlot,
      Position offset,
      Position iconOffset,
      boolean isPlayerInventory,
      Text tooltip) {
    super(ButtonBasePositionFunction.forReferenceSlot(referenceSlot)
            .apply(null, accessor, isPlayerInventory)
            .x() + offset.x(),
        ButtonBasePositionFunction.forReferenceSlot(referenceSlot)
            .apply(null, accessor, isPlayerInventory)
            .y() + offset.y(),
        WIDTH,
        HEIGHT,
        Text.literal(""),
        (button) -> {
        },
        DEFAULT_NARRATION_SUPPLIER);

    this.parent = null;
    this.accessor = accessor;
    this.basePositionFunction = ButtonBasePositionFunction.forReferenceSlot(referenceSlot);
    this.offset = offset;
    this.iconOffset = iconOffset;
    this.isPlayerInventory = isPlayerInventory;

    setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    offset = position;
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    Position position = basePositionFunction.apply(parent, accessor, this.isPlayerInventory);
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
