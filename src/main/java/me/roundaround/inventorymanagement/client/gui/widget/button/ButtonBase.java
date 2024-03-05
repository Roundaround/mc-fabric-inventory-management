package me.roundaround.inventorymanagement.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.client.InventoryManagementClientMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.client.gui.screen.PerScreenPositionEditScreen;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonBase<H extends ScreenHandler, S extends HandledScreen<H>>
    extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  protected Position offset;
  protected PositioningFunction<H, S> positioningFunction;
  protected ButtonContext<H, S> context;

  private final ButtonTextures textures;

  protected ButtonBase(
      Position initialPosition,
      Position offset,
      PositioningFunction<H, S> positioningFunction,
      ButtonContext<H, S> context,
      PressAction onPress,
      Text tooltip,
      ButtonTextures textures) {
    super(initialPosition.x() + offset.x(),
        initialPosition.y() + offset.y(),
        WIDTH,
        HEIGHT,
        ScreenTexts.EMPTY,
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
    this.positioningFunction = positioningFunction;
    this.textures = textures;
    this.context = context;

    setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    offset = position;
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    Position position = positioningFunction.apply(context);
    setX(position.x() + offset.x());
    setY(position.y() + offset.y());

    drawContext.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.enableDepthTest();

    Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected());
    Sprite sprite = InventoryManagementClientMod.getGuiAtlasManager().getSprite(identifier);
    drawContext.drawSprite(this.getX(), this.getY(), 0, this.width, this.height, sprite);
  }
}
