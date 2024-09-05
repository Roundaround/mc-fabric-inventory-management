package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.api.ButtonContext;
import me.roundaround.inventorymanagement.api.positioning.PositioningFunction;
import me.roundaround.inventorymanagement.client.ButtonManager;
import me.roundaround.inventorymanagement.client.gui.widget.button.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.ButtonBase;
import me.roundaround.inventorymanagement.client.gui.widget.button.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.widget.button.TransferAllButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class DefaultPositionEditScreen extends PositionEditScreen implements HandledScreenAccessor {
  private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/generic_54.png");
  private static final int BACKGROUND_WIDTH = 176;
  private static final int BACKGROUND_HEIGHT = 114 + 3 * 18;

  private final LinkedList<ButtonBase<?, ?>> containerButtons = new LinkedList<>();
  private final LinkedList<ButtonBase<?, ?>> playerButtons = new LinkedList<>();

  private DefaultPositionEditScreen(ConfigScreen parent, PositionConfigOption configOption) {
    super(Text.translatable("inventorymanagement.defaultPositionEdit.title"), parent, configOption);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return DefaultPositionEditScreen::new;
  }

  @Override
  public void initElements() {
    this.containerButtons.clear();
    this.playerButtons.clear();

    Position offset = InventoryManagementConfig.getInstance().defaultPosition.getValue();

    Inventory containerInventory = new SimpleInventory(27);
    Slot containerSlot = new Slot(
        containerInventory, 8, BACKGROUND_WIDTH - 16 - 4, 6 + this.textRenderer.fontHeight + 3);
    Inventory playerInventory = new SimpleInventory(27);
    Slot playerSlot = new Slot(playerInventory, 8, BACKGROUND_WIDTH - 16 - 4,
        BACKGROUND_HEIGHT - 94 + this.textRenderer.fontHeight + 2
    );

    int index = 0;
    ButtonContext<ScreenHandler, HandledScreen<ScreenHandler>> containerContext = new ButtonContext<>(null, this, null,
        containerSlot, false, playerInventory, containerInventory
    );

    if (InventoryManagementConfig.getInstance().showSort.getValue()) {
      this.containerButtons.add(new SortInventoryButton<>(ButtonManager.getInstance().getButtonOffset(index++, offset),
          PositioningFunction.refSlotYAndBgRight(), containerContext
      ));
    }
    if (InventoryManagementConfig.getInstance().showStack.getValue()) {
      this.containerButtons.add(new AutoStackButton<>(ButtonManager.getInstance().getButtonOffset(index++, offset),
          PositioningFunction.refSlotYAndBgRight(), containerContext
      ));
    }
    if (InventoryManagementConfig.getInstance().showTransfer.getValue()) {
      this.containerButtons.add(new TransferAllButton<>(ButtonManager.getInstance().getButtonOffset(index, offset),
          PositioningFunction.refSlotYAndBgRight(), containerContext
      ));
    }

    index = 0;
    ButtonContext<ScreenHandler, HandledScreen<ScreenHandler>> playerContext = new ButtonContext<>(null, this, null,
        playerSlot, true, playerInventory, containerInventory
    );

    if (InventoryManagementConfig.getInstance().showSort.getValue()) {
      this.playerButtons.add(new SortInventoryButton<>(ButtonManager.getInstance().getButtonOffset(index++, offset),
          PositioningFunction.refSlotYAndBgRight(), playerContext
      ));
    }
    if (InventoryManagementConfig.getInstance().showStack.getValue()) {
      this.playerButtons.add(new AutoStackButton<>(ButtonManager.getInstance().getButtonOffset(index++, offset),
          PositioningFunction.refSlotYAndBgRight(), playerContext
      ));
    }
    if (InventoryManagementConfig.getInstance().showTransfer.getValue()) {
      this.playerButtons.add(new TransferAllButton<>(ButtonManager.getInstance().getButtonOffset(index, offset),
          PositioningFunction.refSlotYAndBgRight(), playerContext
      ));
    }

    this.containerButtons.forEach(this::addDrawable);
    this.playerButtons.forEach(this::addDrawable);

    this.subscriptions.add(this.getOption().pendingValue.subscribe((pendingValue) -> {
      for (int i = 0; i < this.containerButtons.size(); i++) {
        this.containerButtons.get(i).setOffset(ButtonManager.getInstance().getButtonOffset(i, pendingValue));
      }
      for (int i = 0; i < this.playerButtons.size(); i++) {
        this.playerButtons.get(i).setOffset(ButtonManager.getInstance().getButtonOffset(i, pendingValue));
      }
    }));

    super.initElements();
  }

  @Override
  protected void setValue(Position value) {
    super.setValue(value);

    for (int i = 0; i < this.containerButtons.size(); i++) {
      this.containerButtons.get(i).setOffset(ButtonManager.getInstance().getButtonOffset(i, getValue()));
    }
    for (int i = 0; i < this.playerButtons.size(); i++) {
      this.playerButtons.get(i).setOffset(ButtonManager.getInstance().getButtonOffset(i, getValue()));
    }
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderBackground(context, mouseX, mouseY, delta);

    int x = (this.width - BACKGROUND_WIDTH) / 2;
    int y = (this.height - BACKGROUND_HEIGHT) / 2;
    context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, BACKGROUND_WIDTH, 3 * 18 + 17);
    context.drawTexture(BACKGROUND_TEXTURE, x, y + 3 * 18 + 17, 0, 126, BACKGROUND_WIDTH, 96);
    context.drawText(this.textRenderer, Text.translatable("container.chest"), x + 8, y + 6, 0x404040, false);
    context.drawText(this.textRenderer, Text.translatable("container.inventory"), x + 8, y + BACKGROUND_HEIGHT - 94,
        0x404040, false
    );
  }

  @Override
  public void render(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks
  ) {
    super.render(drawContext, mouseX, mouseY, partialTicks);

    drawContext.drawTextWithShadow(this.textRenderer, Text.literal(getValue().toString()), 4, 4, GuiUtil.LABEL_COLOR);
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
