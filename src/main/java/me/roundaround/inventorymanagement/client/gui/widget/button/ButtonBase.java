package me.roundaround.inventorymanagement.client.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.roundalib.config.value.Position;
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

  protected Position offset;
  protected PositioningFunction<H, S> positioningFunction;
  protected ButtonContext<H, S> context;

  private final Identifier icon;

  protected ButtonBase(
      Position initialPosition,
      Position offset,
      PositioningFunction<H, S> positioningFunction,
      ButtonContext<H, S> context,
      PressAction onPress,
      Text tooltip,
      Identifier icon
  ) {
    super(initialPosition.x() + offset.x(), initialPosition.y() + offset.y(), WIDTH, HEIGHT, ScreenTexts.EMPTY, onPress,
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

    setTooltip(Tooltip.of(tooltip));
  }

  public void setOffset(Position position) {
    offset = position;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    Position position = positioningFunction.apply(this.context);
    setX(position.x() + offset.x());
    setY(position.y() + offset.y());

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.enableBlend();
    RenderSystem.enableDepthTest();

    Identifier background = BACKGROUNDS.get(this.isNarratable(), this.isSelected());
    context.drawGuiTexture(background, this.getX(), this.getY(), this.width, this.height);

    float color = this.active ? 1f : (160f / 255f);
    RenderSystem.setShaderColor(color, color, color, 1f);
    context.drawGuiTexture(this.icon, this.getX(), this.getY(), this.width, this.height);
  }
}
