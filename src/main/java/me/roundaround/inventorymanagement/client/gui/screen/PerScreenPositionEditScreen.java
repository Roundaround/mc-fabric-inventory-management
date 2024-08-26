//package me.roundaround.inventorymanagement.client.gui.screen;
//
//import me.roundaround.inventorymanagement.InventoryManagementMod;
//import me.roundaround.inventorymanagement.client.InventoryButtonsManager;
//import me.roundaround.inventorymanagement.client.gui.widget.button.ButtonBase;
//import me.roundaround.inventorymanagement.config.InventoryManagementConfig;
//import me.roundaround.roundalib.client.gui.GuiUtil;
//import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
//import me.roundaround.roundalib.config.option.PositionConfigOption;
//import me.roundaround.roundalib.config.value.Position;
//import net.fabricmc.fabric.api.client.screen.v1.Screens;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;
//
//import java.util.LinkedList;
//
//public class PerScreenPositionEditScreen extends PositionEditScreen {
//  private final LinkedList<ButtonBase<?, ?>> buttons = new LinkedList<>();
//
//  private final Screen previousScreen;
//  private final boolean isPlayerInventory;
//
//  public PerScreenPositionEditScreen(
//      Screen previousScreen, Screen workingScreen, boolean isPlayerInventory) {
//    super(Text.translatable("inventorymanagement.positionEdit.title"),
//        workingScreen,
//        generateDummyConfigOption(workingScreen, isPlayerInventory));
//
//    this.previousScreen = previousScreen;
//    this.isPlayerInventory = isPlayerInventory;
//
//    this.workingCopy.subscribeToValueChanges(this.hashCode(), (oldValue, newValue) -> {
//      for (int i = 0; i < buttons.size(); i++) {
//        buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, newValue));
//      }
//    });
//  }
//
//  private static PositionConfigOption generateDummyConfigOption(
//      Screen parent, boolean isPlayerInventory) {
//    Position currentValue =
//        InventoryManagementMod.CONFIG.perScreenConfigs.getPosition(parent, isPlayerInventory);
//    if (currentValue == null) {
//      currentValue = InventoryManagementMod.CONFIG.defaultPosition.getValue();
//    }
//
//    PositionConfigOption dummyConfig = PositionConfigOption.builder(InventoryManagementMod.CONFIG,
//        InventoryManagementConfig.getScreenKey(parent),
//        "",
//        InventoryManagementMod.CONFIG.defaultPosition.getValue()).build();
//    dummyConfig.setValue(currentValue);
//    return dummyConfig;
//  }
//
//  @Override
//  protected void init() {
//    super.init();
//
//    buttons.addAll(isPlayerInventory
//        ? InventoryButtonsManager.INSTANCE.getPlayerButtons()
//        : InventoryButtonsManager.INSTANCE.getContainerButtons());
//
//    Screens.getButtons(parent).removeIf((button) -> button instanceof ButtonBase);
//
//    for (int i = 0; i < buttons.size(); i++) {
//      buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, getValue()));
//    }
//  }
//
//  @Override
//  protected void setValue(Position value) {
//    super.setValue(value);
//
//    for (int i = 0; i < buttons.size(); i++) {
//      buttons.get(i).setOffset(InventoryButtonsManager.INSTANCE.getButtonOffset(i, getValue()));
//    }
//  }
//
//  @Override
//  public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
//    MatrixStack matrixStack = drawContext.getMatrices();
//    matrixStack.push();
//    matrixStack.translate(0, 0, -51);
//    parent.render(drawContext, mouseX, mouseY, partialTicks);
//    matrixStack.pop();
//
//    super.render(drawContext, mouseX, mouseY, partialTicks);
//  }
//
//  @Override
//  public void renderBackground(
//      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
//    renderDarkenBackground(drawContext, mouseX, mouseY, partialTicks);
//  }
//
//  @Override
//  protected void renderContent(
//      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
//    super.renderContent(drawContext, mouseX, mouseY, partialTicks);
//
//    buttons.forEach((button) -> button.render(drawContext, mouseX, mouseY, partialTicks));
//
//    drawContext.drawTextWithShadow(textRenderer,
//        Text.literal(getValue().toString()),
//        4,
//        4,
//        GuiUtil.LABEL_COLOR);
//  }
//
//  @Override
//  protected void commitValueToConfig() {
//    if (isDirty()) {
//      if (getValue() == this.configOption.getDefault()) {
//        InventoryManagementMod.CONFIG.perScreenConfigs.clearPosition(this.parent,
//            this.isPlayerInventory);
//      } else {
//        InventoryManagementMod.CONFIG.perScreenConfigs.setPosition(this.parent,
//            this.isPlayerInventory,
//            getValue());
//      }
//      InventoryManagementMod.CONFIG.saveToFile();
//    }
//  }
//
//  @Override
//  public void close() {
//    if (this.client == null) {
//      return;
//    }
//    this.client.setScreen(this.previousScreen);
//  }
//}
