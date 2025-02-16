package me.roundaround.inventorymanagement.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.TransferAllButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class DefaultPositionEditScreen extends PositionEditScreen implements HandledScreenAccessor {
  private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/generic_54.png");
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 114 + 3 * 18;

  private final LinkedList<InventoryManagementButton> containerButtons = new LinkedList<>();
  private final LinkedList<InventoryManagementButton> playerButtons = new LinkedList<>();

  private DefaultPositionEditScreen(ConfigScreen parent, PositionConfigOption configOption) {
    super(Text.translatable("inventorymanagement.default_position_edit.title"), parent, configOption);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return DefaultPositionEditScreen::new;
  }

  @Override
  public void init() {
    super.init();

    this.containerButtons.clear();
    this.playerButtons.clear();

    InventoryManagementConfig config = InventoryManagementConfig.getInstance();

    Position offset = config.defaultPosition.getValue();

    Inventory containerInventory = new SimpleInventory(27);
    Slot containerSlot = new Slot(containerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        6 + this.textRenderer.fontHeight + 3
    );
    int index = 0;

    if (config.showSort.getValue()) {
      this.containerButtons.add(new SortInventoryButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showStack.getValue()) {
      this.containerButtons.add(new AutoStackButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showTransfer.getValue()) {
      this.containerButtons.add(new TransferAllButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }

    Inventory playerInventory = new SimpleInventory(27);
    Slot playerSlot = new Slot(playerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        BACKGROUND_HEIGHT - 94 + this.textRenderer.fontHeight + 2
    );
    index = 0;

    if (config.showSort.getValue()) {
      this.playerButtons.add(new SortInventoryButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showStack.getValue()) {
      this.playerButtons.add(new AutoStackButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showTransfer.getValue()) {
      this.playerButtons.add(new TransferAllButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < this.containerButtons.size(); i++) {
      this.containerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, this.getValue()));
    }
    for (int i = 0; i < this.playerButtons.size(); i++) {
      this.playerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, this.getValue()));
    }
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.render(drawContext, mouseX, mouseY, partialTicks);

    this.containerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));
    this.playerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));

    drawContext.drawTextWithShadow(this.textRenderer,
        Text.literal(this.getValue().toString()),
        GuiUtil.PADDING,
        GuiUtil.PADDING,
        GuiUtil.LABEL_COLOR
    );
  }

  @Override
  public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    // TODO: Clean this up
    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(this.getX(), this.getY(), -51);
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    drawContext.drawTexture(BACKGROUND_TEXTURE, 0, 0, 0, 0, BACKGROUND_WIDTH, 3 * 18 + 17);
    drawContext.drawTexture(BACKGROUND_TEXTURE, 0, 3 * 18 + 17, 0, 126, BACKGROUND_WIDTH, 96);
    drawContext.drawText(this.textRenderer, Text.translatable("container.chest"), 8, 6, 0x404040, false);
    drawContext.drawText(this.textRenderer,
        Text.translatable("container.inventory"),
        8,
        BACKGROUND_HEIGHT - 94,
        0x404040,
        false
    );
    matrixStack.pop();

    super.renderBackground(drawContext, mouseX, mouseY, partialTicks);
  }

  public int getX() {
    return (this.width - BACKGROUND_WIDTH) / 2;
  }

  public int getY() {
    return (this.height - BACKGROUND_HEIGHT) / 2;
  }

  public int getBackgroundWidth() {
    return BACKGROUND_WIDTH;
  }
}
