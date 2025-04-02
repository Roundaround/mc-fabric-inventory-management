package me.roundaround.inventorymanagement.client.gui.screen;

import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
import me.roundaround.inventorymanagement.client.gui.AutoStackButton;
import me.roundaround.inventorymanagement.client.gui.InventoryManagementButton;
import me.roundaround.inventorymanagement.client.gui.SortInventoryButton;
import me.roundaround.inventorymanagement.client.gui.TransferAllButton;
import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
import me.roundaround.inventorymanagement.mixin.HandledScreenAccessor;
import me.roundaround.inventorymanagement.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.inventorymanagement.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.inventorymanagement.roundalib.client.gui.util.GuiUtil;
import me.roundaround.inventorymanagement.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.inventorymanagement.roundalib.config.option.PositionConfigOption;
import me.roundaround.inventorymanagement.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class DefaultPositionEditScreen extends PositionEditScreen implements HandledScreenAccessor {
  private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
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

    Position offset = this.getOption().getPendingValue();

    Inventory containerInventory = new SimpleInventory(27);
    Slot containerSlot = new Slot(
        containerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        6 + this.textRenderer.fontHeight + 3
    );
    int index = 0;

    if (config.showSort.getValue()) {
      this.containerButtons.add(new SortInventoryButton(
          this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showStack.getValue()) {
      this.containerButtons.add(new AutoStackButton(
          this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showTransfer.getValue()) {
      this.containerButtons.add(new TransferAllButton(
          this,
          containerInventory,
          containerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }

    Inventory playerInventory = new SimpleInventory(27);
    Slot playerSlot = new Slot(
        playerInventory,
        8,
        BACKGROUND_WIDTH - 16 - 4,
        BACKGROUND_HEIGHT - 94 + this.textRenderer.fontHeight + 2
    );
    index = 0;

    if (config.showSort.getValue()) {
      this.playerButtons.add(new SortInventoryButton(
          this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showStack.getValue()) {
      this.playerButtons.add(new AutoStackButton(
          this,
          playerInventory,
          playerSlot,
          InventoryButtonsManager.INSTANCE.getButtonPosition(index++, offset),
          true
      ));
    }
    if (config.showTransfer.getValue()) {
      this.playerButtons.add(new TransferAllButton(
          this,
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
    this.refreshButtonPositions();
  }

  @Override
  protected void resetToDefault() {
    super.resetToDefault();
    this.refreshButtonPositions();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.render(drawContext, mouseX, mouseY, partialTicks);

    this.containerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));
    this.playerButtons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));

    drawContext.drawTextWithShadow(
        this.textRenderer,
        Text.literal(this.getValue().toString()),
        GuiUtil.PADDING,
        GuiUtil.PADDING,
        GuiUtil.LABEL_COLOR
    );
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.client == null || this.client.world == null) {
      this.renderPanoramaBackground(context, delta);
    }

    int x = (this.width - BACKGROUND_WIDTH) / 2;
    int y = (this.height - BACKGROUND_HEIGHT) / 2;
    context.drawTexture(
        RenderLayer::getGuiTextured,
        BACKGROUND_TEXTURE,
        x,
        y,
        0,
        0,
        BACKGROUND_WIDTH,
        3 * 18 + 17,
        256,
        256
    );
    context.drawTexture(
        RenderLayer::getGuiTextured,
        BACKGROUND_TEXTURE,
        x,
        y + 3 * 18 + 17,
        0,
        126,
        BACKGROUND_WIDTH,
        96,
        256,
        256
    );

    context.getMatrices().push();
    context.getMatrices().translate(x, y, 0);
    context.drawText(this.textRenderer, Text.translatable("container.chest"), 8, 6, 0x404040, false);
    context.drawText(
        this.textRenderer,
        Text.translatable("container.inventory"),
        8,
        BACKGROUND_HEIGHT - 94,
        0x404040,
        false
    );
    context.getMatrices().pop();

    this.applyBlur();
    this.renderDarkening(context);
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

  private void refreshButtonPositions() {
    Position offset = this.getValue();
    for (int i = 0; i < this.containerButtons.size(); i++) {
      this.containerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, offset));
    }
    for (int i = 0; i < this.playerButtons.size(); i++) {
      this.playerButtons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonPosition(i, offset));
    }
  }
}
