package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.InventoryManagementMod;
import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.PositioningFunction;
import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.widget.button.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.ButtonBase;
import me.roundaround.inventorymanagement.client.gui.widget.button.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.TransferAllButton;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class DefaultPositionEditScreen extends PositionEditScreen implements HandledScreenAccessor {
  private static final Identifier BACKGROUND_TEXTURE =
      new Identifier("textures/gui/container/generic_54.png");
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 114 + 3 * 18;

  private final LinkedList<ButtonBase<?, ?>> containerButtons = new LinkedList<>();
  private final LinkedList<ButtonBase<?, ?>> playerButtons = new LinkedList<>();

  private DefaultPositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.translatable("inventorymanagement.default_position_edit.title"),
        parent,
        configOption);

    this.workingCopy.subscribeToValueChanges(this.hashCode(), (oldValue, newValue) -> {
      for (int i = 0; i < containerButtons.size(); i++) {
        containerButtons.get(i)
            .setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, newValue));
      }
      for (int i = 0; i < playerButtons.size(); i++) {
        playerButtons.get(i)
            .setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, newValue));
      }
    });
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
    Inventory playerInventory = new SimpleInventory(27);
    Slot playerSlot = new Slot(playerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        BACKGROUND_HEIGHT - 94 + textRenderer.fontHeight + 2);

    int index = 0;
    ButtonContext<ScreenHandler, HandledScreen<ScreenHandler>> containerContext =
        new ButtonContext<>(null,
            this,
            null,
            containerSlot,
            false,
            playerInventory,
            containerInventory);

    if (InventoryManagementMod.CONFIG.SHOW_SORT.getValue()) {
      containerButtons.add(new SortInventoryButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(
          index++,
          offset), PositioningFunction.getDefault(), containerContext));
    }
    if (InventoryManagementMod.CONFIG.SHOW_STACK.getValue()) {
      containerButtons.add(new AutoStackButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(
          index++,
          offset), PositioningFunction.getDefault(), containerContext));
    }
    if (InventoryManagementMod.CONFIG.SHOW_TRANSFER.getValue()) {
      containerButtons.add(new TransferAllButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(
          index,
          offset), PositioningFunction.getDefault(), containerContext));
    }

    index = 0;
    ButtonContext<ScreenHandler, HandledScreen<ScreenHandler>> playerContext = new ButtonContext<>(
        null,
        this,
        null,
        playerSlot,
        true,
        playerInventory,
        containerInventory);

    if (InventoryManagementMod.CONFIG.SHOW_SORT.getValue()) {
      playerButtons.add(new SortInventoryButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(
          index++,
          offset), PositioningFunction.getDefault(), playerContext));
    }
    if (InventoryManagementMod.CONFIG.SHOW_STACK.getValue()) {
      playerButtons.add(new AutoStackButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(index++,
          offset), PositioningFunction.getDefault(), playerContext));
    }
    if (InventoryManagementMod.CONFIG.SHOW_TRANSFER.getValue()) {
      playerButtons.add(new TransferAllButton<>(InventoryButtonsManager.INSTANCE.getButtonOffset(
          index,
          offset), PositioningFunction.getDefault(), playerContext));
    }
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < containerButtons.size(); i++) {
      containerButtons.get(i)
          .setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, getValue()));
    }
    for (int i = 0; i < playerButtons.size(); i++) {
      playerButtons.get(i)
          .setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, getValue()));
    }
  }

  @Override
  protected void renderBackground(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    renderTextureBackground(drawContext, mouseX, mouseY, partialTicks);

    int x = (this.width - BACKGROUND_WIDTH) / 2;
    int y = (this.height - BACKGROUND_HEIGHT) / 2;
    drawContext.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, BACKGROUND_WIDTH, 3 * 18 + 17);
    drawContext.drawTexture(BACKGROUND_TEXTURE, x, y + 3 * 18 + 17, 0, 126, BACKGROUND_WIDTH, 96);
    drawContext.drawText(textRenderer,
        Text.translatable("container.chest"),
        x + 8,
        y + 6,
        0x404040,
        false);
    drawContext.drawText(textRenderer,
        Text.translatable("container.inventory"),
        x + 8,
        y + BACKGROUND_HEIGHT - 94,
        0x404040,
        false);

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
