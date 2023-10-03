package me.roundaround.inventorymanagement.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.TransferAllButton;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class DefaultPositionEditScreen extends PositionEditScreen implements HandledScreenAccessor {
  private static final Identifier BACKGROUND_TEXTURE =
      new Identifier("textures/gui/container/generic_54.png");
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 114 + 3 * 18;

  private final LinkedList<InventoryManagementButton> containerButtons = new LinkedList<>();
  private final LinkedList<InventoryManagementButton> playerButtons = new LinkedList<>();

  private DefaultPositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.translatable("inventorymanagement.default_position_edit.title"),
        parent,
        configOption);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return DefaultPositionEditScreen::new;
  }

  @Override
  public void init() {
    super.init();

    containerButtons.clear();
    playerButtons.clear();

    Position offset = InventoryManagementMod.CONFIG.DEFAULT_POSITION.getValue();

    Inventory containerInventory = new SimpleInventory(27);
    Slot containerSlot =
        new Slot(containerInventory, 8, BACKGROUND_WIDTH - 16 - 4, 6 + textRenderer.fontHeight + 3);
    int index = 0;

    if (InventoryManagementMod.CONFIG.SHOW_SORT.getValue()) {
      containerButtons.add(new SortInventoryButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }
    if (InventoryManagementMod.CONFIG.SHOW_STACK.getValue()) {
      containerButtons.add(new AutoStackButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }
    if (InventoryManagementMod.CONFIG.SHOW_TRANSFER.getValue()) {
      containerButtons.add(new TransferAllButton(this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }

    Inventory playerInventory = new SimpleInventory(27);
    Slot playerSlot = new Slot(playerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        BACKGROUND_HEIGHT - 94 + textRenderer.fontHeight + 2);
    index = 0;

    if (InventoryManagementMod.CONFIG.SHOW_SORT.getValue()) {
      playerButtons.add(new SortInventoryButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }
    if (InventoryManagementMod.CONFIG.SHOW_STACK.getValue()) {
      playerButtons.add(new AutoStackButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }
    if (InventoryManagementMod.CONFIG.SHOW_TRANSFER.getValue()) {
      playerButtons.add(new TransferAllButton(this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true));
    }
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < containerButtons.size(); i++) {
      containerButtons.get(i)
          .setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
    for (int i = 0; i < playerButtons.size(); i++) {
      playerButtons.get(i)
          .setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, getValue()));
    }
  }

  @Override
  public void renderBackground(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    renderTextureBackground(drawContext, mouseX, mouseY, partialTicks);

    // TODO: Clean this up
    MatrixStack matrixStack = drawContext.getMatrices();
    matrixStack.push();
    matrixStack.translate(getX(), getY(), -51);
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    drawContext.drawTexture(BACKGROUND_TEXTURE, 0, 0, 0, 0, BACKGROUND_WIDTH, 3 * 18 + 17);
    drawContext.drawTexture(BACKGROUND_TEXTURE, 0, 3 * 18 + 17, 0, 126, BACKGROUND_WIDTH, 96);
    drawContext.drawText(textRenderer, Text.translatable("container.chest"), 8, 6, 0x404040, false);
    drawContext.drawText(textRenderer,
        Text.translatable("container.inventory"),
        8,
        BACKGROUND_HEIGHT - 94,
        0x404040,
        false);
    matrixStack.pop();

    renderDarkenBackground(drawContext, mouseX, mouseY, partialTicks);
  }

  @Override
  protected void renderContent(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(drawContext, mouseX, mouseY, partialTicks);

    containerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));
    playerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));

    drawContext.drawTextWithShadow(textRenderer,
        Text.literal(getValue().toString()),
        4,
        4,
        GuiUtil.LABEL_COLOR);
  }

  public int getX() {
    return (width - BACKGROUND_WIDTH) / 2;
  }

  public int getY() {
    return (height - BACKGROUND_HEIGHT) / 2;
  }

  public int getBackgroundWidth() {
    return BACKGROUND_WIDTH;
  }
}
