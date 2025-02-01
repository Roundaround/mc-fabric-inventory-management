package me.roundaround.inventorymanagement.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.positioning.Coords;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonBase<H extends ScreenHandler, S extends HandledScreen<H>> extends ButtonWidget {
  public static final int WIDTH = 14;
  public static final int HEIGHT = 14;

  private static final ButtonTextures BACKGROUNDS = new ButtonTextures(
      new Identifier(InventoryManagementMod.MOD_ID, "widget/button"),
      new Identifier(InventoryManagementMod.MOD_ID, "widget/button_highlighted")
  );

  protected Coords offset;
  protected PositioningFunction<H, S> positioningFunction;
  protected ButtonContext<H, S> context;

  private final Identifier icon;

  protected ButtonBase(
      Coords initialPosition,
      Coords offset,
      PositioningFunction<H, S> positioningFunction,
      ButtonContext<H, S> context,
      PressAction onPress,
      Text tooltip,
      Identifier icon
  ) {
    super(getX(initialPosition, offset), getY(initialPosition, offset), WIDTH, HEIGHT, ScreenTexts.EMPTY, onPress,
        //        (button) -> {
        //          if (!Screen.hasControlDown()) {
        //            onPress.onPress(button);
        //            return;
        //          }
        //
        //          GuiUtil.setScreen(new PerScreenPositionEditScreen(context.getParentScreen(), context
        //          .getParentScreen(),
        //              context.isPlayerInventory()
        //          ));
        //        },
        DEFAULT_NARRATION_SUPPLIER
    );

    this.offset = offset;
    this.positioningFunction = positioningFunction;
    this.icon = icon;
    this.context = context;

    this.setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Coords offset) {
    this.offset = offset;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    Coords coords = this.positioningFunction.apply(this.context);
    this.active = coords != null;

    if (coords == null) {
      this.hovered = false;
      this.setFocused(false);
      return;
    }

    this.setX(getX(coords, this.offset));
    this.setY(getY(coords, this.offset));

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.enableDepthTest();

    Identifier background = BACKGROUNDS.get(this.isNarratable(), this.isSelected());
    context.drawGuiTexture(background, this.getX(), this.getY(), this.width, this.height);

    float color = this.active ? 1f : (160f / 255f);
    RenderSystem.setShaderColor(color, color, color, 1f);
    context.drawGuiTexture(this.icon, this.getX(), this.getY(), this.width, this.height);
  }

  private static int getX(Coords base, Coords offset) {
    int baseX = base == null ? 0 : base.x();
    int offsetX = offset == null ? 0 : offset.x();
    return baseX + offsetX;
  }

  private static int getY(Coords base, Coords offset) {
    int baseY = base == null ? 0 : base.y();
    int offsetY = offset == null ? 0 : offset.y();
    return baseY + offsetY;
  }
}
